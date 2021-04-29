package thread;

/**
 *  等待当前线程完成，详见源码 while(isAlive())
 */

public class JoinTest {
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
            Thread.sleep(1000);//等待完成
            System.out.println("22222");
        } catch(InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}