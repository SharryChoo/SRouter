package com.sharry.srouter.support;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * The lambda callback use at {@link SRouter#navigation(Context, Request, LambdaCallback)}
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 5/6/2019 2:22 PM
 */
public interface LambdaCallback {

    void onDispatched(@NonNull Response response);

}