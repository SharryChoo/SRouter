package com.sharry.sroutersupport.interceptors;

import android.content.Context;

import com.sharry.sroutersupport.data.Request;
import com.sharry.sroutersupport.data.Response;

/**
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public interface IInterceptor {

    Response process(Chain chain);

    /**
     * The chain for once navigation.
     */
    interface Chain {

        Request request();

        Context context();

        Response dispatch();
    }

}
