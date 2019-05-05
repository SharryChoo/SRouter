package com.sharry.srouter.support.scheduler;

import android.os.Handler;
import android.os.Looper;

/**
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-05-05
 */
public class HandlerScheduler implements Scheduler {

    public static HandlerScheduler getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void schedule(Runnable runnable, long delay) {
        mHandler.postDelayed(runnable, delay);
    }

    private static class InstanceHolder {
        private static final HandlerScheduler INSTANCE = new HandlerScheduler();
    }

}
