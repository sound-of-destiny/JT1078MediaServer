package service.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import protocol.RTPMessage;
import push.MediaPushManager;
import push.MediaPushTask;

import java.time.Instant;

@Slf4j
public class RTPPackageHandler extends SimpleChannelInboundHandler<RTPMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RTPMessage msg) throws Exception {
        /*log.info(msg.toString());
        log.info(Instant.ofEpochMilli(msg.getTimeStamp()).toString());*/
        MediaPushManager mediaPushManager = MediaPushManager.getInstance();
        MediaPushTask task = mediaPushManager.getTask(ctx.channel().id().asLongText());
        String taskName = msg.getSIM() + "_" + msg.getLogicChannel();
        if (msg.getDataType() != 3 && msg.getDataType() != 4) {
            if (task == null) {
                task = mediaPushManager.newPublishTask(ctx, taskName);
                task.start();
            }
            task.write(msg.getDataBody());
            if (msg.getPackageFlag() == 0 || msg.getPackageFlag() == 2) {
                task.flush();
                log.info("video timestamp : {}", Instant.ofEpochMilli(msg.getTimeStamp()).toString());
                log.info("video data_length : {}", msg.getDataBodyLength());
                log.info("video PackageFlag : {}", msg.getPackageFlag());
            }
        } else if (msg.getDataType() == 3) {
            log.info("audio timestamp : {}", Instant.ofEpochMilli(msg.getTimeStamp()).toString());
            log.info("audio data_length : {}", msg.getDataBodyLength());
            log.info("audio PackageFlag : {}", msg.getPackageFlag());
            task.writeAudio(msg.getDataBody());
            task.flushAudio();

            /*short[] samples = new short[40];
            ByteBuffer.wrap(msg.getDataBody()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
            ByteBuffer bb = ByteBuffer.allocate(samples.length * 2);
            bb.asShortBuffer().put(samples);
            ByteBuffer byteBuffer = ByteBuffer.wrap(bb.array());
            FileOutputStream fos = new FileOutputStream("audio.g726", true);
            FileOutputStream fos2 = new FileOutputStream("audio2.g726", true);
            FileChannel channel2 = fos2.getChannel();
            channel2.write(ByteBuffer.wrap(msg.getDataBody()));
            channel2.close();
            fos2.close();
            FileChannel channel = fos.getChannel();
            channel.write(byteBuffer);
            channel.close();
            fos.close();*/


            /*AVCodec codec = null;
            AVCodecContext avCodecContext = null;
            AVCodecParserContext avCodecParserContext = null;
            AVPacket avPacket = null;
            AVFrame avFrame = null;

            avPacket = avcodec.av_packet_alloc();
            codec = avcodec.avcodec_find_decoder(avcodec.AV_CODEC_ID_ADPCM_G726);
            avCodecParserContext = avcodec.av_parser_init(codec.id());
            avCodecContext = avcodec.avcodec_alloc_context3(codec);
            AVDictionary avDictionary = new AVDictionary();
            avcodec.avcodec_open2(avCodecContext, codec, avDictionary);
            avcodec.avcodec_send_packet(avCodecContext, avPacket);
            avcodec.avcodec_receive_frame(avCodecContext, avFrame);*/
        }
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }

}
