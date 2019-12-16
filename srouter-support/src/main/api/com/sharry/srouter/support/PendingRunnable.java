package com.sharry.srouter.support;

import android.app.Activity;

import androidx.annotation.NonNull;

/**
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-12-16
 */
public interface PendingRunnable {

    /**
     * Run when PendingIntent triggered.
     *
     * @param hookActivity hooked pendingIntent activity.
     */
    void run(@NonNull Activity hookActivity);

}
