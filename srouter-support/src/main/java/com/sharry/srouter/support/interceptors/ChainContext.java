package com.sharry.srouter.support.interceptors;

import android.content.Context;
import android.content.ContextWrapper;

import androidx.annotation.NonNull;

import com.sharry.srouter.support.call.Cancelable;
import com.sharry.srouter.support.data.Request;
import com.sharry.srouter.support.utils.Preconditions;

/**
 * The context associated with dispatching.
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 5/6/2019 2:35 PM
 */
public class ChainContext extends ContextWrapper {

    /**
     * Get an instance of ChainContext.
     */
    public static ChainContext obtain(@NonNull Context context,
                                      @NonNull Request request,
                                      @NonNull Cancelable cancelable) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(request);
        return new ChainContext(context, request, cancelable);
    }

    public Cancelable cancelable;
    public Request request;

    private ChainContext(Context base, Request request, Cancelable cancelable) {
        super(base);
        this.request = request;
        this.cancelable = cancelable;
    }

}
