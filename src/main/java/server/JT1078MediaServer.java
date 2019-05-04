package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class JT1078MediaServer {

    private static void bind() {
        EpollEventLoopGroup bossGroup = new EpollEventLoopGroup();
        EpollEventLoopGroup workerGroup = new EpollEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(EpollServerSocketChannel.class)
                    .childHandler(new JT1078ServerTerminalInitializer())
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            log.info("====================JT1078终端服务器启动完毕====================");
            ChannelFuture channelFuture = b.bind(10003).sync();
            channelFuture.channel().closeFuture().sync();
        }
        catch (Exception e) {
           e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    private static void websocket() {
        EpollEventLoopGroup bossGroup = new EpollEventLoopGroup();
        EpollEventLoopGroup workerGroup = new EpollEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(EpollServerSocketChannel.class)
                    .childHandler(new JT1078ServerWebInitializer())
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            log.info("====================JT1078websocket服务器启动完毕====================");
            ChannelFuture channelFuture = b.bind(10004).sync();
            channelFuture.channel().closeFuture().sync();
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }



    private final static ExecutorService executorService = Executors.newFixedThreadPool(2);

    private static synchronized void stopServer() {
        executorService.shutdown();
    }

    private static synchronized void startServer() {
        executorService.execute(JT1078MediaServer::bind);
        executorService.execute(JT1078MediaServer::websocket);
    }

    public static void main(String[] args) {
        startServer();
    }
}
