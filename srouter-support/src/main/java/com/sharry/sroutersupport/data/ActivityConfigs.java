package com.sharry.sroutersupport.data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;

/**
 * The config for navigation request.
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 2/22/2019 3:28 PM
 */
public class ActivityConfigs {

    public static final int NON_REQUEST_CODE = -1;
    public static final int NON_FLAGS = -1;

    /**
     * The Flag for the route navigation.
     */
    private int flags = NON_FLAGS;

    /**
     * The requestCode for the requestCode.
     */
    private int requestCode = NON_REQUEST_CODE;

    /**
     * The jump activity configs for the request.
     */
    private ActivityOptionsCompat activityOptions;

    /**
     * The call back for activity result.
     */
    private Callback callback;

    private ActivityConfigs() {

    }

    public int getRequestCode() {
        return requestCode;
    }

    public int getFlags() {
        return flags;
    }

    public ActivityOptionsCompat getActivityOptions() {
        return activityOptions;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public Callback getCallback() {
        return callback;
    }

    public static class Builder {

        ActivityConfigs configs;

        public Builder() {
            this.configs = new ActivityConfigs();
        }

        /**
         * Set activity configs when jump to other page.
         */
        public Builder setActivityOptions(@NonNull ActivityOptionsCompat options) {
            configs.activityOptions = options;
            return this;
        }

        /**
         * Set callback for a navigation request, it will called when routed activity finished.
         */
        public Builder setActivityCallback(Callback callback) {
            this.configs.callback = callback;
            return this;
        }

        /**
         * Set request code for the navigation.
         */
        public Builder setRequestCode(int requestCode) {
            configs.requestCode = requestCode;
            return this;
        }

        /**
         * Set special flags controlling how this intent is handled.  Most values
         * here depend on the type of component being executed by the Intent,
         * specifically the FLAG_ACTIVITY_* flags are all for use with
         * {@link Context#startActivity Context.startActivity()} and the
         * FLAG_RECEIVER_* flags are all for use with
         * {@link Context#sendBroadcast(Intent) Context.sendBroadcast()}.
         */
        public Builder withFlags(@Request.FlagInt int flag) {
            configs.flags = flag;
            return this;
        }

        public ActivityConfigs build() {
            return configs;
        }

    }

    public interface Callback {
        void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
    }
}
