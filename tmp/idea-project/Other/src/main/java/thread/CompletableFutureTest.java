package thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 *  runAsync 
 *  supplyAsync
 */

public class CompletableFutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> res= CompletableFuture.supplyAsync(new Supplier<String>() {
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
            System.out.println("thenthen:"+dbResult);
            return null;
        });

        res.get();
    }
}
