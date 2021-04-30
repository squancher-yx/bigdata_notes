package thread;

import java.util.concurrent.*;
import java.util.function.*;

/**
 * 1.异步执行：
 * runAsync 创建无返回值的异步任务
 * supplyAsync 创建带返回值的异步任务
 * <p>
 * 3.异步回调：某个任务执行完成后执行的动作，即回调方法(创建了一个新的CompletableFuture实例)
 * thenApply(接收上一步的返回值作为参数，有返回值；和上一步的 1 使用同一个线程池的同一个线程，可链式执行)(不太确定)
 * thenApplyAsync(可指定线程池，可链式执行，有返回值)(不太确定)
 * thenAccept (同 thenApply，无返回值)
 * thenAcceptAsync (同 thenApplyAsync 无返回值)
 * whenComplete (类似 thenAccept，但上一步异常也会执行)
 * whenCompleteAsync (类似 thenAcceptAsync，但上一步异常也会执行)
 * handle (类似 whenComplete，有返回值，如果有异常可获取到异常)
 * handleAsync (类似 whenCompleteAsync，无返回值，如果有异常可获取到异常)
 *
 * cancel(boolean mayInterruptIfRunning)
 * mayInterruptIfRunning = false 如果已经开始运行，则会等待结束，如果未开始运行，则不会运行。
 * mayInterruptIfRunning = true 如果已经开始运行，会尝试使用 interrupt 中断，如果未开始运行，则不会运行。
 *
 */

public class CompletableFutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        thenAndThen();
        exception();
    }



    public static void exception(){
        //不同线程池
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinPool pool2 = new ForkJoinPool();
        ExecutorService pool3 = Executors.newScheduledThreadPool(1);

        CompletableFuture<String> res = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
//                System.out.println(1/0);
                if(true){
                    throw new NullPointerException("test Exception");
                }
                try {
                    System.out.println("print getget");
                    System.out.println(Thread.currentThread());
//                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "get";
            }
        }, pool);


        // res 异常后不执行
//        res.thenAcceptAsync(new Consumer<String>() {
//            @Override
//            public void accept(String s) {
//                System.out.println("执行");
//            }
//        }, pool3);

        // res 异常后会执行
//        res.whenComplete(new BiConsumer<String, Throwable>() {
//            @Override
//            public void accept(String s, Throwable throwable) {
//                System.out.println("执行");
//            }
//        });

        CompletableFuture<String> res2 = res.handle(new BiFunction<String, Throwable, String>() {
            @Override
            public String apply(String s, Throwable throwable) {
                System.out.println(throwable);
                return "handle";
            }
        });

        try {
            System.out.println(res2.get());
//            System.out.println(res.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void thenAndThen() {
        //不同线程池
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinPool pool2 = new ForkJoinPool();
        ExecutorService pool3 = Executors.newScheduledThreadPool(1);

        CompletableFuture<String> res = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    System.out.println("print getget");
                    System.out.println(Thread.currentThread());
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "get";
            }
        }, pool).thenAcceptAsync(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(Thread.currentThread());
            }
        }, pool3).thenApplyAsync(new Function<Object, String>() {
            @Override
            public String apply(Object o) {
                System.out.println(Thread.currentThread());
                return "a2";
            }
        }, pool3);

        try {
            System.out.println(res.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
