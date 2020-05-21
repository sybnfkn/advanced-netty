package com.zhangyan.bio.eof;

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
 * 10:09:11.138687 IP localhost.58450 > localhost.http-alt: Flags [S], seq 2536016985, win 65535, options [mss 16344,nop,wscale 6,nop,nop,TS val 222027782 ecr 0,sackOK,eol], length 0
 * 10:09:11.138761 IP localhost.http-alt > localhost.58450: Flags [S.], seq 1655685196, ack 2536016986, win 65535, options [mss 16344,nop,wscale 6,nop,nop,TS val 222027782 ecr 222027782,sackOK,eol], length 0
 * 10:09:11.138794 IP localhost.58450 > localhost.http-alt: Flags [.], ack 1, win 6379, options [nop,nop,TS val 222027782 ecr 222027782], length 0
 * 10:09:11.138810 IP localhost.http-alt > localhost.58450: Flags [.], ack 1, win 6379, options [nop,nop,TS val 222027782 ecr 222027782], length 0
 * 10:09:11.142852 IP localhost.58450 > localhost.http-alt: Flags [P.], seq 1:12, ack 1, win 6379, options [nop,nop,TS val 222027786 ecr 222027782], length 11: HTTP
 * 10:09:11.142878 IP localhost.http-alt > localhost.58450: Flags [.], ack 12, win 6379, options [nop,nop,TS val 222027786 ecr 222027786], length 0
 * 10:09:11.142956 IP localhost.58450 > localhost.http-alt: Flags [P.], seq 12:23, ack 1, win 6379, options [nop,nop,TS val 222027786 ecr 222027786], length 11: HTTP
 * 10:09:11.142969 IP localhost.http-alt > localhost.58450: Flags [.], ack 23, win 6379, options [nop,nop,TS val 222027786 ecr 222027786], length 0
 * 10:09:11.144122 IP localhost.58450 > localhost.http-alt: Flags [F.], seq 23, ack 1, win 6379, options [nop,nop,TS val 222027787 ecr 222027786], length 0
 * 10:09:11.144147 IP localhost.http-alt > localhost.58450: Flags [.], ack 24, win 6379, options [nop,nop,TS val 222027787 ecr 222027787], length 0
 * 10:09:11.145207 IP localhost.http-alt > localhost.58450: Flags [F.], seq 1, ack 24, win 6379, options [nop,nop,TS val 222027788 ecr 222027787], length 0
 * 10:09:11.145240 IP localhost.58450 > localhost.http-alt: Flags [.], ack 2, win 6379, options [nop,nop,TS val 222027788 ecr 222027788], length 0
 *
 *
 *
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
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[1];
                int length;
                // 正常会阻塞这里，对端close后返回 -1 EOF
                while ((length = input.read(buffer)) != -1) {
                    // 请求结尾标示
                    if (buffer[0] == '\n') {
                        // 模拟后端业务处理
                        String req = new String(output.toByteArray(), "utf-8");
                        System.out.println(req);
                        // 重置容器
                        output.reset();
                    } else {
                        output.write(buffer, 0, length);
                    }
                }
                // 收到EOF之后，直接关闭
                socket.close();
            } catch (Exception e) {

            }
        }
    }
}
