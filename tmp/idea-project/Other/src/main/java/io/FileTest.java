package io;

import java.io.*;

public class FileTest {
    public static void main(String[] args) throws IOException {
        test2();
//        test1();
    }

    /**
     * 字节流1
     * @throws IOException
     */
    public static void test1() throws IOException {

        long start = System.currentTimeMillis();
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                "D:\\vm\\share2.tar"));
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream("D:\\vm\\share.tar1"));

        byte[] buff = new byte[8192];
        int len = 1;
        while ((len = bis.read(buff)) != -1) {
            bos.write(buff, 0, len);
        }

        bos.close();
        bis.close();
        System.out.println(System.currentTimeMillis() - start + "毫秒");
    }

    /**
     * 字节流2
     * 每次写入大小和 BufferedInputStream 缓冲区一样时，耗时几乎一样。
     * 5.6GB 文件：
     *  BufferedInputStream ：76037毫秒
     *  FileOutputStream： 74499毫秒
     * @throws IOException
     */
    public static void test2() throws IOException {
        long start = System.currentTimeMillis();
        FileInputStream fis = new FileInputStream("D:\\vm\\share2.tar");
        FileOutputStream fos = new FileOutputStream("D:\\vm\\share.tar2");
        int len;
        byte[] buff = new byte[8192];
        while ((len = fis.read(buff)) != -1) {
            fos.write(buff, 0, len);
        }

        fis.close();
        fos.close();
        System.out.println(System.currentTimeMillis() - start + "毫秒");
    }

    /**
     * 字符流 Reader、Writer 类似字节流
     */
}
