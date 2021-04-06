package thread;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DaemonTest {
    public static void main(String[] args) throws InterruptedException {
        TestRunnable ttt = new TestRunnable();
        Thread thread = new Thread(ttt);
        thread.start();
        thread.join();
        System.out.println("111");
    }
}


class TestRunnable implements Runnable {
    public void run(){
        try {
            Thread.sleep(1000); // 守护线程阻塞1秒后运行
            System.out.println("22222");
        } catch(InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}