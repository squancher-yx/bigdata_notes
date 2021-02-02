package com.yx.flink;

import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import scala.Tuple2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

public class Demo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //设置水位线自动提交时间
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setAutoWatermarkInterval(1000);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("group.id", "test");

        DataStream<String> stream = env
                .addSource(new FlinkKafkaConsumer<>("quickstart-events", new SimpleStringSchema(), properties).setStartFromLatest())
                .setParallelism(1).assignTimestampsAndWatermarks(new MyWatermarkStrategy().withTimestampAssigner(new SerializableTimestampAssigner<String>() {
                    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    @Override
                    public long extractTimestamp(String element, long recordTimestamp) {
                        String[] line = element.split("\t", -1);
                        String time = line[0];
                        Date date = null;
                        try {
                            date = simpleDateFormat.parse(time);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long ts = date.getTime();
                        return Long.parseLong(ts + "");
                    }
                }));

//        WindowedStream<String, String, TimeWindow> windowedStream =
                stream.filter(new FilterFunction<String>() {
            //过滤长度不符的数据
            @Override
            public boolean filter(String value) throws Exception {
                return value.split("\t", -1).length == 3;
            }
        })
                .keyBy(new KeySelector<String, String>() {
                    @Override
                    public String getKey(String value) throws Exception {
                        String[] line = value.split("\t", -1);
                        return line[1];
                    }
                })
                .timeWindow(Time.seconds(5)).trigger(new Trigger<String, TimeWindow>() {
                    @Override
                    public TriggerResult onElement(String element, long timestamp, TimeWindow window, TriggerContext ctx) throws Exception {
                        System.out.println("element  "+timestamp+"  "+window.maxTimestamp()+"   "+ctx.getCurrentWatermark()+"  "+Thread.currentThread().getName());
                        if (window.maxTimestamp() <= ctx.getCurrentWatermark()) {
                            // if the watermark is already past the window fire immediately
                            return TriggerResult.FIRE;
                        } else {
                            ctx.registerEventTimeTimer(window.maxTimestamp());
                            ctx.registerEventTimeTimer(window.maxTimestamp());
                            return TriggerResult.CONTINUE;
                        }
                    }

                    @Override
                    public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
                        return TriggerResult.CONTINUE;
                    }

                    @Override
                    public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
                        System.out.println("eventtime  "+"  "+time+"   "+window.maxTimestamp());
                        return time == window.maxTimestamp() ?
                                TriggerResult.FIRE :
                                TriggerResult.CONTINUE;
                    }

                    @Override
                    public void clear(TimeWindow window, TriggerContext ctx) throws Exception {
                        ctx.deleteEventTimeTimer(window.maxTimestamp());
                    }
                })
                .process(new ProcessWindowFunction<String, Object, String, TimeWindow>() {

                    @Override
                    public void process(String s, Context context, Iterable<String> elements, Collector<Object> out) throws Exception {
//                        context.globalState().

                        for (String str : elements) {
                            System.out.println(str);
                        }
                    }
                }).print();
//        stream.addSink(new RichSinkFunction<String>() {
//        });


        env.execute("test");
    }
}

class MyWatermarkStrategy implements WatermarkStrategy<String> {

    @Override
    public WatermarkGenerator<String> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context) {
        return new WatermarkGenerator<String>() {
            private long watermark = 0;
            //允许延迟
            private final long delay = 1000;
            private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //作用每条数据
            @Override
            public void onEvent(String s, long l, WatermarkOutput watermarkOutput) {
                String[] line = s.split("\t", -1);
                try {
                    String time = line[0];
                    Date date = simpleDateFormat.parse(time);
                    long ts = date.getTime();
                    //更新水位線
                    this.watermark = Math.max(watermark, ts);
//                    System.out.println(watermark);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //作用于setAutoWatermarkInterval，向下游发送watermarks
            @Override
            public void onPeriodicEmit(WatermarkOutput watermarkOutput) {
//                System.out.println(watermark+"    "+Thread.currentThread().getName());
                watermarkOutput.emitWatermark(new Watermark(watermark));
            }
        };
    }
}

//value1:2020-12-23 01:02:10	3	13
//value2:2020-12-23 01:02:13	3	13
//value1:2020-12-23 01:02:15	3	13
//value2:2020-12-23 01:02:17	3	13
//4> 2020-12-23 01:02:13	3	26
//value1:2020-12-23 01:02:17	3	26
//value2:2020-12-23 01:02:19	3	13
//value1:2020-12-23 01:02:20	3	13
//value2:2020-12-23 01:02:21	3	13
//4> 2020-12-23 01:02:19	3	39
//value1:2020-12-23 01:02:25	3	13
//value2:2020-12-23 01:02:27	3	13
//4> 2020-12-23 01:02:21	3	26
//4> 2020-12-23 01:02:27	3	26
//4> 2020-12-23 01:02:30	3	13

