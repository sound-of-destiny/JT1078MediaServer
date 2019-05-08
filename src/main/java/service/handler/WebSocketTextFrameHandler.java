package service.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import push.TalkBackPushManager;
import push.TalkBackPushTask;

@Slf4j
public class WebSocketTextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info("text : {}", msg.text());
        log.info("readableBytes : {}", msg.content().readableBytes());

        String ctxId = ctx.channel().id().asLongText();

        TalkBackPushManager talkBackPushManager = TalkBackPushManager.getInstance();
        TalkBackPushTask task = talkBackPushManager.get(ctxId);
        if (msg.text().equals("start")) {
            if (task == null) {
                task = talkBackPushManager.newPublishTask(ctx.channel().id().asLongText());
                if (task != null) {
                    task.start();
                }
            }
        }

        if (msg.text().equals("stop")) {
            if (task != null) {
                task.shutdown();
                talkBackPushManager.remove(ctxId);
            }
        }

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("【有对讲web接入】   " + channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("【有对讲web断开】");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("【异常发生】", cause);
        ctx.close();
    }
}
