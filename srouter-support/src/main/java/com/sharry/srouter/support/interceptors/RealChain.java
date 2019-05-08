package com.sharry.srouter.support.interceptors;

import androidx.annotation.NonNull;

import com.sharry.srouter.support.utils.Preconditions;

import java.util.List;

/**
 * The Chain implementor.
 *
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-05-05
 */
public class RealChain implements IInterceptor.Chain {

    /**
     * Gat an instance of RealChain.
     */
    public static RealChain create(@NonNull List<IInterceptor> handles, @NonNull ChainContext chainContext,
                                   @NonNull IInterceptor.ChainCallback chainCallback) {
        Preconditions.checkNotEmpty(handles);
        Preconditions.checkNotNull(chainContext);
        Preconditions.checkNotNull(chainCallback);
        return new RealChain(handles, chainContext, chainCallback);
    }

    private final List<IInterceptor> handles;
    private final ChainContext chainContext;
    private final IInterceptor.ChainCallback chainCallback;
    private int index = 0;

    private RealChain(List<IInterceptor> handles, ChainContext context,
                      IInterceptor.ChainCallback chainCallback) {
        this.handles = handles;
        this.chainContext = context;
        this.chainCallback = chainCallback;
    }

    @Override
    public ChainContext chainContext() {
        return chainContext;
    }

    @Override
    public IInterceptor.ChainCallback callback() {
        return chainCallback;
    }

    @Override
    public void dispatch() {
        if (chainContext.cancelable.isCanceled()) {
            chainCallback.onCanceled();
            return;
        }
        handles.get(index++).intercept(this);
    }

}
