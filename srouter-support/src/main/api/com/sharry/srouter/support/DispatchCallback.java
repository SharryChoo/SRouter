package com.sharry.srouter.support;

import androidx.annotation.NonNull;

/**
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 2019-12-17 13:30
 */
public interface DispatchCallback {

    void onSuccess(@NonNull Response response);

    void onFailed(Throwable throwable);

    void onCanceled();

}
