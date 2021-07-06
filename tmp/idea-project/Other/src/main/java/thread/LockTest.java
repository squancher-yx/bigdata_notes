package thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * await 实现原理
 * 1. 将当前线程包装成Node，尾插法插入到等待队列中
 * 2. 释放当前线程所占用的lock，在释放的过程中会唤醒同步队列中的下一个节点
 * 4. 自旋等待获取到同步状态（即获取到lock）
 * 5. 处理被中断的情况
 * （详见父类AQS）
 * 当前线程调用condition.await()方法后，会使得当前线程释放lock然后加入到等待队列中，
 * 直至被signal/signalAll(唤醒线程，不释放锁)后会使得当前线程从等待队列中移至到同步队列中去，
 * 直到获得了lock后才会从await方法返回，或者在等待时被中断会做中断处理。
 */
public class LockTest {
    public static void main(String[] args) throws InterruptedException {
        SyncClass myThread = new SyncClass();
        Thread thread2 = new Thread(new RunTest(myThread, false,1,1));
        thread2.start();
        Thread.sleep(1000);
        Thread thread = new Thread(new RunTest(myThread, true,1,1));
        thread.start();
        Thread thread11 = new Thread(new Runnable() {
            @Override
            public void run() {
                
            }
        });


    }
}

class SyncClass {
    final Lock lock = new ReentrantLock();
    // 条件变量：添加
    final Condition putCondition = lock.newCondition();
    // 条件变量：移除
    final Condition removeCondition = lock.newCondition();

    private Map<Integer, Integer> map = new HashMap<>();

    int MAX_SIZE = 3;

    /**
     * 小于最大容量时添加
     *
     * @param k
     * @param v
     */
    public void put(Integer k, Integer v) {
        lock.lock();
        try {
            while (map.size() >= MAX_SIZE) {
                putCondition.await();
            }
            map.put(k, v);
            System.out.println("put:" + k + " " + v);
            removeCondition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void remove(Integer k) {
        lock.lock();
        try {
            while (map.isEmpty()) {
                removeCondition.await();
            }
            map.remove(k);
            System.out.println("remove:"+k);
            putCondition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

class RunTest implements Runnable {
    SyncClass th;
    boolean flag;
    Integer k;
    Integer v;

    /**
     * @param th
     * @param flag true:set  false:get
     */
    public RunTest(SyncClass th, boolean flag, Integer k, Integer v) {
        this.th = th;
        this.flag = flag;
        this.k = k;
        this.v = v;
    }

    @Override
    public void run() {
        if (flag) {
            for (int i = 0; i < 10; i++) {
                th.put(i, v);
            }
        } else {
            for (int i = 0; i < 10; i++) {
                th.remove(i);
            }
        }
    }
}