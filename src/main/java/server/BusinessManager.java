package server;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BusinessManager {

    private volatile short currentFlowId = 0;
    private Map<String, ChannelHandlerContext> ctxMap;

    public static BusinessManager getInstance() {
        return BusinessManager.Singleton.INSTANCE.getSingleton();
    }

    private BusinessManager() {
         ctxMap = new ConcurrentHashMap<>();
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

    public synchronized void put(String name, ChannelHandlerContext ctx) {
        ctxMap.put(name, ctx);
    }

    public synchronized ChannelHandlerContext get(String name) {
        return ctxMap.get(name);
    }

    public synchronized short currentFlowId() {
        if (currentFlowId == 0x7fff)
            currentFlowId = 0;
        return currentFlowId++;
    }
}
