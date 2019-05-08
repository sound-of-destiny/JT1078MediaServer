package service.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import push.TalkBackPushManager;
import push.TalkBackPushTask;

@Slf4j
public class WebSocketBinaryFrameHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
        int length = msg.content().readableBytes();
        byte[] data = new byte[length - 42];
        ByteBuf byteBuf = msg.content();
        byteBuf.skipBytes(42);
        byteBuf.readBytes(data);
        log.info("readableBytes : {}", length);
        TalkBackPushManager talkBackPushManager = TalkBackPushManager.getInstance();
        TalkBackPushTask task = talkBackPushManager.get(ctx.channel().id().asLongText());
        task.writeAudio(data);
        task.flushAudio();
    }
}
