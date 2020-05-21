package com.zhangyan.bio.rst;

import java.io.InputStream;
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
//        socket.setTcpNoDelay(true);

        SocketAddress address = new InetSocketAddress(HOST, PORT);
        socket.connect(address);

        InputStream inputStream = socket.getInputStream();
        byte[] buffer = new byte[1];
        int result = 0;
        while ((result = inputStream.read(buffer)) != -1) {
            if (buffer[0] == '\n') {
                socket.close();
                break;
            }
        }
        System.out.println("end........");
    }
}
