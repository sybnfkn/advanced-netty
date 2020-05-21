package com.zhangyan.bio.rst;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: Messi
 * @Date: 2020/05/18/7:44 下午
 * @Description:
 *
 * 10:35:47.911281 IP localhost.58787 > localhost.http-alt: Flags [S], seq 4085467106, win 65535, options [mss 16344,nop,wscale 6,nop,nop,TS val 223620011 ecr 0,sackOK,eol], length 0
 * 10:35:47.911354 IP localhost.http-alt > localhost.58787: Flags [S.], seq 1616411357, ack 4085467107, win 65535, options [mss 16344,nop,wscale 6,nop,nop,TS val 223620011 ecr 223620011,sackOK,eol], length 0
 * 10:35:47.911369 IP localhost.58787 > localhost.http-alt: Flags [.], ack 1, win 6379, options [nop,nop,TS val 223620011 ecr 223620011], length 0
 * 10:35:47.911386 IP localhost.http-alt > localhost.58787: Flags [.], ack 1, win 6379, options [nop,nop,TS val 223620011 ecr 223620011], length 0
 * 10:35:47.914694 IP localhost.http-alt > localhost.58787: Flags [P.], seq 1:2, ack 1, win 6379, options [nop,nop,TS val 223620014 ecr 223620011], length 1: HTTP
 * 10:35:47.914716 IP localhost.58787 > localhost.http-alt: Flags [.], ack 2, win 6379, options [nop,nop,TS val 223620014 ecr 223620014], length 0
 *
 * 10:35:47.915644 IP localhost.58787 > localhost.http-alt: Flags [F.], seq 1, ack 2, win 6379, options [nop,nop,TS val 223620014 ecr 223620014], length 0
 * 10:35:47.915666 IP localhost.http-alt > localhost.58787: Flags [.], ack 2, win 6379, options [nop,nop,TS val 223620014 ecr 223620014], length 0
 *
 * 10:35:48.916008 IP localhost.http-alt > localhost.58787: Flags [P.], seq 2:3, ack 2, win 6379, options [nop,nop,TS val 223621009 ecr 223620014], length 1: HTTP
 * 10:35:48.916066 IP localhost.58787 > localhost.http-alt: Flags [R], seq 4085467108, win 0, length 0
 */
public class Server {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(8080));

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(new Task(socket)).start();
        }
    }
    static class Task implements Runnable {
        private Socket socket;

        public Task(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            try {
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();
                // 通知客户端关闭
                output.write('\n');
                Thread.sleep(1000);
                // 继续向客户端写数据
                output.write('a');
                // 接着读取数据
                byte[] buffer = new byte[1];
                input.read(buffer);
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
