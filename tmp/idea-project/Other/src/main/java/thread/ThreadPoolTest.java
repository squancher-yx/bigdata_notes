package thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * java.uitl.concurrent.ThreadPoolExecutor类是线程池中最核心的一个类
 * 构造器中各个参数的含义：
 * corePoolSize：核心池的大小，这个参数跟后面讲述的线程池的实现原理有非常大的关系。在创建了线程池后，默认情况下，线程池中并没有任何线程，而是等待有任务到来才创建线程去执行任务，除非调用了prestartAllCoreThreads()或者prestartCoreThread()方法，从这2个方法的名字就可以看出，是预创建线程的意思，即在没有任务到来之前就创建corePoolSize个线程或者一个线程。默认情况下，在创建了线程池后，线程池中的线程数为0，当有任务来之后，就会创建一个线程去执行任务，当线程池中的线程数目达到corePoolSize后，就会把到达的任务放到缓存队列当中；
 *
 * maximumPoolSize：线程池最大线程数，这个参数也是一个非常重要的参数，它表示在线程池中最多能创建多少个线程；
 *
 * keepAliveTime：表示线程没有任务执行时最多保持多久时间会终止。默认情况下，只有当线程池中的线程数大于corePoolSize时，keepAliveTime才会起作用，直到线程池中的线程数不大于corePoolSize，即当线程池中的线程数大于corePoolSize时，如果一个线程空闲的时间达到keepAliveTime，则会终止，直到线程池中的线程数不超过corePoolSize。但是如果调用了allowCoreThreadTimeOut(boolean)方法，在线程池中的线程数不大于corePoolSize时，keepAliveTime参数也会起作用，直到线程池中的线程数为0；
 *
 * unit：参数keepAliveTime的时间单位，有7种取值，在TimeUnit类中有7种静态属性；
 * TimeUnit.DAYS;               //天
 * TimeUnit.HOURS;             //小时
 * TimeUnit.MINUTES;           //分钟
 * TimeUnit.SECONDS;           //秒
 * TimeUnit.MILLISECONDS;      //毫秒
 * TimeUnit.MICROSECONDS;      //微妙
 * TimeUnit.NANOSECONDS;       //纳秒
 *
 * workQueue：一任务缓存队列及排队策略。个阻塞队列，用来存储等待执行的任务，这个参数的选择也很重要，会对线程池的运行过程产生重大影响，一般来说，这里的阻塞队列有以下几种选择：
 * ArrayBlockingQueue和PriorityBlockingQueue使用较少，一般使用LinkedBlockingQueue和Synchronous。线程池的排队策略与BlockingQueue有关。
 * 1）ArrayBlockingQueue：基于数组的先进先出队列，此队列创建时必须指定大小；
 * 2）LinkedBlockingQueue：基于链表的先进先出队列，如果创建时没有指定此队列大小，则默认为Integer.MAX_VALUE；
 * 3）synchronousQueue：这个队列比较特殊，它不会保存提交的任务，而是将直接新建一个线程来执行新来的任务。
 *
 * 当线程池的任务缓存队列已满并且线程池中的线程数目达到maximumPoolSize，如果还有任务到来就会采取任务拒绝策略，通常有以下四种策略：
 * ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException异常。
 * ThreadPoolExecutor.DiscardPolicy：也是丢弃任务，但是不抛出异常。
 * ThreadPoolExecutor.DiscardOldestPolicy：丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
 * ThreadPoolExecutor.CallerRunsPolicy：由调用线程处理该任务
 *
 * 线程池的关闭
 * shutdown()：不会立即终止线程池，而是要等所有任务缓存队列中的任务都执行完后才终止，但再也不会接受新的任务
 * shutdownNow()：立即终止线程池，并尝试打断正在执行的任务，并且清空任务缓存队列，返回尚未执行的任务
 *
 * 一般使用Executors类中提供的几个静态方法来创建线程池,实际上也是调用了ThreadPoolExecutor
 *
 * newFixedThreadPool 创建的线程池corePoolSize和maximumPoolSize值是相等的，它使用的LinkedBlockingQueue；
 * newSingleThreadExecutor 将corePoolSize和maximumPoolSize都设置为1，也使用的LinkedBlockingQueue；
 * newCachedThreadPool 将corePoolSize设置为0，将maximumPoolSize设置为Integer.MAX_VALUE，使用的SynchronousQueue，也就是说来了任务就创建线程运行，当线程空闲超过60秒，就销毁线程。
 * newScheduledThreadPool 计划时间执行
 *
 * |------------------------------------------------------------------------------------------------|
 * |method                 | corePoolSize |  maximumPoolSize  | keepAliveTime | workQueue           |
 * |------------------------------------------------------------------------------------------------|
 * |newSingleThreadExecutor|1             | 1                 |0              | LinkedBlockingQueue |
 * |------------------------------------------------------------------------------------------------|
 * |newCachedThreadPool    |0             | Integer.MAX_VALUE |60s            | SynchronousQueue    |
 * |------------------------------------------------------------------------------------------------|
 * |newScheduledThreadPool |corePoolSize  | Integer.MAX_VALUE |0              | DelayedWorkQueue    |
 * |------------------------------------------------------------------------------------------------|
 * |newFixedThreadPool     |nThreads      | nThreads          |0              | LinkedBlockingQueue |
 * |------------------------------------------------------------------------------------------------|
 *
 * 排队有三种通用策略：
 * 直接提交。工作队列的默认选项是 SynchronousQueue，它将任务直接提交给线程而不保持它们(SynchronousQueue作为阻塞队列的时候，对于每一个take的线程会阻塞直到有一个put的线程放入元素为止，反之亦然。在SynchronousQueue内部没有任何存放元素的能力)
 * 无界队列。使用无界队列（例如，不具有预定义容量的 LinkedBlockingQueue）将导致在所 有 corePoolSize 线程都忙时新任务在队列中等待。这样，创建的线程就不会超过 corePoolSize。
 * 有界队列(需要直接使用 ThreadPoolExecutor 创建)。当使用有限的 maximumPoolSizes 时，有界队列 （如 ArrayBlockingQueue）有助于防止资源耗尽，但是可能较难调整和控制。
 *
 * execute()和submit()方法
 * 1、execute()，执行一个任务，没有返回值。
 *
 * 2、submit()，提交一个线程任务，有返回值，使用 get 获取。
 *  1）submit(Runnable task)，因为Runnable是没有返回值的，所以如果submit一个 Runnable 的话，get 得到的为 null。
 *      （1）执行 execute 之前，new 了一个 FutureTask，将自定义的 Runnable 封装到 FutureTask 内部（先使用 Executors.callable(runnable, result)返回 RunnableAdapter，其中 RunnableAdapter<T> implements Callable<T>，并调用了 Runnable 的 run 方法）
 *      （2）FutureTask 实现接口 RunnableFuture（其中 RunnableFuture<V> extends Runnable, Future<V>），实现了 run、get等方法，然后 run 方法中调用（1）里面封装的的 callable 的 call。
 *  2）submit(Runnable task, T result)，通过submit(Runnable task, T result)传入一个载体，通过这个载体获取返回值。和 submit(Runnable task)类似
 *  3）submit(Callable<T> task)，同上，但不封装 task 了。
 */
public class ThreadPoolTest {
    public static void main(String[] args) {
        ExecutorService tmp = Executors.newScheduledThreadPool(1);
        Executors.newFixedThreadPool(1);
        Executors.newFixedThreadPool(1).submit(new Runnable() {
            @Override
            public void run() {

            }
        },1);
//        Executors.newScheduledThreadPool()
    }
}
