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
    public static RealChain create(@NonNull List<IInterceptor> handles, ChainContext chainContext,
                                   IInterceptor.ChainCallback chainCallback) {
        Preconditions.checkNotEmpty(handles);
        return new RealChain(handles, 0, chainContext, chainCallback);
    }

    private static RealChain create(List<IInterceptor> handles, int handleIndex,
                                    ChainContext chainContext, IInterceptor.ChainCallback chainCallback) {
        return new RealChain(handles, handleIndex, chainContext, chainCallback);
    }

    private final List<IInterceptor> handles;
    private final int index;
    private final ChainContext chainContext;
    private final IInterceptor.ChainCallback chainCallback;

    private RealChain(List<IInterceptor> handles, int handleIndex, ChainContext context,
                      IInterceptor.ChainCallback chainCallback) {
        this.handles = handles;
        this.index = handleIndex;
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
        handles.get(index).intercept(
                RealChain.create(
                        handles,
                        index + 1,
                        chainContext,
                        chainCallback
                )
        );
    }

}
