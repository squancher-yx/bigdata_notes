package flink;


import java.text.ParseException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;


public class FunTest {
    public static void main(String[] args) throws ParseException, ExecutionException, InterruptedException {
        CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    System.out.println("print getget");
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "get";
            }
        }).thenApplyAsync((String dbResult) -> {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            System.out.println("thenthen:"+dbResult);
            return null;
        });

//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
class FanXingTest{
    static int a=0;

    public static void set(int a){


    }
}
