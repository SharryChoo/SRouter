package com.sharry.sroutersupport.facade;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.sharry.sroutersupport.data.LogisticsCenter;
import com.sharry.sroutersupport.data.Request;
import com.sharry.sroutersupport.data.RouteInterceptorMeta;
import com.sharry.sroutersupport.data.Warehouse;
import com.sharry.sroutersupport.exceptions.NoRouteFoundException;
import com.sharry.sroutersupport.frame.Logger;
import com.sharry.sroutersupport.interceptors.NavigationInterceptor;
import com.sharry.sroutersupport.template.IInterceptor;
import com.sharry.sroutersupport.thread.DefaultPoolExecutor;

import java.util.ArrayList;
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
    private volatile static ThreadPoolExecutor sExecutor = DefaultPoolExecutor.getInstance();

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
     * Build navigation postcard by authority.
     */
    public Request build(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new IllegalArgumentException("Parameter authority cannot be null!");
        }
        return new Request(path);
    }

    /**
     * Initiatory perform navigation.
     */
    public Object navigation(final Context context, final Request request) {
        // load data to request.
        try {
            LogisticsCenter.completion(request);
        } catch (NoRouteFoundException e) {
            Logger.e(e.getMessage(), e);
            return null;
        }
        final List<IInterceptor> interceptors = new ArrayList<>();
        // TODO: 添加前置 拦截器
        // Add navigation interceptors.
        if (!request.isGreenChannel()) {
            for (String interceptorAuthority : request.getInterceptors()) {
                IInterceptor iInterceptor = null;
                RouteInterceptorMeta meta = Warehouse.INTERCEPTORS.get(interceptorAuthority);
                if (meta != null) {
                    try {
                        iInterceptor = (IInterceptor) meta.getInterceptorClass().newInstance();
                    } catch (IllegalAccessException e) {
                        Logger.e(interceptorAuthority + " cannot access, please ensure this class " +
                                "have public and no args Constructor", e);
                    } catch (InstantiationException e) {
                        Logger.e(interceptorAuthority + " instantiation failed.", e);
                    }
                } else {
                    Logger.e("Cannot find interceptors, authority is: " + interceptorAuthority);
                    return null;
                }
                interceptors.add(iInterceptor);
            }
        }
        // Add finalize navigation Interceptor.
        interceptors.add(new NavigationInterceptor());
        return RealChain.create(interceptors, null == context ? sContext : context, request, 0).dispatch();
    }


    private static class InstanceHolder {

        private static final SRouterImpl INSTANCE = new SRouterImpl();

    }

}
