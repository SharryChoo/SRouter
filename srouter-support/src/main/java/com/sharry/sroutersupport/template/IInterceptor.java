package com.sharry.sroutersupport.template;

import android.content.Context;

import com.sharry.sroutersupport.data.Request;
import com.sharry.sroutersupport.data.Result;

/**
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public interface IInterceptor {

    Result process(Chain chain);

    /**
     * The chain for once navigation.
     */
    interface Chain {

        Request request();

        Context context();

        Result dispatch();
    }

}
