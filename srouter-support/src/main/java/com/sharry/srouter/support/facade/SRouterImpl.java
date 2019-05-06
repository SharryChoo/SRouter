package com.sharry.srouter.support.facade;

import android.app.Application;
import android.content.Context;

import com.sharry.srouter.support.call.ICall;
import com.sharry.srouter.support.call.ICallAdapter;
import com.sharry.srouter.support.call.RealCall;
import com.sharry.srouter.support.data.InterceptorMeta;
import com.sharry.srouter.support.data.LogisticsCenter;
import com.sharry.srouter.support.data.Request;
import com.sharry.srouter.support.data.Warehouse;
import com.sharry.srouter.support.exceptions.NoRouteFoundException;
import com.sharry.srouter.support.interceptors.IInterceptor;
import com.sharry.srouter.support.interceptors.NavigationInterceptor;
import com.sharry.srouter.support.utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Route features implement.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
class SRouterImpl {

    private static Context sContext;

    static synchronized boolean init(Application application) {
        sContext = application;
        Logger.i("Route initialize success.");
        return true;
    }

    static void registerComponents(String[] names) {
        LogisticsCenter.registerComponents(names);
    }

    static void unregisterComponents(String[] names) {
        LogisticsCenter.unregisterComponents(names);
    }

    static void addCallAdapter(ICallAdapter adapter) {
        LogisticsCenter.addCallAdapter(adapter);
    }

    /**
     * Build navigation postcard by path.
     */
    static Request build(String authority, String path) {
        return Request.create(authority, path);
    }

    /**
     * Initiatory perform navigation.
     */
    static void navigation(final Context context, final Request request, Callback callback) {
        ICall newCall = newCall(context, request);
        newCall.call(callback);
    }

    /**
     * Build an instance of navigation call.
     */
    static ICall newCall(final Context context, final Request request) {
        // 1. load data to request.
        try {
            LogisticsCenter.completion(request);
        } catch (NoRouteFoundException e) {
            Logger.e(e.getMessage(), e);
            return ICall.DEFAULT;
        }
        // 2. Add user added interceptors before route interceptor.
        final List<IInterceptor> interceptors = new ArrayList<>(request.getInterceptors());
        // 3. Parse interceptor URIS.
        final List<String> interceptorURIs = new ArrayList<>(request.getInterceptorURIs());
        interceptorURIs.addAll(Arrays.asList(request.getRouteInterceptorURIs()));
        if (!request.isGreenChannel()) {
            // 3.1 Sort interceptor URIs by priority.
            final List<InterceptorMeta> orderedMetas = new LinkedList<>();
            for (String value : interceptorURIs) {
                InterceptorMeta meta = Warehouse.TABLE_ROUTES_INTERCEPTORS.get(value);
                if (null == meta) {
                    continue;
                }
                // Add meta data to comfortable position.
                int insertIndex = 0;
                for (InterceptorMeta orderedMeta : orderedMetas) {
                    if (orderedMeta.getPriority() < meta.getPriority()) {
                        break;
                    } else {
                        insertIndex++;
                    }
                }
                orderedMetas.add(insertIndex, meta);
            }
            // 3.2 Put sorted metas to interceptors.
            for (InterceptorMeta meta : orderedMetas) {
                try {
                    IInterceptor interceptor = (IInterceptor) meta.getInterceptorClass().newInstance();
                    interceptors.add(interceptor);
                } catch (IllegalAccessException e) {
                    Logger.e(meta.getInterceptorClass().getName() + " cannot access, please ensure this class " +
                            "have public and no args Constructor", e);
                } catch (InstantiationException e) {
                    Logger.e(meta.getInterceptorClass().getName() + " instantiation failed.", e);
                }
            }
        }
        // 4. Add finalize navigation Interceptor.
        interceptors.add(new NavigationInterceptor());
        return RealCall.create(null == context ? sContext : context, request, interceptors);
    }
}
