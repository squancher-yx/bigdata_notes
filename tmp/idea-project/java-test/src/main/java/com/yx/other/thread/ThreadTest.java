package com.yx.other.thread;

import java.util.concurrent.Executors;

public class ThreadTest {
    public static void main(String[] args) throws InterruptedException {
        MyThread2 myThread = new MyThread2();
        myThread.setName("world");
        Thread thread = new Thread(myThread);
        thread.start();
        System.out.println(thread.getName());
        Thread.sleep(1000);
        Executors.newFixedThreadPool(30);
        myThread.setName("world2");
    }
}
class MyThread2 implements Runnable
{
    private String name;

    public void setName(String name)
    {
        this.name = name;
    }
    public void run()
    {
        System.out.println("hello " + name);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("hello " + name);
    }
}