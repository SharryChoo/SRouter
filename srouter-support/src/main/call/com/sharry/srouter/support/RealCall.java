package com.sharry.srouter.support;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.List;

import static com.sharry.srouter.support.ThreadMode.MAIN_THREAD;

/**
 * The Call implementor.
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 5/6/2019 1:56 PM
 */
public class RealCall implements ICall {

    /**
     * The factory method help U createApi instance.
     */
    public static RealCall create(@NonNull Context context,
                                  @NonNull Request request,
                                  @NonNull List<IInterceptor> interceptors) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(request);
        Preconditions.checkNotNull(interceptors);
        return new RealCall(context, request, interceptors);
    }

    private final Context context;
    private final Request request;
    private final List<IInterceptor> interceptors;

    private RealCall(Context context, Request request, List<IInterceptor> interceptors) {
        this.context = context;
        this.request = request;
        this.interceptors = interceptors;
    }

    @Override
    public ICancelable post(@NonNull final DispatchCallback callback) {
        Preconditions.checkNotNull(callback);
        IScheduler scheduler = SchedulerFactory.create(MAIN_THREAD);
        final ICancelable cancelable = new RealCancelable();
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                ChainContext chainContext = ChainContext.obtain(context, request, cancelable);
                IInterceptor.Chain chain = RealChain.create(interceptors, chainContext, callback);
                chain.dispatch();
            }
        }, request.getDelay());
        return cancelable;
    }

    @Override
    @NonNull
    public <T> T adaptTo(@NonNull Class<T> returnClass) {
        // Find adapter.
        for (ICallAdapter callAdapter : DataSource.CALL_ADAPTERS) {
            if (returnClass.getName().equals(callAdapter.adaptType().getName())) {
                return (T) callAdapter.adapt(this);
            }
        }
        throw new NoAdapterFoundException("Cannot find CallAdapter return type as: "
                + returnClass.getSimpleName());
    }

}