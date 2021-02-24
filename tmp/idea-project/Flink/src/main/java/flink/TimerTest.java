package flink;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

//对TimerService每个键和时间戳进行重复数据删除计时器，即每个键和时间戳最多有一个计时器。如果为同一时间戳注册了多个计时器，则该onTimer()方法将仅被调用一次。
//如果要访问键控状态和计时器，则必须ProcessFunction在键控流上应用
public class TimerTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.socketTextStream("127.0.0.1",8888).keyBy(new KeySelector<String, String>() {
            @Override
            public String getKey(String s) throws Exception {
                return s;
            }
        }).process(new ProcessFunction<String, String>() {
            @Override
            public void processElement(String s, Context context, Collector<String> collector) throws Exception {
                long timestamp = System.currentTimeMillis();
                System.out.println(timestamp+"processElement:"+s);
                collector.collect(timestamp+"processElement:"+s);
                context.timerService().registerProcessingTimeTimer(timestamp + 6000);
                context.timerService().registerProcessingTimeTimer(timestamp + 6000);
            }

            
            @Override
            public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
                System.out.println("onTimer:"+timestamp);
                out.collect("onTimer:"+timestamp);
            }
        }).print();
        env.execute("test");
    }
}
