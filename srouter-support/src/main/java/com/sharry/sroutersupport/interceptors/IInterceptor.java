package com.sharry.sroutersupport.interceptors;

import android.content.Context;

import com.sharry.sroutersupport.data.NavigationRequest;
import com.sharry.sroutersupport.data.NavigationResponse;

/**
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public interface IInterceptor {

    NavigationResponse process(Chain chain);

    /**
     * The chain for once navigation.
     */
    interface Chain {

        NavigationRequest request();

        Context context();

        NavigationResponse dispatch();
    }

}
