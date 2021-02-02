package com.yx.flink;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.formats.compress.CompressWriterFactory;
import org.apache.flink.formats.compress.extractor.DefaultExtractor;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.BucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.SimpleVersionedStringSerializer;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.OnCheckpointRollingPolicy;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

//https://ci.apache.org/projects/flink/flink-docs-release-1.12/zh/dev/connectors/streamfile_sink.html

public class StreamingRollingFileSink {
    public static void main(String[] args) throws IOException {
        Properties kafkasource = new Properties();
        kafkasource.put("bootstrap.servers", "127.0.0.1:9092");
        kafkasource.put("group.id", "testGroup");
        kafkasource.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkasource.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<String>("test_topic", new SimpleStringSchema(), kafkasource);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(
                3, // 尝试重启的次数
                Time.of(10, TimeUnit.SECONDS) // 延时
        ));
        env.enableCheckpointing(1000 * 60 * 5);
        //checkpoint间隔至少2分钟
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(1000 * 60 * 2);
        FsStateBackend state = new FsStateBackend("hdfs:///");
        env.setStateBackend((StateBackend) state);
        //失败清楚文件策略
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        DataStreamSource<String> stream = env.addSource(consumer);
        DataStream<String> s1 = stream.map(x -> x);

        //Hadoop < 2.7 时，请使用 OnCheckpointRollingPolicy 滚动策略
        /*
        final StreamingFileSink<String> sink = StreamingFileSink
                .forRowFormat(new Path("hdfs:///"), new SimpleStringEncoder<String>("UTF-8"))
                .withRollingPolicy(
                        DefaultRollingPolicy.builder()
                                .withRolloverInterval(TimeUnit.MINUTES.toMillis(15))
                                .withInactivityInterval(TimeUnit.MINUTES.toMillis(5))
                                .withMaxPartSize(1024 * 1024 * 1024)
                                .build())
                .build();*/
        OutputFileConfig config = OutputFileConfig
                .builder()
                .withPartPrefix("prefix")
                .withPartSuffix(".lzo")
                .build();
        CompressWriterFactory<String> compressWriterFactory = new CompressWriterFactory<String>(new DefaultExtractor<>())
                .withHadoopCompression("com.hadoop.compression.lzo.lzopCodec");
        StreamingFileSink<String> sink = StreamingFileSink.forBulkFormat(new Path("hdfs:///"), compressWriterFactory)
                .withBucketAssigner(new MyBucketAssigner())
                .withOutputFileConfig(config)
                .withRollingPolicy(OnCheckpointRollingPolicy.build())
                .build();
        s1.print();
        s1.addSink(sink);
    }
}

class MyBucketAssigner implements BucketAssigner<String, String> {

    @Override
    public String getBucketId(String element, Context context) {
        String[] line = element.split(" ");
        return line[0] + "/" + line[1];
    }

    @Override
    public SimpleVersionedSerializer<String> getSerializer() {
        return SimpleVersionedStringSerializer.INSTANCE;
    }
}