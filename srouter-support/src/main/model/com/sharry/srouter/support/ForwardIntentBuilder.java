package com.sharry.srouter.support;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

/**
 * Build a proxy intent that hold target info at {@link #EXTRA_TARGET_INFO}
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 2019-12-17 12:02
 */
public class ForwardIntentBuilder {

    static final String EXTRA_TARGET_INFO = "EXTRA_TARGET_INFO";
    static final String BUNDLE_EXTRA_URI = "BUNDLE_EXTRA_URI";
    static final String BUNDLE_EXTRA_AUTHORITY = "BUNDLE_EXTRA_AUTHORITY";
    static final String BUNDLE_EXTRA_PATH = "BUNDLE_EXTRA_PATH";

    private String mForwardActivityClassName = DefaultForwardActivity.class.getName();
    private final Bundle mTargetInfo;

    ForwardIntentBuilder() {
        this.mTargetInfo = new Bundle();
    }

    /**
     * add target page uri
     */
    public ForwardIntentBuilder uri(@Nullable String uri) {
        mTargetInfo.putString(BUNDLE_EXTRA_URI, uri);
        return this;
    }

    /**
     * add target page authority and path
     */
    public ForwardIntentBuilder uri(@Nullable String authority, @Nullable String path) {
        mTargetInfo.putString(BUNDLE_EXTRA_AUTHORITY, authority);
        mTargetInfo.putString(BUNDLE_EXTRA_PATH, path);
        return this;
    }

    /**
     * Add other data.
     */
    public ForwardIntentBuilder addExtra(Bundle newDatum) {
        mTargetInfo.putAll(newDatum);
        return this;
    }

    /**
     * Set the delegate activity.
     */
    public ForwardIntentBuilder forwardActivity(@Nullable Class<? extends Activity> forwardActivityClass) {
        if (forwardActivityClass != null) {
            forwardActivity(forwardActivityClass.getName());
        }
        return this;
    }

    /**
     * Set the delegate activity.
     */
    public ForwardIntentBuilder forwardActivity(@Nullable String activityClassName) {
        if (!TextUtils.isEmpty(activityClassName)) {
            mForwardActivityClassName = activityClassName;
        }
        return this;
    }

    public Intent build() {
        Intent intent = new Intent();
        // Jump to delegate activity.
        intent.setClassName(SRouterImpl.sAppContext, mForwardActivityClassName);
        // Hold real target info.
        intent.putExtra(EXTRA_TARGET_INFO, mTargetInfo);
        return intent;
    }

}
