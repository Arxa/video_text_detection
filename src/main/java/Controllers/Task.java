package Controllers;

import java.util.concurrent.Callable;

/**
 * Created by arxa on 15/4/2017.
 */

public class Task implements Callable<String>
{
    @Override
    public String call() throws Exception {
        Thread.sleep(4000); // Just to demo a long running task of 4 seconds.
        return "Ready!";
    }
}
