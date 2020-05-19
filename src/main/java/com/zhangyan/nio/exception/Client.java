package com.zhangyan.nio.exception;

import com.zhangyan.nio.util.CodecUtil;
import sun.nio.ch.sctp.SctpStdSocketOption;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.net.SocketOptions;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

public class Client {
    private SocketChannel clientSocketChannel;
    private Selector selector;

    private CountDownLatch connected = new CountDownLatch(1);

    public Client() throws IOException, InterruptedException {
        // 打开 Client Socket Channel
        clientSocketChannel = SocketChannel.open();
        // 配置为非阻塞
        clientSocketChannel.configureBlocking(false);
        clientSocketChannel.setOption(StandardSocketOptions.SO_LINGER, 1);
        // 创建 Selector
        selector = Selector.open();
        // 注册 Server Socket Channel 到 Selector
        clientSocketChannel.register(selector, SelectionKey.OP_CONNECT);
        // 连接服务器
        clientSocketChannel.connect(new InetSocketAddress("127.0.0.1", 8080));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    handleKeys();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        if (connected.getCount() != 0) {
            connected.await();
        }
        System.out.println("Client 启动完成");
    }

    @SuppressWarnings("Duplicates")
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

    private synchronized void handleKey(SelectionKey key) throws IOException {
        // 接受连接就绪
        if (key.isConnectable()) {
            handleConnectableKey(key);
        }
        if (key.isReadable()) {
            handleReadableKey(key); // 这里是一个变量，案例2会被注释
        }
    }

    private void handleConnectableKey(SelectionKey key) throws IOException {
        // 完成连接
        if (!clientSocketChannel.isConnectionPending()) {
            return;
        }
        clientSocketChannel.finishConnect();
        // log
        System.out.println("接受新的 Channel");
        // 注册 Client Socket Channel 到 Selector
        clientSocketChannel.register(selector, SelectionKey.OP_READ);
        // 标记为已连接
        connected.countDown();
    }

    private void handleReadableKey(SelectionKey key) throws ClosedChannelException {
        // Client Socket Channel
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        // 读取数据
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        try {
            int count = clientSocketChannel.read(readBuffer);
            if (count == -1) {
                System.out.println("读取到EOF。。。");
                return ;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 打印数据
        if (readBuffer.position() > 0) { // 写入模式下，
            String content = CodecUtil.newString(readBuffer);
            System.out.println("读取数据：" + content);
        }
    }
    public void close() {
        try {
            clientSocketChannel.close();
            selector.close();
        } catch (Exception e) {

        }
    }
    public void send(String content) {
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
            clientSocketChannel.write(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client();
//        client.send("client say byebye...");
//        Thread.sleep(5000);
        client.close(); // 不管是主动关闭还是kill掉进程，都会发送给对象FIN指令
    }
}
