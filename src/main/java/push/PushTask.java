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
import static org.bytedeco.ffmpeg.global.avutil.AV_SAMPLE_FMT_S16;

@Slf4j
public class PushTask extends Thread {

    private volatile boolean stop = false;

    private String name;

    private FFmpegFrameGrabber grabber;
    private FFmpegFrameGrabber audioGrabber;
    private FFmpegFrameRecorder recorder;

    private PipedOutputStream pos;
    private PipedInputStream pis;
    private PipedOutputStream apos;
    private PipedInputStream apis;

    private ByteArrayOutputStream bos = new ByteArrayOutputStream();
    private ByteArrayOutputStream abos = new ByteArrayOutputStream();

    public PushTask(String name) throws IOException {
        this.name = name;
        pos = new PipedOutputStream();
        pis = new PipedInputStream(65536);
        pis.connect(pos);

        apos = new PipedOutputStream();
        apis = new PipedInputStream(65536);
        apis.connect(apos);
    }

    @Override
    public void run() {

        try {
            //grabber = new FFmpegFrameGrabber(pis);
            //grabber.start();

            audioGrabber = new FFmpegFrameGrabber(apis);
            //audioGrabber.setFormat("flv");
            audioGrabber.setSampleRate(8000);
            audioGrabber.setSampleFormat(AV_SAMPLE_FMT_S16);
            audioGrabber.setSampleMode(FrameGrabber.SampleMode.SHORT);
            //audioGrabber.setAudioBitrate(128000);
            audioGrabber.setAudioChannels(1);
            audioGrabber.setAudioCodec(avcodec.AV_CODEC_ID_ADPCM_G726LE);
            audioGrabber.start();

            log.info("getImageHeight {}", grabber.getImageHeight());
            log.info("getImageWidth {}", grabber.getImageWidth());
            log.info("getFormat {}", grabber.getFormat());
            log.info("getPixelFormat {}", grabber.getPixelFormat());
            log.info("getFrameRate {}", grabber.getFrameRate());
            log.info("getMetadata {}", grabber.getMetadata());
            log.info("getVideoMetadata {}", grabber.getVideoMetadata());
            log.info("getVideoBitrate {}", grabber.getVideoBitrate());
            log.info("getVideoFrameRate {}", grabber.getVideoFrameRate());
            log.info("getVideoCodec {}", grabber.getVideoCodec());
            log.info("getLengthInVideoFrames {}", grabber.getLengthInVideoFrames());
            log.info("getLengthInTime {}", grabber.getLengthInTime());
            log.info("getAudioBitrate {}", audioGrabber.getAudioBitrate());
            log.info("getAudioChannels {}", audioGrabber.getAudioChannels());
            log.info("getAudioCodec {}", audioGrabber.getAudioCodec());
            log.info("getAudioFrameRate {}", audioGrabber.getAudioFrameRate());
            log.info("getAudioCodecName {}", audioGrabber.getAudioCodecName());
            log.info("getAudioMetadata {}", audioGrabber.getAudioMetadata());
            log.info("getAudioCodecName {}", audioGrabber.getAudioOptions());
            log.info("getLengthInAudioFrames {}", audioGrabber.getLengthInAudioFrames());
            log.info("getAudioStream {}", audioGrabber.getAudioStream());

            recorder = new FFmpegFrameRecorder("rtmp://202.194.14.72/myapp/test", 704, 576,1);

            recorder.setInterleaved(true);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // 27
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);  // 0x15000 + 2
            //recorder.setAudioCodec(avcodec.AV_CODEC_ID_ADPCM_G726);
            recorder.setFormat("flv"); // rtmp
            recorder.setFrameRate(25); // 设低码率不卡本来25
            //recorder.setPixelFormat(0);
            recorder.setPixelFormat(AV_PIX_FMT_YUV420P);
            //recorder.setSampleFormat(AV_SAMPLE_FMT_S16);

            recorder.start();

            /*Frame audioFrame = new Frame();
            ShortBuffer audioBuffer = ShortBuffer.allocate(64 * 1024);
            audioFrame.sampleRate = 8000;
            audioFrame.audioChannels = 1;
            audioFrame.samples = new ShortBuffer[] {audioBuffer};
            for (int i = 0; i < audioBuffer.capacity(); i++) {
                audioBuffer.put(i, (short)i);
            }
            recorder.record(audioFrame);*/


            CanvasFrame canvasFrame = new CanvasFrame("camera", CanvasFrame.getDefaultGamma() / grabber.getGamma());
            canvasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            canvasFrame.setAlwaysOnTop(true);

            while (!stop && !this.isInterrupted()) {

                //Frame aframe = agrabber.grab();
                //recorder.recordSamples(aframe.samples);


                //Frame frame = grabber.grab();
                Frame aframe = audioGrabber.grabSamples();

                /*if(frame != null) {
                    recorder.record(frame);
                    canvasFrame.showImage(frame); // 主码流不卡, 子码流卡
                } */
                if(aframe != null) {
                    log.info("音频");
                    recorder.recordSamples(aframe.samples);
                }

                /*if (keyFrame.keyFrame) {
                    canvasFrame.showImage(keyFrame);
                }*/

                //OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
                /*Mat grabbedImage = converter.convert(frame);
                int height = grabbedImage.rows();
                int width = grabbedImage.cols();*/

                //opencv_imgcodecs.cvSaveImage("hello.jpg", grabbedImage);
                //log.info("{}", height + " " + width);

                /*Frame rotatedFrame = converter.convert(grabbedImage);

                if (startTime == 0) {
                    startTime = System.currentTimeMillis();
                }
                recorder.setTimestamp(1000 * (System.currentTimeMillis() - startTime));//时间戳
                if(rotatedFrame!=null){
                    recorder.record(rotatedFrame);
                }*/

                //Thread.sleep(40);

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
        audioGrabber.stop();
        recorder.stop();
        recorder.release();
    }

    public void flusha() throws IOException {
        abos.flush();
        apos.write(abos.toByteArray());
        apos.flush();
        abos.reset();
    }

    public void writea(byte[] dataBody) throws IOException {
        abos.write(dataBody);
    }
}
