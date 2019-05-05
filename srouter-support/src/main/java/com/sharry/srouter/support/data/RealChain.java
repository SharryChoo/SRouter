package com.sharry.srouter.support.data;

import android.content.Context;

import com.sharry.srouter.support.interceptors.IInterceptor;

import java.util.List;

/**
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-05-05
 */
public class RealChain implements IInterceptor.Chain {

    public static RealChain create(List<IInterceptor> handles, Context context, Request request) {
        return new RealChain(handles, context, request, 0);
    }

    private static RealChain create(List<IInterceptor> handles, Context context, Request request, int handleIndex) {
        return new RealChain(handles, context, request, handleIndex);
    }

    private final List<IInterceptor> handles;
    private final Context context;
    private final Request request;
    private final int index;

    private RealChain(List<IInterceptor> handles, Context context, Request request, int handleIndex) {
        this.handles = handles;
        this.context = context;
        this.request = request;
        this.index = handleIndex;
    }

    @Override
    public Request request() {
        return request;
    }

    @Override
    public Context context() {
        return context;
    }

    @Override
    public Response dispatch() {
        return handles.get(index).process(
                RealChain.create(
                        handles,
                        context,
                        request,
                        index + 1
                )
        );
    }

}
