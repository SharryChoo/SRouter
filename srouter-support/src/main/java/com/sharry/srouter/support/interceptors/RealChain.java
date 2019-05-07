package com.sharry.srouter.support.interceptors;

import androidx.annotation.NonNull;

import com.sharry.srouter.support.data.Response;
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
    public static RealChain create(@NonNull List<IInterceptor> handles, ChainContext chainContext) {
        Preconditions.checkNotEmpty(handles);
        return new RealChain(handles, 0, chainContext);
    }

    private static RealChain create(List<IInterceptor> handles, int handleIndex, ChainContext chainContext) {
        return new RealChain(handles, handleIndex, chainContext);
    }

    private final List<IInterceptor> handles;
    private final int index;
    private final ChainContext chainContext;

    private RealChain(List<IInterceptor> handles, int handleIndex, ChainContext context) {
        this.handles = handles;
        this.index = handleIndex;
        this.chainContext = context;
    }

    @Override
    public ChainContext chainContext() {
        return chainContext;
    }

    @Override
    public Response dispatch() {
       return handles.get(index).intercept(
                RealChain.create(
                        handles,
                        index + 1,
                        chainContext
                )
        );
    }

}
