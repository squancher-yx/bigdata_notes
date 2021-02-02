package com.yx.flink;

import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;

public class WindowDelayTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setAutoWatermarkInterval(1000);
        DataStream<String> stream = env.socketTextStream("127.0.0.1",8888);
        final OutputTag<String> lateOutputTag = new OutputTag<String>("late-data"){};
        SingleOutputStreamOperator<String> tmp = stream.assignTimestampsAndWatermarks(new WatermarkStrategy<String>() {
            @Override
            public WatermarkGenerator<String> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context) {
                return new WatermarkGenerator<String>() {
                    private long watermark = 0;
                    private final long delay = 1000;
                    @Override
                    public void onEvent(String event, long eventTimestamp, WatermarkOutput output) {
                        watermark = Math.max(Integer.parseInt(event),watermark);
                        output.emitWatermark(new Watermark(watermark));
//                        System.out.println(watermark);
                    }

                    @Override
                    public void onPeriodicEmit(WatermarkOutput output) {
                        output.emitWatermark(new Watermark(watermark));
                    }
                };
            }

            @Override
            public TimestampAssigner<String> createTimestampAssigner(TimestampAssignerSupplier.Context context) {
                return (SerializableTimestampAssigner<String>) (element, recordTimestamp) -> Integer.parseInt(element);
            }
        }).setParallelism(1).keyBy((KeySelector<String, Object>) value -> value)
                .window(SlidingEventTimeWindows.of(Time.seconds(10),Time.seconds(5)))
                .allowedLateness(Time.seconds(3)).sideOutputLateData(lateOutputTag)
                .reduce((ReduceFunction<String>) (value1, value2) -> {
                    System.out.println(Thread.currentThread().getName()+"a:"+Integer.parseInt(value1));
                    System.out.println(Thread.currentThread().getName()+"b:"+Integer.parseInt(value2));
                    return Integer.parseInt(value1)+Integer.parseInt(value2)+"";
                });
        tmp.print();


        env.execute("test");
    }
}
