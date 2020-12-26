package com.yx.flink;

import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Demo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env =StreamExecutionEnvironment.getExecutionEnvironment();
        //设置水位线自动提交时间
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setAutoWatermarkInterval(10000);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("group.id", "test");

        DataStream<String> stream = env
                .addSource(new FlinkKafkaConsumer<>("quickstart-events", new SimpleStringSchema(), properties).setStartFromLatest())
                .assignTimestampsAndWatermarks(new MyWatermarkStrategy().withTimestampAssigner(new SerializableTimestampAssigner<String>() {
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
                        return Long.parseLong(ts+"");
                    }
                }));


        WindowedStream<String,String, TimeWindow> windowedStream = stream.filter(new FilterFunction<String>() {
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
                }).timeWindow(Time.seconds(60));
//        stream.addSink(new RichSinkFunction<String>() {
//        });

        //前一分钟数据
        windowedStream.reduce(new ReduceFunction<String>() {
            @Override
            public String reduce(String value1, String value2) throws Exception {
                String[] line1 = value1.split("\t", -1);
                String[] line2 = value2.split("\t", -1);
//                System.out.println("value1:"+value1);
//                System.out.println("value2:"+value2);
                String key =line2[0]+"\t"+line2[1];
                return key+"\t"+(Integer.parseInt(line1[2])+Integer.parseInt(line2[2]));
            }
        }).print();

        //分组总和
        stream.keyBy(new KeySelector<String, String>() {
            @Override
            public String getKey(String value) throws Exception {
                String[] line = value.split("\t", -1);
                return line[1];
            }
        }).reduce(new ReduceFunction<String>() {
            @Override
            public String reduce(String value1, String value2) throws Exception {
                String[] line1 = value1.split("\t", -1);
                String[] line2 = value2.split("\t", -1);
//                System.out.println("value1:"+value1);
//                System.out.println("value2:"+value2);
                String key =line2[0]+"\t"+line2[1];
                return key+"\t"+(Integer.parseInt(line1[2])+Integer.parseInt(line2[2]));
            }
        }).print();

        //总和
        stream.keyBy(new KeySelector<String, Object>() {
            @Override
            public Object getKey(String value) throws Exception {
                return 1;
            }
        }).reduce(new ReduceFunction<String>() {
            @Override
            public String reduce(String value1, String value2) throws Exception {
                String[] line1 = value1.split("\t", -1);
                String[] line2 = value2.split("\t", -1);
//                System.out.println("value1:"+value1);
//                System.out.println("value2:"+value2);
                String key =line2[0]+"\t"+"total";
                return key+"\t"+(Integer.parseInt(line1[2])+Integer.parseInt(line2[2]));
            }
        }).print();

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
//                System.out.println(watermark);
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

