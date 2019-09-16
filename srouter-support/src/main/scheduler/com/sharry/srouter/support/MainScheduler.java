package com.sharry.srouter.support;

import android.os.Handler;
import android.os.Looper;

/**
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-05-05
 */
class MainScheduler implements IScheduler {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public static MainScheduler getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void schedule(Runnable runnable) {
        schedule(runnable, 0);
    }

    @Override
    public void schedule(Runnable runnable, long delay) {
        mHandler.postDelayed(runnable, delay);
    }

    private static class InstanceHolder {
        private static final MainScheduler INSTANCE = new MainScheduler();
    }

}
