package com.zhangyan.nio.bio.so_linger;

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
                OutputStream outputStream = socket.getOutputStream();
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
                    } else {
                        output.write(buffer, 0, length);
                    }
                }

                socket.close();
            } catch (Exception e) {

            }

        }
    }
}
