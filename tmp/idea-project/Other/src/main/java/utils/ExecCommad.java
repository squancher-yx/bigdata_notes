package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ExecCommad {
    public static void main(String[] args) throws IOException {
//        run("ping 127.0.0.1 -t");
        String[] cmd = new String[3];
        cmd[0] = "ping";
        cmd[1] = "127.0.0.1";
        cmd[2] = "-t";
        run(cmd);
    }

    public static String run(String command) throws IOException {
        Scanner input = null;
        String result = "";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
//            try {
//                //等待命令执行完成，可能未完成。
//                process.waitFor(10, TimeUnit.SECONDS);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            InputStream is = process.getInputStream();
            input = new Scanner(is);
            while (input.hasNextLine()) {
                String tmp = input.nextLine();
                result += tmp + "\n";
                System.out.println(tmp + "\n");
            }
            result = command + "\n" + result;
        } finally {
            if (input != null) {
                input.close();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }


    // exec方法无法执行带 | > 等特殊字符的命令，如 ps -ef | grep java，使用字符串数组
    public static String run(String[] command) throws IOException {
        Scanner input = null;
        String result = "";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
//            try {
//                //等待命令执行完成
//                process.waitFor(10, TimeUnit.SECONDS);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            InputStream is = process.getInputStream();
            input = new Scanner(is);
            while (input.hasNextLine()) {
                String tmp = input.nextLine();
                result += tmp + "\n";
                System.out.println(tmp + "\n");
            }
            result = command + "\n" + result;
        } finally {
            if (input != null) {
                input.close();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }
}
