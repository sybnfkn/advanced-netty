package com.zhangyan.bio.exception;

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
                // 收到EOF之后，再次write
                outputStream.write(99);
                try {
                    int read = input.read(buffer);
                }catch (Exception e) {
                    // 理论应该RST，但是底层源码直接eof标示返回-1，但是对端tcp包过来是RST。
                    e.printStackTrace();
                }
                // 收到rst后再次write
                try {
                    outputStream.write(100);
                } catch (Exception e) {
                    // broken pipe异常
                    e.printStackTrace();
                }

                socket.close();

                // 关闭连接之后read
                try {
                    outputStream.write(200);
                } catch (Exception e) {
                    // close异常
                    e.printStackTrace();
                }
            } catch (Exception e) {

            }

        }
    }
}
