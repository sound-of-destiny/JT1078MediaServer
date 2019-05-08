package push;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TalkBackPushManager {


    private Map<String, TalkBackPushTask> tasks;

    public static TalkBackPushManager getInstance() {
        return TalkBackPushManager.Singleton.INSTANCE.getSingleton();
    }

    private TalkBackPushManager() {
        tasks = new ConcurrentHashMap<>();
    }

    private enum Singleton {
        INSTANCE;

        private TalkBackPushManager singleton;

        Singleton() {
            singleton = new TalkBackPushManager();
        }

        public TalkBackPushManager getSingleton() {
            return singleton;
        }
    }

    public synchronized TalkBackPushTask newPublishTask(String key) {
        try {
            TalkBackPushTask task = new TalkBackPushTask();
            tasks.put(key, task);
            return task;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized TalkBackPushTask get(String name) {
        return tasks.get(name);
    }

    public synchronized void remove(String key) {
        tasks.remove(key);
    }
}
