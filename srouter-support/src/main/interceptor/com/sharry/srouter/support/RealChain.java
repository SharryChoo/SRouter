package com.sharry.srouter.support;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * The Chain implementor.
 *
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-05-05
 */
class RealChain implements IInterceptor.Chain {

    /**
     * Gat an instance of RealChain.
     */
    static RealChain create(@NonNull List<IInterceptor> handles, @NonNull ChainContext chainContext,
                            @NonNull DispatchCallback chainCallback) {
        Preconditions.checkNotEmpty(handles);
        Preconditions.checkNotNull(chainContext);
        Preconditions.checkNotNull(chainCallback);
        return new RealChain(handles, chainContext, chainCallback);
    }

    private final List<IInterceptor> handles;
    private final ChainContext chainContext;
    private final DispatchCallback dispatchCallback;
    private int index = 0;

    private RealChain(List<IInterceptor> handles, ChainContext context,
                      DispatchCallback dispatchCallback) {
        this.handles = handles;
        this.chainContext = context;
        this.dispatchCallback = dispatchCallback;
    }

    @Override
    public ChainContext context() {
        return chainContext;
    }

    @Override
    public DispatchCallback callback() {
        return dispatchCallback;
    }

    @Override
    public void dispatch() {
        if (chainContext.cancelable.isCanceled()) {
            dispatchCallback.onCanceled();
            return;
        }
        handles.get(index++).intercept(this);
    }

}
