package com.sharry.srouter.support.call;

import android.content.Context;

import androidx.annotation.NonNull;

import com.sharry.srouter.support.data.Request;
import com.sharry.srouter.support.data.Warehouse;
import com.sharry.srouter.support.exceptions.NoAdapterFoundException;
import com.sharry.srouter.support.interceptors.ChainContext;
import com.sharry.srouter.support.interceptors.IInterceptor;
import com.sharry.srouter.support.interceptors.RealChain;
import com.sharry.srouter.support.scheduler.IScheduler;
import com.sharry.srouter.support.scheduler.SchedulerFactory;
import com.sharry.srouter.support.utils.Preconditions;

import java.util.List;

import static com.sharry.srouter.support.scheduler.ThreadMode.MAIN_THREAD;

/**
 * The Call implementor.
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 5/6/2019 1:56 PM
 */
public class RealCall implements ICall {

    private final Context context;
    private final Request request;
    private final List<IInterceptor> interceptors;

    private RealCall(Context context, Request request, List<IInterceptor> interceptors) {
        this.context = context;
        this.request = request;
        this.interceptors = interceptors;
    }

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

    @Override
    public void post(@NonNull final IInterceptor.ChainCallback callback) {
        Preconditions.checkNotNull(callback);
        IScheduler scheduler = SchedulerFactory.create(MAIN_THREAD);
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                ChainContext chainContext = ChainContext.obtain(context, request);
                IInterceptor.Chain chain = RealChain.create(interceptors, chainContext, callback);
                chain.dispatch();
            }
        }, request.getDelay());
    }

    @Override
    @NonNull
    public <T> T adaptTo(@NonNull Class<T> returnClass) {
        // Find adapter.
        for (ICallAdapter callAdapter : Warehouse.CALL_ADAPTERS) {
            if (returnClass.getName().equals(callAdapter.adaptType().getName())) {
                return (T) callAdapter.adapt(this);
            }
        }
        throw new NoAdapterFoundException("Cannot find CallAdapter return type as: "
                + returnClass.getSimpleName());
    }

}
