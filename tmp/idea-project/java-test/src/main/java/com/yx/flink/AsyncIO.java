package com.yx.flink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

//需要客户端支持异步查询，或者自己使用线程池实现多个客户端连接
public class AsyncIO {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStream<String> ds = env.socketTextStream("127.0.0.1", 8888);
        AsyncDataStream.unorderedWait(ds, new MyAsyncFunction(), 10000000, TimeUnit.MILLISECONDS, 10)
                .print();
        env.execute("test");
    }
}

class MyAsyncFunction extends RichAsyncFunction<String, String> {
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public void asyncInvoke(String s, ResultFuture<String> resultFuture) throws Exception {

//        CompletableFuture.supplyAsync(new Supplier<String>() {
//            @Override
//            public String get() {
//                try {
//                    if (s.equals("qqq"))
//                        Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return s;
//            }
//        }).thenAccept((String dbResult) -> {
//            resultFuture.complete(Collections.singleton(dbResult));
//        });
        new Thread(
                () -> {
                    try {
                        // 模拟一段耗时的操作
                        if(s.equals("qqq"))
                        Thread.sleep(10000);
                        resultFuture.complete(Collections.singleton(s));
                    } catch (Exception ignored) {
                    }
                }
        ).start();

    }
}
