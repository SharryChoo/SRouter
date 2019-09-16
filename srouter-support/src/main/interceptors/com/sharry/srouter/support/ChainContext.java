package com.sharry.srouter.support;

import android.content.Context;
import android.content.ContextWrapper;

import androidx.annotation.NonNull;

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
    static ChainContext obtain(@NonNull Context context,
                                      @NonNull Request request,
                                      @NonNull ICancelable cancelable) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(request);
        return new ChainContext(context, request, cancelable);
    }

    public ICancelable cancelable;
    public Request request;

    private ChainContext(Context base, Request request, ICancelable cancelable) {
        super(base);
        this.request = request;
        this.cancelable = cancelable;
    }

}
