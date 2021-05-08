package flink;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;

import java.text.ParseException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;


public class FunTest {
    public static void main(String[] args) throws ParseException, ExecutionException, InterruptedException {
        String topic = "Hello-Kafka";
        String group = "group1";
        Properties props = new Properties();
        props.put("bootstrap.servers", "hadoop001:9092");
        /*指定分组 ID*/
        props.put("group.id", group);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.offsetsForTimes().subscribe(Collections.singletonList(topic));
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
                Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(record);
                    /*记录每个主题的每个分区的偏移量*/
                    TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());

                    OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(record.offset()+1, "no metaData");
                    /*TopicPartition 重写过 hashCode 和 equals 方法，所以能够保证同一主题和分区的实例不会被重复添加*/
                    offsets.put(topicPartition, offsetAndMetadata);
                }

                /*提交特定偏移量*/
                consumer.commitAsync(offsets, null);
                consumer.commitAsync();
                List<TopicPartition> partitions = new ArrayList<>();
                for(TopicPartition seekTopic:partitions){
                    consumer.seek(seekTopic,0L);
                }

            }
        } finally {
            consumer.close();
        }

    }
}

