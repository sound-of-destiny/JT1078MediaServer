package service.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import protocol.RTPMessage;
import push.*;

import java.time.Instant;

@Slf4j
public class RTPPackageHandler extends SimpleChannelInboundHandler<RTPMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RTPMessage msg) throws Exception {
        MediaPushManager mediaPushManager = MediaPushManager.getInstance();
        String taskName = msg.getSIM() + "_" + msg.getLogicChannel();
        if (msg.getDataType() != 3 && msg.getDataType() != 4) {
            VideoPushTask task = mediaPushManager.getVideoTask(taskName);
            if (task == null) {
                mediaPushManager.newVideoPublishTask(taskName);
                task = mediaPushManager.getVideoTask(taskName);
                task.start();
            }
            task.write(msg.getDataBody());
            if (msg.getPackageFlag() == 0 || msg.getPackageFlag() == 2) {
                task.flush();
                log.info("video timestamp : {}", Instant.ofEpochMilli(msg.getTimeStamp()).toString());
            }
        } else if (msg.getDataType() == 3) {
            // AudioPushTask task = mediaPushManager.getAudioTask(taskName);
            TalkBackPushManager talkBackPushManager = TalkBackPushManager.getInstance();
            TalkBackPushTask task = talkBackPushManager.get(taskName);
            if (task == null) {
                // mediaPushManager.newAudioPublishTask(taskName);
                talkBackPushManager.newPublishTask(taskName);
                // task = mediaPushManager.getAudioTask(taskName);
                task = talkBackPushManager.get(taskName);
                task.start();
            }
            log.info("audio timestamp : {}", Instant.ofEpochMilli(msg.getTimeStamp()).toString());
            log.info("size : {}", msg.getDataBodyLength());
            task.writeAudio(msg.getDataBody());
            task.flushAudio();
        }

        /* byte[] msgBody = ByteOperator.concatAll(ByteOperator.intTo1Byte(msg.getLogicChannel()), new byte[] {0});
        BusinessManager businessManager = BusinessManager.getInstance();
        int flowId = businessManager.currentFlowId();
        ChannelFuture channelFuture = ctx.writeAndFlush(Unpooled.copiedBuffer(
                JT808ProtocolUtils.sendToTerminal(msgBody, flowId, msg.getSIM(),0x9105))).sync();
        if (channelFuture.isSuccess()) log.info("success"); */

    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }

}
