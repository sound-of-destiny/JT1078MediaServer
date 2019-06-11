package push;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_ADPCM_G726LE;
import static org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P;
import static org.bytedeco.ffmpeg.global.avutil.AV_SAMPLE_FMT_S16;

@Slf4j
public class AudioPushTask extends Thread {

    public volatile boolean stop = false;

    private FFmpegFrameGrabber audioGrabber;
    private FFmpegFrameRecorder recorder;

    private PipedOutputStream apos;
    private PipedInputStream apis;

    private ByteArrayOutputStream abos = new ByteArrayOutputStream();

    public AudioPushTask(FFmpegFrameRecorder recorder) throws IOException {
        this.recorder = recorder;

        apos = new PipedOutputStream();
        apis = new PipedInputStream(65536);
        apis.connect(apos);
    }

    @Override
    public void run() {
        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {

            audioGrabber = new FFmpegFrameGrabber(apis);
            audioGrabber.setFormat("g726le");
            audioGrabber.setSampleRate(8000);
            audioGrabber.setSampleFormat(AV_SAMPLE_FMT_S16);
            audioGrabber.setSampleMode(FrameGrabber.SampleMode.SHORT);
            // audioGrabber.setAudioBitrate(40000);
            audioGrabber.setAudioChannels(1);
            // audioGrabber.setAudioCodec(AV_CODEC_ID_ADPCM_G726LE);
            audioGrabber.start();

            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);  // 0x15000 + 2
            // recorder.setAudioCodec(avcodec.AV_CODEC_ID_ADPCM_G726);
            // recorder.setFormat("flv"); // rtmp
            // recorder.setFormat("flac"); // rtmp
            // recorder.setSampleFormat(AV_SAMPLE_FMT_S16);

            recorder.start();

            while (!stop && !this.isInterrupted()) {
                Frame aframe = audioGrabber.grabSamples();
                if(aframe != null) {
                    log.info("voice");
                    recorder.recordSamples(8000, 1, aframe.samples);
                }
                // Thread.sleep(20);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void shutdown() throws FrameGrabber.Exception, FrameRecorder.Exception {
        audioGrabber.stop();
        recorder.stop();
        recorder.release();
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
