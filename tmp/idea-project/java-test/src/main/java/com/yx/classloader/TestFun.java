package com.yx.classloader;

public class TestFun {
    public void Fun1(int... a) throws InterruptedException {
        System.out.println("qwetr");
        Thread.sleep(10000);
        System.out.println("qwetr");
    }
    public void Fun2() throws InterruptedException {
        System.out.println("asdf");
        Thread.sleep(10000);
        System.out.println("asdf");
    }

    public static void main(String[] args) throws InterruptedException {
//        Fun1();
    }
}
