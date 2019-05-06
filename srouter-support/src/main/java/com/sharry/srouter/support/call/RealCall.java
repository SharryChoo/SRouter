package com.sharry.srouter.support.call;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sharry.srouter.support.data.Request;
import com.sharry.srouter.support.data.Response;
import com.sharry.srouter.support.data.Warehouse;
import com.sharry.srouter.support.exceptions.NoAdapterFoundException;
import com.sharry.srouter.support.facade.Callback;
import com.sharry.srouter.support.interceptors.ChainContext;
import com.sharry.srouter.support.interceptors.IInterceptor;
import com.sharry.srouter.support.interceptors.RealChain;
import com.sharry.srouter.support.scheduler.IScheduler;
import com.sharry.srouter.support.scheduler.SchedulerFactory;
import com.sharry.srouter.support.utils.Logger;
import com.sharry.srouter.support.utils.Preconditions;

import java.util.List;

/**
 * The Call implementor.
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 5/6/2019 1:56 PM
 */
public class RealCall implements ICall {

    /**
     * The factory method help U create instance.
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
    public void call() {
        this.call(null);
    }

    @Override
    public void call(@Nullable final Callback callback) {
        IScheduler scheduler = SchedulerFactory.create(request.getThreadMode());
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                ChainContext chainContext = ChainContext.obtain(
                        context,
                        request,
                        new IInterceptor.Chain.Callback() {
                            @Override
                            public void onDispatched(@NonNull Response response) {
                                if (callback == null) {
                                    Logger.i("Callback is null, callback canceled.");
                                    return;
                                }
                                callback.onSuccess(response);
                            }
                        }
                );
                RealChain.create(interceptors, chainContext).dispatch();
            }
        }, request.getDelay());
    }

    @Override
    public <T> T adaptTo(@NonNull Class<T> returnClass) {
        for (ICallAdapter callAdapter : Warehouse.CALL_ADAPTERS) {
            if (returnClass.getName().equals(callAdapter.adaptType().getName())) {
                return (T) callAdapter.adapt(this);
            }
        }
        throw new NoAdapterFoundException("Cannot find CallAdapter return type as: "
                + returnClass.getSimpleName());
    }

}
