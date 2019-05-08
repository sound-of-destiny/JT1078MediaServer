package push;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MediaPushManager {

    private Map<String, MediaPushTask> tasks;
    private Map<String, ChannelHandlerContext> bus;

    public static MediaPushManager getInstance() {
        return Singleton.INSTANCE.getSingleton();
    }

    private MediaPushManager() {
        tasks = new ConcurrentHashMap<>();
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

    public synchronized MediaPushTask newPublishTask(ChannelHandlerContext key, String name) {
        try {
            MediaPushTask task = new MediaPushTask(name);
            tasks.put(key.channel().id().asLongText(), task);
            bus.put(name, key);
            return task;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized MediaPushTask getTask(String name) {
        return tasks.get(name);
    }

    public synchronized ChannelHandlerContext getCTX(String name) {
        return bus.get(name);
    }

    public synchronized void remove(String name, ChannelHandlerContext key) {
        tasks.remove(key.channel().id().asLongText());
        bus.remove(name);
    }
}
