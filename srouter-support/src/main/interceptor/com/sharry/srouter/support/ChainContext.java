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
        Preconditions.checkNotNull(cancelable);
        return new ChainContext(context, request, cancelable);
    }

    @NonNull
    public ICancelable cancelable;

    @NonNull
    public Request request;

    private ChainContext(Context base, @NonNull Request request, @NonNull ICancelable cancelable) {
        super(base);
        this.request = request;
        this.cancelable = cancelable;
    }

}
