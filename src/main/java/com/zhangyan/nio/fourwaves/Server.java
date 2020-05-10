package com.zhangyan.nio.fourwaves;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public Server() throws IOException {
        // 打开 Server Socket Channel
        serverSocketChannel = ServerSocketChannel.open();
        // 配置为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 绑定 Server port
        serverSocketChannel.socket().bind(new InetSocketAddress("127.0.0.1", 9999));
        // 创建 Selector
        selector = Selector.open();
        // 注册 Server Socket Channel 到 Selector
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server 启动完成。。。");
        handleKeys();
    }

    private void handleKeys() throws IOException {
        while (true) {
            // 通过 Selector 选择 Channel
            int selectNums = selector.select(30 * 1000L);
            if (selectNums == 0) {
                continue;
            }
            // 遍历可选择的 Channel 的 SelectionKey 集合
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove(); // 移除下面要处理的 SelectionKey
                if (!key.isValid()) { // 忽略无效的 SelectionKey
                    continue;
                }
                handleKey(key);
            }
        }
    }

    private void handleKey(SelectionKey key) throws IOException {
        // 接受连接就绪
        if (key.isAcceptable()) {
            handleAcceptableKey(key);
        }
        // 读就绪
        if (key.isReadable()) {
            handleReadableKey(key);
        }
    }

    private void handleAcceptableKey(SelectionKey key) throws IOException {
        SocketChannel clientSocketChannel = ((ServerSocketChannel) key.channel()).accept();
        // 配置为非阻塞
        clientSocketChannel.configureBlocking(false);
        System.out.println("接受新的 Channel");
        clientSocketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void handleReadableKey(SelectionKey key) throws IOException {
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        // 读取数据，此时可能读取到EOF，为了模拟异常，接着发送数据
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        try {
            int count = clientSocketChannel.read(readBuffer);
            if (count == -1) {
                // 收到客户端的FIN，服务端发回FIN
                clientSocketChannel.close();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 三次握手
     * 18:16:33.671813 IP localhost.54507 > localhost.distinct: Flags [S], seq 2937720159, win 65535, options [mss 16344,nop,wscale 6,nop,nop,TS val 781378561 ecr 0,sackOK,eol], length 0
     * 18:16:33.671883 IP localhost.distinct > localhost.54507: Flags [S.], seq 2031035892, ack 2937720160, win 65535, options [mss 16344,nop,wscale 6,nop,nop,TS val 781378561 ecr 781378561,sackOK,eol], length 0
     * 18:16:33.671893 IP localhost.54507 > localhost.distinct: Flags [.], ack 1, win 6379, options [nop,nop,TS val 781378561 ecr 781378561], length 0
     * 18:16:33.671903 IP localhost.distinct > localhost.54507: Flags [.], ack 1, win 6379, options [nop,nop,TS val 781378561 ecr 781378561], length 0
     *
     * 四次挥手
     * 18:16:38.688024 IP localhost.54507 > localhost.distinct: Flags [F.], seq 1, ack 1, win 6379, options [nop,nop,TS val 781383563 ecr 781378561], length 0
     * 18:16:38.688087 IP localhost.distinct > localhost.54507: Flags [.], ack 2, win 6379, options [nop,nop,TS val 781383563 ecr 781383563], length 0
     * 18:16:38.692636 IP localhost.distinct > localhost.54507: Flags [F.], seq 1, ack 2, win 6379, options [nop,nop,TS val 781383567 ecr 781383563], length 0
     * 18:16:38.692680 IP localhost.54507 > localhost.distinct: Flags [.], ack 2, win 6379, options [nop,nop,TS val 781383567 ecr 781383567], length 0
     */
    public static void main(String[] args) throws IOException {
        new Server();
    }
}
