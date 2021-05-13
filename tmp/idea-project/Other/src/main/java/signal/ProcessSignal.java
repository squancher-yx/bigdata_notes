package signal;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class ProcessSignal {
    public static void main(String[] args) {
        TestSignal handler = new TestSignal();
        //仅支持某些信号
        //Signal already used by VM or OS: SIGQUIT
        Signal.handle(new Signal("HUP"), handler);//kill -1
        Signal.handle(new Signal("INT"), handler);//kill -2
        Signal.handle(new Signal("ABRT"), handler);
        Signal.handle(new Signal("ALRM"), handler);
        Signal.handle(new Signal("TERM"), handler);


        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class TestSignal implements SignalHandler {
    //收到信号后执行，不会自动结束
    public void handle(Signal arg0) {
        System.out.println(arg0.getName() + "is recevied.");
    }

}