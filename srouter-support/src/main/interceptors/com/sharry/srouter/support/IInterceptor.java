package com.sharry.srouter.support;

import androidx.annotation.NonNull;

/**
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public interface IInterceptor {

    void intercept(@NonNull Chain chain);

    /**
     * The chain for once navigation.
     */
    interface Chain {

        ChainContext chainContext();

        ChainCallback callback();

        void dispatch();
    }

    interface ChainCallback {
        void onSuccess(@NonNull Response response);

        void onFailed(Throwable throwable);

        void onCanceled();
    }

}
