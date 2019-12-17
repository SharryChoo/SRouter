package com.sharry.srouter.module.base;

import android.util.Log;

import androidx.annotation.NonNull;

import com.sharry.srouter.support.IInterceptor;

/**
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019/9/16
 */
public class LoggerGlobalInterceptor implements IInterceptor {

    private static final String TAG = LoggerGlobalInterceptor.class.getSimpleName();

    @Override
    public void intercept(@NonNull Chain chain) {
        Log.e(TAG, chain.context().request.toString());
        chain.dispatch();
    }

}
