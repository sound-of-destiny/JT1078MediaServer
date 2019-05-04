import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;

import javax.sound.sampled.*;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.concurrent.*;

public class SoundRecordTest {
    public static void main(String[] args) throws Exception {
        /**
         * 设置音频编码器 最好是系统支持的格式，否则getLine() 会发生错误
         * 采样率:44.1k;采样率位数:16位;立体声(stereo);是否签名;true:
         * big-endian字节顺序,false:little-endian字节顺序(详见:ByteOrder类)
         */
        AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);
        System.out.println("准备开启音频！");
        // 通过AudioSystem获取本地音频混合器信息
        Mixer.Info[] minfoSet = AudioSystem.getMixerInfo();
        System.out.println("size:" + minfoSet.length);
        Arrays.asList(minfoSet).forEach(System.out::println);
        // 通过AudioSystem获取本地音频混合器
        Mixer mixer = AudioSystem.getMixer(minfoSet[3]);
        // 通过设置好的音频编解码器获取数据线信息
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        System.out.println("dataLineInfo : " + dataLineInfo.toString());


        // 打开并开始捕获音频
        // 通过line可以获得更多控制权
        // 获取设备：
        // TargetDataLine line
        // =(TargetDataLine)mixer.getLine(dataLineInfo);
        Line dataline = null;
        try {
            dataline = AudioSystem.getLine(dataLineInfo);
        } catch (LineUnavailableException e2) {
            System.err.println("开启失败...");
        }
        TargetDataLine line = (TargetDataLine) dataline;

        try {
            line.open(audioFormat);
        } catch (LineUnavailableException e1) {
            line.stop();
            try {
                line.open(audioFormat);
            } catch (LineUnavailableException e) {
                System.err.println("按照指定音频编码器打开失败...");
            }
        }
        System.out.println(line.getBufferSize());
        line.start();
        System.out.println(line.getBufferSize());
        System.out.println("已经开启音频！");
        // 获得当前音频采样率
        int sampleRate = (int) audioFormat.getSampleRate();
        // 获取当前音频通道数量
        int numChannels = audioFormat.getChannels();
        // 初始化音频缓冲区(size是音频采样率*通道数)
        System.out.println("sampleRate :" + sampleRate + " numChannels :" + numChannels);
        int audioBufferSize = sampleRate * numChannels;
        byte[] audioBytes = new byte[audioBufferSize];

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder("output.flv",2);

        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);  // 0x15000 + 2
        recorder.start();
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        ScheduledFuture tasker = scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            int nBytesRead;
            int nSamplesRead;

            System.out.println("读取音频数据...");
            // 非阻塞方式读取
            System.out.println("getBufferSize" + line.getBufferSize());
            System.out.println("line.available()" + line.available());
            nBytesRead = line.read(audioBytes, 0, line.available());
            // 因为我们设置的是16位音频格式,所以需要将byte[]转成short[]
            nSamplesRead = nBytesRead / 2;
            short[] samples = new short[nSamplesRead];
            /**
             * ByteBuffer.wrap(audioBytes)-将byte[]数组包装到缓冲区
             * ByteBuffer.order(ByteOrder)-按little-endian修改字节顺序，解码器定义的
             * ByteBuffer.asShortBuffer()-创建一个新的short[]缓冲区
             * ShortBuffer.get(samples)-将缓冲区里short数据传输到short[]
             */
            ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
            // 将short[]包装到ShortBuffer
            ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);

            System.out.println("sBuff.remaining() : " + sBuff.remaining());
            // 按通道录制shortBuffer
            try {
                System.out.println("录制音频数据...");
                recorder.recordSamples(sampleRate, numChannels, sBuff);
            } catch (Exception e) {
                System.out.println("wrong");
                e.printStackTrace();
            }

        }, 0, (long) 1000 / 25, TimeUnit.MILLISECONDS);
        Thread.sleep(5 * 1000);
        recorder.stop();
        tasker.cancel(true);
        if (!scheduledThreadPoolExecutor.isShutdown()) {
            scheduledThreadPoolExecutor.shutdownNow();
        }

    }



}
