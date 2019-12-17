package com.sharry.srouter.support;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

/**
 * Build a proxy intent that hold target info at {@link #PROXY_EXTRA_TARGET_INFO}
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 2019-12-17 12:02
 */
public class ProxyIntentBuilder {

    static final String PROXY_EXTRA_TARGET_INFO = "PROXY_EXTRA_TARGET_INFO";
    static final String BUNDLE_EXTRA_URI = "BUNDLE_EXTRA_URI";
    static final String BUNDLE_EXTRA_AUTHORITY = "BUNDLE_EXTRA_AUTHORITY";
    static final String BUNDLE_EXTRA_PATH = "BUNDLE_EXTRA_PATH";

    private String mProxyActivityClassName = DefaultProxyActivity.class.getName();
    private final Bundle mTargetInfo;

    ProxyIntentBuilder() {
        this.mTargetInfo = new Bundle();
    }

    /**
     * add target page uri
     */
    public ProxyIntentBuilder uri(@Nullable String uri) {
        mTargetInfo.putString(BUNDLE_EXTRA_URI, uri);
        return this;
    }

    /**
     * add target page authority and path
     */
    public ProxyIntentBuilder uri(@Nullable String authority, @Nullable String path) {
        mTargetInfo.putString(BUNDLE_EXTRA_AUTHORITY, authority);
        mTargetInfo.putString(BUNDLE_EXTRA_PATH, path);
        return this;
    }

    /**
     * Add other data.
     */
    public ProxyIntentBuilder addExtra(Bundle newDatum) {
        mTargetInfo.putAll(newDatum);
        return this;
    }

    /**
     * Set the delegate activity.
     */
    public ProxyIntentBuilder proxyActivity(@Nullable Class<? extends Activity> proxyActivityClass) {
        if (proxyActivityClass != null) {
            proxyActivity(proxyActivityClass.getName());
        }
        return this;
    }

    /**
     * Set the delegate activity.
     */
    public ProxyIntentBuilder proxyActivity(@Nullable String activityClassName) {
        if (!TextUtils.isEmpty(activityClassName)) {
            mProxyActivityClassName = activityClassName;
        }
        return this;
    }

    public Intent build() {
        Intent intent = new Intent();
        // Jump to delegate activity.
        intent.setClassName(SRouterImpl.sAppContext, mProxyActivityClassName);
        // Hold real target info.
        intent.putExtra(PROXY_EXTRA_TARGET_INFO, mTargetInfo);
        return intent;
    }

}
