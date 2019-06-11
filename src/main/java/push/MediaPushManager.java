package push;

import org.bytedeco.javacv.FFmpegFrameRecorder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MediaPushManager {
    private String MY_APP = "rtmp://202.194.14.72/myapp/";
    private Map<String, VideoPushTask> videoTasks;
    private Map<String, AudioPushTask> audioTasks;
    private Map<String, FFmpegFrameRecorder> bus;

    public static MediaPushManager getInstance() {
        return Singleton.INSTANCE.getSingleton();
    }

    private MediaPushManager() {
        videoTasks = new ConcurrentHashMap<>();
        audioTasks = new ConcurrentHashMap<>();
        bus = new ConcurrentHashMap<>();
    }

    private enum Singleton {
        INSTANCE;

        private MediaPushManager singleton;

        Singleton() {
            singleton = new MediaPushManager();
        }

        public MediaPushManager getSingleton() {
            return singleton;
        }
    }

    public synchronized FFmpegFrameRecorder getRecorder(String key) {
        return bus.get(key);
    }

    public synchronized void newVideoPublishTask(String key) {
        try {
            FFmpegFrameRecorder recorder = getRecorder(key);
            if (recorder == null) {
                recorder = new FFmpegFrameRecorder(MY_APP + key, 704, 576);
                bus.put(key, recorder);
            }
            VideoPushTask videoTask = new VideoPushTask(recorder);
            videoTasks.put(key, videoTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void newAudioPublishTask(String key) {
        try {
            FFmpegFrameRecorder recorder = getRecorder(key);
            if (recorder == null) {
                recorder = new FFmpegFrameRecorder(MY_APP + key, 1);
                bus.put(key, recorder);
            }
            AudioPushTask audioTask = new AudioPushTask(recorder);
            audioTasks.put(key, audioTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized VideoPushTask getVideoTask(String key) {
        return videoTasks.get(key);
    }

    public synchronized AudioPushTask getAudioTask(String key) {
        return audioTasks.get(key);
    }

    public synchronized void remove(String key) {
        videoTasks.remove(key);
        audioTasks.remove(key);
        bus.remove(key);
    }
}
