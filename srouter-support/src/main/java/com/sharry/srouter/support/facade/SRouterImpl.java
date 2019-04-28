package com.sharry.srouter.support.facade;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.sharry.srouter.support.data.LogisticsCenter;
import com.sharry.srouter.support.data.Request;
import com.sharry.srouter.support.data.Response;
import com.sharry.srouter.support.data.RouteInterceptorMeta;
import com.sharry.srouter.support.data.Warehouse;
import com.sharry.srouter.support.exceptions.NoRouteFoundException;
import com.sharry.srouter.support.interceptors.IInterceptor;
import com.sharry.srouter.support.interceptors.NavigationInterceptor;
import com.sharry.srouter.support.thread.RouterPoolExecutor;
import com.sharry.srouter.support.utils.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Route features implement.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
class SRouterImpl {

    private static Context sContext;
    private volatile static ThreadPoolExecutor sExecutor = RouterPoolExecutor.getInstance();

    /**
     * Get instance of router implementation. S
     *
     * @return SRouterImpl singleton
     */
    static SRouterImpl getInstance() {
        return InstanceHolder.INSTANCE;
    }

    static synchronized boolean init(Application application) {
        sContext = application;
        LogisticsCenter.init(application, sExecutor);
        Logger.i("Route initialize success.");
        return true;
    }

    /**
     * Build navigation postcard by path.
     */
    Request build(@NonNull String path) {
        return Request.create(path);
    }

    /**
     * Initiatory perform navigation.
     */
    Response navigation(final Context context, final Request request) {
        // 1. load data to request.
        try {
            LogisticsCenter.completion(request);
        } catch (NoRouteFoundException e) {
            Logger.e(e.getMessage(), e);
            return null;
        }
        final List<IInterceptor> interceptors = new ArrayList<>();
        // 2. Add user added interceptors before route interceptor.
        interceptors.addAll(request.getInterceptors());
        // 3. Add route INTERCEPTORS.
        if (!request.isGreenChannel()) {
            // 3.1 Sort route INTERCEPTORS by priority.
            final List<RouteInterceptorMeta> orderedMetas = new LinkedList<>();
            for (String interceptorPath : request.getRouteInterceptors()) {
                // Get meta data associated with the path.
                RouteInterceptorMeta meta = Warehouse.TABLE_ROUTES_INTERCEPTORS.get(interceptorPath);
                if (null == meta) {
                    continue;
                }
                // Add meta data to comfortable position.
                int insertIndex = 0;
                for (RouteInterceptorMeta orderedMeta : orderedMetas) {
                    if (orderedMeta.getPriority() < meta.getPriority()) {
                        break;
                    } else {
                        insertIndex++;
                    }
                }
                orderedMetas.add(insertIndex, meta);
            }
            // Put Instantiation interceptor to INTERCEPTORS.
            for (RouteInterceptorMeta meta : orderedMetas) {
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
        // 4. Add user added interceptors before route interceptor.
        interceptors.addAll(request.getNavigationInterceptors());
        // 5. Add finalize navigation Interceptor.
        interceptors.add(new NavigationInterceptor());
        return RealChain.create(interceptors, null == context ? sContext : context, request, 0).dispatch();
    }

    private static class InstanceHolder {

        private static final SRouterImpl INSTANCE = new SRouterImpl();

    }

    /**
     * Real chain for the interceptor.
     */
    private static class RealChain implements IInterceptor.Chain {

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

}
