package push;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avcodec.AVCodecContext;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avutil.AVDictionary;
import org.bytedeco.ffmpeg.avutil.AVFrame;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.*;
import server.BusinessManager;
import util.BCD8421Operator;
import util.ByteOperator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.time.LocalDateTime;

import static org.bytedeco.ffmpeg.global.avcodec.avcodec_alloc_context3;
import static org.bytedeco.ffmpeg.global.avutil.AV_SAMPLE_FMT_S16;

@Slf4j
public class TalkBackPushTask extends Thread {

    private volatile boolean stop = false;

    private FFmpegFrameGrabber audioGrabber;
    private FFmpegFrameRecorder recorder;

    private PipedOutputStream apos;
    private PipedInputStream apis;

    private ByteArrayOutputStream abos = new ByteArrayOutputStream();

    TalkBackPushTask() throws IOException {

        apos = new PipedOutputStream();
        apis = new PipedInputStream(65536);
        apis.connect(apos);
    }

    @Override
    public void run() {

        try {
            audioGrabber = new FFmpegFrameGrabber(apis);
            audioGrabber.setFormat("s16le");
            audioGrabber.setSampleRate(16000);      // test
            //audioGrabber.setSampleRate(8000);     // formal
            audioGrabber.setSampleFormat(AV_SAMPLE_FMT_S16);
            audioGrabber.setSampleMode(FrameGrabber.SampleMode.SHORT);
            //audioGrabber.setAudioBitrate(128000);
            audioGrabber.setAudioChannels(1);
            audioGrabber.setAudioCodec(avcodec.AV_CODEC_ID_PCM_S16LE);
            audioGrabber.start();
            log.info("getAudioBitrate {}", audioGrabber.getAudioBitrate());
            log.info("getAudioChannels {}", audioGrabber.getAudioChannels());
            log.info("getAudioCodec {}", audioGrabber.getAudioCodec());
            log.info("getAudioFrameRate {}", audioGrabber.getAudioFrameRate());
            log.info("getAudioCodecName {}", audioGrabber.getAudioCodecName());
            log.info("getAudioMetadata {}", audioGrabber.getAudioMetadata());
            log.info("getAudioOptions {}", audioGrabber.getAudioOptions());
            log.info("getLengthInAudioFrames {}", audioGrabber.getLengthInAudioFrames());

            recorder = new FFmpegFrameRecorder("rtmp://202.194.14.72/myapp/test", 1);

            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);  // 0x15000 + 2
            recorder.setSampleRate(16000);      // test
            //recorder.setSampleRate(8000);     // formal
            recorder.setFormat("flv");

            recorder.start();


            while (!stop && !isInterrupted()) {

                Frame audioFrame = audioGrabber.grabSamples();

                if(audioFrame != null) {
                    log.info("time {}", LocalDateTime.now().toString());

                    recorder.recordSamples(audioFrame.samples);
                    log.info("frame length : {}", audioFrame.samples[0].capacity());
                    log.info("frame position : {}", audioFrame.samples[0].limit());
                    //log.info("{}", ((ShortBuffer)audioFrame.samples[0]).flip().array());
                    /*AVCodec codec = avcodec.avcodec_find_encoder(avcodec.AV_CODEC_ID_ADPCM_G726LE);
                    AVCodecContext c = avcodec_alloc_context3(codec);
                    c.bit_rate(40000);
                    c.sample_fmt(AV_SAMPLE_FMT_S16);
                    c.sample_rate(8000);
                    c.channels(1);
                    //c.channel_layout();
                    avcodec.avcodec_open2(c, codec, (AVDictionary) null);
                    AVPacket avPacket = avcodec.av_packet_alloc();
                    AVFrame avFrame = new AVFrame();
                    ByteBuffer buffer = ByteBuffer.allocate(audioFrame.samples[0].capacity() * 2);
                    ShortBuffer shortBuffer = (ShortBuffer) audioFrame.samples[0];
                    if (shortBuffer.hasArray()) {
                        buffer.asShortBuffer().put(shortBuffer.array());
                        avFrame.data(0, new BytePointer(buffer));
                        avcodec.avcodec_send_frame(c, avFrame);
                        avcodec.avcodec_receive_packet(c, avPacket);
                        byte[] data = avPacket.asByteBuffer().array();
                        log.info("{}", data.length);
                    }*/
                    /**
                     * 发送到设备
                     * test
                     */
                    /*MediaPushManager mediaPushManager = MediaPushManager.getInstance();
                    ChannelHandlerContext mediaCTX = mediaPushManager.getCTX("test");

                    byte M = 1;
                    byte PT = 98;
                    byte dataType = 0b0011;
                    byte packageFlag = 0b0000;
                    ByteBuffer buffer = ByteBuffer.allocate(audioFrame.samples[0].capacity() * 2);
                    buffer.asShortBuffer().put((ShortBuffer) audioFrame.samples[0].array());

                    BusinessManager businessManager = BusinessManager.getInstance();
                    byte[] out = ByteOperator.concatAll(
                        new byte[]{0x30, 0x31, 0x63, 0x64},
                        ByteOperator.intTo1Byte(0x81),
                        ByteOperator.intTo1Byte(M << 7 | (PT & 0x7f)),
                        ByteOperator.shortToBytes(businessManager.currentFlowId()),
                        BCD8421Operator.string2Bcd("15153139702"),
                        ByteOperator.intTo1Byte(dataType << 4 | (packageFlag & 0x0f)),
                        ByteOperator.longTo8Byte(System.currentTimeMillis()),
                        ByteOperator.intTo2Byte(audioFrame.samples[0].capacity()),
                        buffer.array()
                    );
                    mediaCTX.writeAndFlush(out);*/
                }

                sleep(20);  // 放在最后
            }
            log.info("close talkBack in"); // interrupt 的话不会被执行
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("close talkBack out");
    }

    public void shutdown() throws FrameGrabber.Exception, FrameRecorder.Exception {
        audioGrabber.stop();
        recorder.stop();
        recorder.release();
        interrupt();
        stop = true;
    }

    public void flushAudio() throws IOException {
        abos.flush();
        apos.write(abos.toByteArray());
        apos.flush();
        abos.reset();
    }

    public void writeAudio(byte[] dataBody) throws IOException {
        abos.write(dataBody);
    }
}
