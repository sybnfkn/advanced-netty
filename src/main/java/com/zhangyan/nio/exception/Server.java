package com.zhangyan.nio.exception;

import com.zhangyan.nio.util.CodecUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
                // 1。首先对方close会读到EOF
                System.out.println("读取到EOF。。。");
                // 2。接着发送数据，对端会发回RST
                send(clientSocketChannel, "abc。。。");
                System.out.println("写入数据：" + "abc。。。");
                return;
            }
        } catch (IOException e) {
            // 如果继续读会发生connection reset by peer
            e.printStackTrace();
            // 3。如果继续写数据就会出现broken pipe
            send(clientSocketChannel, "def。。。");
            System.out.println("写入数据：" + "def。。。");
            // 停止接受这个channel的事件
            clientSocketChannel.register(selector, 0);
            return;
        }
        // 正常接受数据，打印数据
        if (readBuffer != null && readBuffer.position() > 0) { // 写入模式下，
            String content = CodecUtil.newString(readBuffer);
            System.out.println("读取数据：" + content);
        }
    }



    private void send(SocketChannel channel, String content) {
        // 写入 Buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            buffer.put(content.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        // 写入 Channel
        buffer.flip();
        try {
            // 注意，不考虑写入超过 Channel 缓存区上限。
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * 17:32:39.676522 IP localhost.53755 > localhost.distinct: Flags [S], seq 4020747160, win 65535, options [mss 16344,nop,wscale 6,nop,nop,TS val 778751629 ecr 0,sackOK,eol], length 0
     * 17:32:39.676686 IP localhost.distinct > localhost.53755: Flags [S.], seq 2908853130, ack 4020747161, win 65535, options [mss 16344,nop,wscale 6,nop,nop,TS val 778751629 ecr 778751629,sackOK,eol], length 0
     * 17:32:39.676710 IP localhost.53755 > localhost.distinct: Flags [.], ack 1, win 6379, options [nop,nop,TS val 778751629 ecr 778751629], length 0
     * 17:32:39.676728 IP localhost.distinct > localhost.53755: Flags [.], ack 1, win 6379, options [nop,nop,TS val 778751629 ecr 778751629], length 0
     *
     * // -------客户端close后，向服务端发送FIN。
     * 17:32:44.705387 IP localhost.53755 > localhost.distinct: Flags [F.], seq 1, ack 1, win 6379, options [nop,nop,TS val 778756642 ecr 778751629], length 0
     * 17:32:44.705419 IP localhost.distinct > localhost.53755: Flags [.], ack 2, win 6379, options [nop,nop,TS val 778756642 ecr 778756642], length 0
     *
     * // 收到客户端的FIN后，仍然向客户端发送数据
     * 17:32:44.710615 IP localhost.distinct > localhost.53755: Flags [P.], seq 1:13, ack 2, win 6379, options [nop,nop,TS val 778756646 ecr 778756642], length 12
     *
     * // 客户端的操作系统负责发回RST，因为所属的socket已经关闭了
     * 17:32:44.710646 IP localhost.53755 > localhost.distinct: Flags [R], seq 4020747162, win 0, length 0
     */
    public static void main(String[] args) throws IOException {
        new Server();
    }
}
