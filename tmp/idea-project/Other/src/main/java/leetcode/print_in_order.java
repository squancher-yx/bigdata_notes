package leetcode;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class print_in_order {
}

class Foo {
    Lock lock = new ReentrantLock();
    Condition condition1 = lock.newCondition();
    Condition condition2 = lock.newCondition();

    public Foo() {

    }

    public void first(Runnable printFirst) throws InterruptedException {
        lock.lock();
        System.out.println("1");
        // printFirst.run() outputs "first". Do not change or remove this line.
        printFirst.run();
        condition1.signal();
        lock.unlock();
    }

    public void second(Runnable printSecond) throws InterruptedException {
        // printSecond.run() outputs "second". Do not change or remove this line.
        lock.lock();
        System.out.println("2");
        condition1.await();
        printSecond.run();
        condition2.signal();
        lock.unlock();
    }

    public void third(Runnable printThird) throws InterruptedException {
        lock.lock();
        System.out.println("3");
        // printThird.run() outputs "third". Do not change or remove this line.
        condition2.await();
        printThird.run();
        lock.unlock();
    }
}

//ok//321 231
//  //123 312 213