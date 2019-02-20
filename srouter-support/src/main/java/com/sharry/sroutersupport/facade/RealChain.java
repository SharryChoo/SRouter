package com.sharry.sroutersupport.facade;

import android.content.Context;

import com.sharry.sroutersupport.data.Request;
import com.sharry.sroutersupport.data.Result;
import com.sharry.sroutersupport.template.IInterceptor;

import java.util.List;

/**
 * The chain for a navigation request.
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 2/20/2019 10:05 AM
 */
class RealChain implements IInterceptor.Chain {

    static RealChain create(List<IInterceptor> handles, Context context, Request request, int handleIndex) {
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
    public Result dispatch() {
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