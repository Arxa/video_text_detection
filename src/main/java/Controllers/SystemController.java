package Controllers;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Status;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by arxa on 26/2/2017.
 */

public class SystemController
{
    private static Cache cache;
    private static CacheManager cm;
    private static ExecutorService mainExecutorService;

    public static CacheManager getCm() {
        return cm;
    }

    public static void initSystem()
    {
        cm  = CacheManager.newInstance();
        cache = cm.getCache("cache1");
        mainExecutorService = Executors.newFixedThreadPool(3); // Creating 3 threads in a thread pool
    }

    public static void closeSystem()
    {
        closeVideoHandlers();
        if (getCm().getStatus() == Status.STATUS_ALIVE){
            getCm().removeAllCaches();
            getCm().shutdown();
        }
        if (!mainExecutorService.isShutdown()){
            mainExecutorService.shutdown();
        }
    }

    public static void closeVideoHandlers()
    {
        if (Writer.getOutput() != null){
            Writer.getOutput().release(); // Closing VideoWriter
        }
        if (VideoProcessor.getCap() != null) {
            VideoProcessor.getCap().release(); // Closing VideoCapture
        }
    }

    public static Cache getCache() {
        return cache;
    }

    public static ExecutorService getExecutorService() {
        return mainExecutorService;
    }
}
