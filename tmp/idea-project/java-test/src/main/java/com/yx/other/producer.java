package com.yx.other;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class producer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);
        Socket socket = serverSocket.accept();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                OutputStream outputStream = socket.getOutputStream();
                String string = scanner.nextLine()+"\n";
                outputStream.write(string.getBytes());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
