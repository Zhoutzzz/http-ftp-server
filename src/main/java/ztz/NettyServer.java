package ztz;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import ztz.ftp.FtpChannelInit;

public class NettyServer {
    static ServerBootstrap serverBootstrap = new ServerBootstrap();
    static EventLoopGroup boss = new NioEventLoopGroup(1);
    static EventLoopGroup work = new NioEventLoopGroup(12);

    public static void main(String[] args) {

        try {

            serverBootstrap.group(boss, work).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new FtpChannelInit());

            Channel ch = serverBootstrap.bind(10001).sync().channel();
            ch.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }

    }

}
