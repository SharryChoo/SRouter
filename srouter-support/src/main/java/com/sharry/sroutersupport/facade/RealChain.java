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

    static RealChain build(List<IInterceptor> handles, Context context, Request request) {
        return new RealChain(handles, context, request);
    }

    private final List<IInterceptor> handles;
    private final Context context;
    private final Request request;
    private int index = 0;

    private RealChain(List<IInterceptor> handles, Context context, Request request) {
        this.handles = handles;
        this.context = context;
        this.request = request;
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
    public IInterceptor next() {
        return handles.get(index);
    }

    Result dispatch() {
        return handles.get(index++).process(this);
    }

}