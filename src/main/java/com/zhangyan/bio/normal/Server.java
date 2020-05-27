package com.zhangyan.bio.normal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: Messi
 * @Date: 2020/05/27/10:15 上午
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

            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
