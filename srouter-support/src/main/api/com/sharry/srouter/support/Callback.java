package com.sharry.srouter.support;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * The lambda callback use at {@link SRouter#navigation(Context, Request, Callback)}
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 5/6/2019 2:22 PM
 */
public interface Callback {

    void onSuccess(@NonNull Response response);

}