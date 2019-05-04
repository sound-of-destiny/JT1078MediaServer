import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;

public class MyClient {

    public static void main(String[] args) throws Exception {
        EpollEventLoopGroup eventLoopGroup = new EpollEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(eventLoopGroup).channel(EpollSocketChannel.class)
                    .handler(new MyClientInitializer());

            ChannelFuture channelFuture = b.connect("localhost", 10003).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
