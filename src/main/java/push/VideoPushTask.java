package push;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P;

@Slf4j
public class VideoPushTask extends Thread {

    public volatile boolean stop = false;

    private FFmpegFrameGrabber grabber;
    private FFmpegFrameRecorder recorder;

    private PipedOutputStream pos;
    private PipedInputStream pis;

    private ByteArrayOutputStream bos = new ByteArrayOutputStream();

    public VideoPushTask(FFmpegFrameRecorder recorder) throws IOException {
        this.recorder = recorder;
        pos = new PipedOutputStream();
        pis = new PipedInputStream(65536);
        pis.connect(pos);
    }

    @Override
    public void run() {
        try {
            grabber = new FFmpegFrameGrabber(pis);
            grabber.setFormat("h264");
            grabber.start();

            // recorder.setInterleaved(true);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // 27
            recorder.setFormat("flv"); // rtmp
            recorder.setFrameRate(25); // 设低码率不卡本来25
            recorder.setPixelFormat(AV_PIX_FMT_YUV420P);
            // recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            recorder.start();

            /*CanvasFrame canvasFrame = new CanvasFrame("camera", CanvasFrame.getDefaultGamma() / grabber.getGamma());
            canvasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            canvasFrame.setAlwaysOnTop(true);*/

            while (!stop && !this.isInterrupted()) {

                Frame frame = grabber.grab();

                if(frame != null) {
                    log.info("video");
                    recorder.record(frame);
                    /*canvasFrame.showImage(frame);*/ // 主码流不卡, 子码流卡
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void write(byte[] buff) throws IOException {
        bos.write(buff);
    }

    public void flush() throws IOException {
        bos.flush();
        pos.write(bos.toByteArray());
        pos.flush();
        bos.reset();
    }

    public void shutdown() throws FrameGrabber.Exception, FrameRecorder.Exception {
        grabber.stop();
        //audioGrabber.stop();
        recorder.stop();
        recorder.release();
        interrupt();
    }
}
