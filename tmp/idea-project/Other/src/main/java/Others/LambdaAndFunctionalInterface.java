package Others;

/**
 * Java8 匿名函数和lambda表达式的区别
 * lambda 实现函数式编程时，函数必须满足 @FunctionalInterface（有且仅有一个抽象方法）
 * lambda 引用外部变量时不能重新赋值（必须为 final）
 */
public class LambdaAndFunctionalInterface {
    /**
     * this和super的含义不同
     * ① 在匿名函数中this代表类自身，在lambda表达式中表示包含类；
     * ② 匿名类可以屏蔽包含类的变量，而lambda表达式不能。
     */
    public void fun1() {
        int a = 10;
        Runnable r1 = () -> {
            //int a = 2;   // 编译报错
            System.out.println(a);
        };
        Runnable r2 = new Runnable() {
            public void run() {
                int a = 2;   // 正常 覆盖了上面
                System.out.println(a);
            }
        };
    }


    public static void doSomething(Runnable r) {
        r.run();
    }

    public static void doSomething(Task a) {
        a.execute();
    }

    /**
     * 重载问题
     */
    public void fun2() {
        // 用匿名类实现Task,没有歧义
        doSomething(new Task() {
            public void execute() {
                System.out.println("qqqqq");
            }
        });
        // 用lambda表达式,存在歧义，不清楚调用的是Runnable重载方法还是Task重载方法
        // doSomething(() -> System.out.println("qqqqq"));
        // 解决办法:显示指定
        doSomething((Runnable) () -> System.out.println("111111"));
    }
}

interface Task {
    public void execute();
}

