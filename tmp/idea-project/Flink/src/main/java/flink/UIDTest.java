package flink;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


/**
 * UID 适用于checkpoint
 */
public class UIDTest {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream dataStream = env.socketTextStream("127.0.0.1",8888).keyBy(new KeySelector<String, String>() {
            @Override
            public String getKey(String s) throws Exception {
                return s;
            }
        }).map(new MapFunction<String, Object>() {
            @Override
            public Object map(String s) throws Exception {
                return null;
            }
        }).uid("999");
        System.out.println(env.getExecutionPlan());
    }
}
