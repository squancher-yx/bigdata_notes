package flink;

import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.OutputTag;

public class WaterMarkTest {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> ds = env.socketTextStream("127.0.0.1", 8888);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        //onPeriodicEmit 发送间隔
        env.getConfig().setAutoWatermarkInterval(100000);
        //侧输出
        OutputTag<String> output = new OutputTag<String>("test") {
        };
        DataStream<String> tmp = ds.assignTimestampsAndWatermarks(((WatermarkStrategy<String>) context -> new WatermarkGenerator<String>() {
            private long watermark = 0;
            private long delay = 0;

            @Override
            public void onEvent(String s, long l, WatermarkOutput watermarkOutput) {
                watermark = Math.max(watermark, Long.parseLong(s));
            }

            //间隔发送
            @Override
            public void onPeriodicEmit(WatermarkOutput watermarkOutput) {
                watermarkOutput.emitWatermark(new Watermark(watermark));
            }
        }).withTimestampAssigner((SerializableTimestampAssigner<String>) (s, l) -> Long.parseLong(s)));

        SingleOutputStreamOperator<String> tmp2 = tmp.windowAll(TumblingEventTimeWindows.of(Time.seconds(10)))
                .sideOutputLateData(output)
                .apply((AllWindowFunction<String, String, TimeWindow>) (timeWindow, iterable, collector) -> {
                    for (String value : iterable) {
                        collector.collect(value);
                    }
                });
        tmp2.getSideOutput(output).print();
        tmp2.print();
    }
}
