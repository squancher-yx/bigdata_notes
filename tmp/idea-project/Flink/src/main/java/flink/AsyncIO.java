package flink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;

import java.io.Serializable;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

//需要客户端支持异步查询，或者自己使用线程池实现多个客户端连接
public class AsyncIO {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStream<String> ds = env.socketTextStream("127.0.0.1", 8888).setParallelism(1);
        // capacity 为最大异步时等待队列
        AsyncDataStream.unorderedWait(ds, new MyAsyncFunction(), 100000, TimeUnit.MILLISECONDS, 2)
                .print();
        env.execute("test");
    }
}

class MyAsyncFunction extends RichAsyncFunction<String, String> {
    TestClient client = new TestClient();
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
        System.out.println(Thread.currentThread().getName());

//1. CompletableFuture.supplyAsync 相当于新启一个线程执行，然后回调。client 不支持并发的时候无效(TestClient 去掉 synchronized 可行)。
        CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println(client.hashCode());
                return client.get(s);
            }
        }).thenAccept((String dbResult) -> {
            resultFuture.complete(Collections.singleton(dbResult));
        });

//        resultFuture.complete(Collections.singleton(client.get(s)));

//2.直接多线程模拟并发，可行
//        new Thread(
//                () -> {
//                    try {
//                        // 模拟一段耗时的操作
////                        System.out.println(Thread.currentThread().getName());
//                        if (s.equals("qqq"))
//                            Thread.sleep(10000);
//                        resultFuture.complete(Collections.singleton(s));
//                    } catch (Exception ignored) {
//                    }
//                }
//        ).start();

        System.out.println("end:"+Thread.currentThread().getName());

    }
}

class TestClient implements Serializable {
    public synchronized String get(String str) {
        try {
            if (str.equals("qqq"))
                System.out.println(Thread.currentThread().getName());
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return str;
    }
}