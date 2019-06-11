package service.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import push.TalkBackPushManager;
import push.TalkBackPushTask;
import server.BusinessManager;
import util.BCD8421Operator;
import util.ByteOperator;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

@Slf4j
public class WebSocketBinaryFrameHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
        int length = msg.content().readableBytes();
        byte[] data = new byte[length];
        ByteBuf byteBuf = msg.content();
        byteBuf.readBytes(data);
        log.info("readableBytes : {}", length);

        // ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.copiedBuffer(data)));
    }
}
