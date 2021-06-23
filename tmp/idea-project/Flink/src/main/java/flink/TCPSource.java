package flink;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TCPSource {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);

        FileInputStream f1 = new FileInputStream("");
        BufferedInputStream b2 = new BufferedInputStream(f1);

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                while (true) {
                    Scanner scanner = new Scanner(System.in);
                    OutputStream outputStream = socket.getOutputStream();
                    String string = scanner.nextLine() + "\n";
                    outputStream.write(string.getBytes());
                    outputStream.flush();
                }

            } catch (IOException e) {
                System.err.println("connection closed, start new.");
            }
        }
    }
}
