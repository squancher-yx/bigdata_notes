package flink;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class KafkaTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties kafkasource = new Properties();
        kafkasource.put("bootstrap.servers", "127.0.0.1:9092");
        kafkasource.put("group.id", "testGroup");
        kafkasource.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkasource.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<String>("quickstart-events", new SimpleStringSchema(), kafkasource);
        consumer.setStartFromTimestamp(1613792655851L);
//        consumer.setStartFromGroupOffsets();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(
                3, // 尝试重启的次数
                Time.of(10, TimeUnit.SECONDS) // 延时
        ));
        env.enableCheckpointing(1000);
        DataStreamSource<String> stream = env.addSource(consumer);
        DataStream<String> s1 = stream.map(x -> {
//            Thread.sleep(1000);
            return x;
        });
        s1.print();
        env.execute("wer");
    }
}
