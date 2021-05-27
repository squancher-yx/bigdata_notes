package kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class KafkaProducerTest {
    public static void main(String[] args) {
        String topicName = "quickstart-events";

        Properties props = new Properties();
        props.put("bootstrap.servers", "127.0.0.1:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        /*创建生产者*/
        Producer<String, String> producer = new KafkaProducer<>(props);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] logs = new String[]{"d_active_m2sw_5.2", "d_active_mx_5.2", "d_active_m2mx_5.2", "usr_info_m2sw_5.2"};
        String[] platforms = new String[]{"1", "2", "3"};
        Random random = new Random(10);
        for (int i = 0; i < 1000000000; i++) {
            String ftime = df.format(new Date());
            int server = random.nextInt(100000);
            String logname = logs[random.nextInt(4)];
            int title = random.nextInt(3) + 1;
            int info_version = random.nextInt(99);
            int uid = random.nextInt(10000000);
            int role_id = random.nextInt(10000000);
            int stats_obj = 1;
            String login_platform = platforms[random.nextInt(3)];
            String ip = random.nextInt(255) + 1 + "." + random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255);
            String stats_location = "99,66";
            String stats_rel = "";
            String opt_result = "";
            String stats_col = "test";

            ProducerRecord<String, String> record = new ProducerRecord<>(topicName,
                    ftime + "\t" + server + "\t" + logname + "\t" + title + "\t" + info_version + "\t" + uid + "\t" + role_id + "\t" + stats_obj + "\t" +
                            login_platform + "\t" + ip + "\t" + stats_location + "\t" + stats_rel + "\t" + opt_result + "\t" + stats_col);
            /* 发送消息*/
            producer.send(record);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        /*关闭生产者*/
        producer.close();
    }
}
