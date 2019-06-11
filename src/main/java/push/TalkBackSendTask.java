package push;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.*;
import server.BusinessManager;
import util.BCD8421Operator;
import util.ByteOperator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_ADPCM_G726LE;
import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_PCM_S16LE;

@Slf4j
public class TalkBackSendTask extends Thread {

    private volatile boolean stop = false;
    private FFmpegFrameGrabber audioGrabber;
    private FFmpegFrameRecorder recorder;

    private PipedOutputStream apos;
    private PipedInputStream apis;

    private ByteArrayOutputStream abos = new ByteArrayOutputStream();

    TalkBackSendTask() throws IOException {
        apos = new PipedOutputStream();
        apis = new PipedInputStream(65536);
        apis.connect(apos);
    }

    @Override
    public void run() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            audioGrabber = new FFmpegFrameGrabber(apis);
            audioGrabber.setFormat("s16le");
            audioGrabber.setSampleRate(8000);     // formal
            //audioGrabber.setSampleFormat(AV_SAMPLE_FMT_S16);
            audioGrabber.setSampleMode(FrameGrabber.SampleMode.SHORT);
            audioGrabber.setAudioChannels(1);
            audioGrabber.setAudioCodec(AV_CODEC_ID_PCM_S16LE);
            audioGrabber.start();

            recorder = new FFmpegFrameRecorder(baos, 1);
            recorder.setAudioCodec(AV_CODEC_ID_ADPCM_G726LE);  // 0x15000 + 2
            recorder.setFormat("g726le");
            recorder.start();

            while (!stop && !isInterrupted()) {

                Frame audioFrame = audioGrabber.grabSamples();

                if(audioFrame != null) {
                    recorder.recordSamples(audioFrame.samples);
                    baos.flush();
                    byte[] data = baos.toByteArray();
                    log.info("data {}", data);
                    log.info("data size {}", data.length);
                    /**
                     * 发送到设备
                     * test
                     */
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
                    //ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.copiedBuffer(out)));
                    baos.reset();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("close talkBack out");
    }

    public void shutdown() throws FrameGrabber.Exception, FrameRecorder.Exception {
        audioGrabber.stop();
        recorder.stop();
        recorder.release();
        stop = true;
        interrupt();
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
