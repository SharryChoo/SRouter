package com.sharry.srouter.support.interceptors;

import androidx.annotation.NonNull;

import com.sharry.srouter.support.data.Response;

/**
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public interface IInterceptor {

    Response intercept(@NonNull Chain chain);

    /**
     * The chain for once navigation.
     */
    interface Chain {

        ChainContext chainContext();

        Response dispatch();

    }

}
