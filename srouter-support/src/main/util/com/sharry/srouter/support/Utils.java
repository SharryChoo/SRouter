package com.sharry.srouter.support;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.annotation.Nullable;

/**
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-05-06
 */
class Utils {

    /**
     * Find context mapper special Activity.
     */
    @Nullable
    static Activity findActivity(@Nullable Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) context).getBaseContext());
        } else {
            return null;
        }
    }

    /**
     * Ensure activity state.
     */
    static boolean isIllegalState(@Nullable Activity activity) {
        return activity == null || activity.isFinishing() || (isJellyBeanMr1() && activity.isDestroyed());
    }

    static boolean isJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    static boolean isJellyBeanMr1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

}
