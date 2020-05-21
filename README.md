# 1.bio
####   （1）模拟网络中各种异常（代码中有tcpdump抓包的数据）。

- ###### Connection reset

  [模拟异常代码](https://github.com/sybnfkn/advanced-netty/tree/master/src/main/java/com/zhangyan/bio/rst)

  ```java
  java.net.SocketException: Connection reset
  	at java.net.SocketInputStream.read(SocketInputStream.java:210)
  	at java.net.SocketInputStream.read(SocketInputStream.java:141)
  	at java.net.SocketInputStream.read(SocketInputStream.java:127)
  	at com.zhangyan.bio.rst.Server$Task.run(Server.java:60)
  	at java.lang.Thread.run(Thread.java:748)
  ```

- ###### Broken pipe

  [模拟异常代码](https://github.com/sybnfkn/advanced-netty/tree/master/src/main/java/com/zhangyan/bio/broken)

  ```java
  java.net.SocketException: Broken pipe (Write failed)
  	at java.net.SocketOutputStream.socketWrite0(Native Method)
  	at java.net.SocketOutputStream.socketWrite(SocketOutputStream.java:111)
  	at java.net.SocketOutputStream.write(SocketOutputStream.java:134)
  	at com.zhangyan.bio.broken.Server$Task.run(Server.java:79)
  	at java.lang.Thread.run(Thread.java:748)
  ```

- ###### EOF（不算一种异常）

  [模拟场景的代码](https://github.com/sybnfkn/advanced-netty/tree/master/src/main/java/com/zhangyan/bio/eof)



#### （2）常见socket参数。

- ###### SO_REUSEADDR

  [示例代码](https://github.com/sybnfkn/advanced-netty/tree/master/src/main/java/com/zhangyan/bio/so_reuseaddr)

- ###### SO_LINGER

  [示例代码](https://github.com/sybnfkn/advanced-netty/tree/master/src/main/java/com/zhangyan/bio/so_linger)



#### （3）backlog案例。





# 2.nio
####   （1）完整的nio案例。

####   （2）模拟Reactor模型案例。

# 3.netty
####   （1）完整的netty案例。

####   （2）生产级心跳检测和异常重连。

####   （3）拆包粘包，自定义协议。

####   （4）高水位低水位。

####   （5）Attribution应用。

####   （6）记一次堆外内存泄漏。