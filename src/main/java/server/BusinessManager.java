package server;

public class BusinessManager {

    private volatile short currentFlowId = 0;

    public static BusinessManager getInstance() {
        return BusinessManager.Singleton.INSTANCE.getSingleton();
    }

    private BusinessManager() {
    }

    private enum Singleton {
        INSTANCE;

        private BusinessManager singleton;

        Singleton() {
            singleton = new BusinessManager();
        }

        public BusinessManager getSingleton() {
            return singleton;
        }
    }

    public synchronized short currentFlowId() {
        if (currentFlowId == 0x7fff)
            currentFlowId = 0;
        return currentFlowId++;
    }
}
