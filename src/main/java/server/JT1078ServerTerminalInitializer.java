package server;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import service.codec.RTPMessageDecoder;
import service.handler.RTPPackageHandler;
import util.JT1078Const;

public class JT1078ServerTerminalInitializer extends ChannelInitializer {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new DelimiterBasedFrameDecoder(1530, Unpooled.copiedBuffer(JT1078Const.pkg_delimiter)));
        pipeline.addLast(new RTPMessageDecoder());
        pipeline.addLast(new RTPPackageHandler());
    }
}
