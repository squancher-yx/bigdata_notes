package jvm;

import java.util.ArrayList;
import java.util.Scanner;

public class MenoryTest {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String[]> list = new ArrayList<>();
        while (true){
            String next = scanner.next();
            String[] tmp = new String[100000];
            for(int i = 0; i<tmp.length;i++){
                tmp[i]="sssssssssssssssssssssssssssssssssssssssssss";
            }
            System.out.println(next);
            list.add(tmp);
        }

    }
}
