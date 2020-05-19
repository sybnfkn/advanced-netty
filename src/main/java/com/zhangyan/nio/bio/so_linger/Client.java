package com.zhangyan.nio.bio.so_linger;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: Messi
 * @Date: 2020/05/18/7:44 下午
 * @Description:
 */
public class Client {
    private static int PORT = 8080;
    private static String HOST = "127.0.0.1";

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket();
        // 测试#1: 默认设置
//        socket.setSoLinger(false, 0);
        // 测试#2
//         socket.setSoLinger(true, 0);
        // 测试#3
        socket.setSoLinger(true, 1);

        SocketAddress address = new InetSocketAddress(HOST, PORT);
        socket.connect(address);

        OutputStream output = socket.getOutputStream();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append("hel");
        }
        byte[] request = sb.toString().getBytes("utf-8");
        output.write(request);
        long start = System.currentTimeMillis();
        socket.close();
        long end = System.currentTimeMillis();
        System.out.println("close time cost: " + (end - start));
    }
}
