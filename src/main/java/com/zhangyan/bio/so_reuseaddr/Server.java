package com.zhangyan.bio.so_reuseaddr;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: Messi
 * @Date: 2020/05/17/7:53 下午
 * @Description:
 */
public class Server {
    /**
     * 使用nc localhost 8080 作为一个客户端连接服务器
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        // setReuseAddress 必须在 bind 函数调用之前执行
//        serverSocket.setReuseAddress(false);
        serverSocket.bind(new InetSocketAddress(8080));
        System.out.println("reuse address: " + serverSocket.getReuseAddress());
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("incoming socket..");
            OutputStream out = socket.getOutputStream();
            out.write("Hello\n".getBytes());
            out.close();
            break;
        }
    }
}
