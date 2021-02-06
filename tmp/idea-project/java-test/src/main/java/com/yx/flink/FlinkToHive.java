package com.yx.flink;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;
import org.apache.flink.table.expressions.Expression;
import scala.Tuple3;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.apache.flink.table.api.Expressions.$;

public class FlinkToHive {
    public static void main(String[] args) {
        String kafkaurl = "127.0.0.1:9092";
        StreamExecutionEnvironment streamEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        streamEnv.setRestartStrategy(RestartStrategies.fixedDelayRestart(
                3, // 尝试重启的次数
                Time.of(10, TimeUnit.SECONDS) // 延时
        ));
        System.setProperty("HADOOP_USER_NAME","hadoop");
        System.setProperty("HADOOP_USER_PASSWORD","hadoopadmin");
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(streamEnv,settings);
        tableEnv.getConfig().getConfiguration().set(ExecutionCheckpointingOptions.CHECKPOINTING_MODE, CheckpointingMode.EXACTLY_ONCE);
        //tableEnv.getConfig().getConfiguration().set(ExecutionCheckpointingOptions.CHECKPOINTING_INTERVAL, Duration.ofSeconds(120));
        HiveCatalog catalog = new HiveCatalog("test_catlog","default_databases","hive-site.xml path");
        tableEnv.registerCatalog("test_catlog",catalog);
        tableEnv.useCatalog("test_catlog");
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "127.0.0.1:9092");
        properties.put("group.id", "testGroup");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<String>("test_topic",new SimpleStringSchema(),properties);
        consumer.setStartFromLatest();
        //需要使用 flink Tuple，不能使用lambda，需要使用匿名类
        DataStream<Tuple3<String,String,String>> source= streamEnv.addSource(consumer).map(f->{
            String[] line = f.split(" ");
            return new Tuple3<>(line[0], line[1], line[2]);
        });
        tableEnv.createTemporaryView("view" , source,$(""),$(""));
        StatementSet statementSet = tableEnv.createStatementSet();
        statementSet.addInsertSql("insert into xx (select * fron xx)");
        statementSet.addInsertSql("insert into xxx (select * fron xxx)");
        statementSet.execute();
    }
}
