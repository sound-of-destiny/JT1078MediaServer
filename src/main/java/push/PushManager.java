package push;

import java.util.HashMap;
import java.util.Map;

public class PushManager {

    private static Map<String, PushTask> tasks = new HashMap<>();

    public static PushTask newPublishTask(String key, String name) {
        try {
            PushTask task = new PushTask(name);
            tasks.put(key, task);
            return task;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PushTask get(String name) {
        return tasks.get(name);
    }

    public static void remove(String key) {
        tasks.remove(key);
    }
}
