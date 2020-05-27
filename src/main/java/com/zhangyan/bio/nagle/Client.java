package com.zhangyan.bio.nagle;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: Messi
 * @Date: 2020/05/23/7:20 下午
 * @Description:
 * nc -l 9999
 */
public class Client {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket();
        SocketAddress address = new InetSocketAddress("127.0.0.1", 9999);
        socket.connect(address);
        OutputStream output = socket.getOutputStream();
        byte[] request = new byte[10];
        // 分 5 次发送 5 个小包
        for (int i = 0; i < 5; i++) {
            output.write(request);
        }
        TimeUnit.SECONDS.sleep(1);
        socket.close();
    }
}
