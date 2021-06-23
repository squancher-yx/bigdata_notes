package io;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SockTest {
    public static void test1() throws IOException {
        ServerSocket ss = new ServerSocket(8888);
        Socket s = ss.accept();

        //字符流
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

        //字符流
        BufferedInputStream br2 = new BufferedInputStream(s.getInputStream());
        BufferedOutputStream bw2 = new BufferedOutputStream(s.getOutputStream());
    }
}
