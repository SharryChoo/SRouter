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
    public static RealChain create(@NonNull List<IInterceptor> handles, @NonNull ChainContext chainContext) {
        Preconditions.checkNotEmpty(handles);
        Preconditions.checkNotNull(chainContext);
        return new RealChain(handles, chainContext, 0);
    }

    private static RealChain create(List<IInterceptor> handles, ChainContext chainContext, int handleIndex) {
        return new RealChain(handles, chainContext, handleIndex);
    }

    private final List<IInterceptor> handles;
    private final ChainContext chainContext;
    private final int index;

    private RealChain(List<IInterceptor> handles, ChainContext context, int handleIndex) {
        this.handles = handles;
        this.chainContext = context;
        this.index = handleIndex;
    }

    @Override
    public ChainContext chainContext() {
        return chainContext;
    }

    @Override
    public void dispatch() {
        handles.get(index).process(
                RealChain.create(
                        handles,
                        chainContext,
                        index + 1
                )
        );
    }

}
