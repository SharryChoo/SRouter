package com.sharry.srouter.support;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.sharry.srouter.support.Constants.DOT;
import static com.sharry.srouter.support.Constants.NAME_OF_INTERCEPTOR;
import static com.sharry.srouter.support.Constants.NAME_OF_ROUTERS;
import static com.sharry.srouter.support.Constants.PACKAGE_OF_GENERATE_FILE;
import static com.sharry.srouter.support.Constants.SEPARATOR;
import static com.sharry.srouter.support.Constants.SUFFIX_OF_QUERY_BINDING;

/**
 * Route feature's implementor.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
class SRouterImpl {

    static Context sAppContext;

    // /////////////////////////////////// initialize /////////////////////////////////////////

    static synchronized boolean init(Application application) {
        sAppContext = application;
        Logger.i("Route initialize success.");
        return true;
    }

    static void isDebug(boolean debug) {
        Logger.isDebug(debug);
    }

    // /////////////////////////////////// Module config /////////////////////////////////////////

    static void registerModules(String[] names) {
        for (String moduleName : names) {
            // 加载根元素(com.sharry.srouter.generate.SRouter$$Routes$$XXX)
            String routesClassName = PACKAGE_OF_GENERATE_FILE + DOT + NAME_OF_ROUTERS + SEPARATOR + moduleName;
            try {
                IRoute route = (IRoute) (Class.forName(routesClassName).getConstructor().newInstance());
                route.loadInto(DataSource.TABLE_ROUTES);
            } catch (Exception e) {
                Logger.e(e.getMessage(), e);
            }
            // 加载拦截器标签元素(com.sharry.srouter.generate.SRouter$$Interceptors$$XXX)
            String interceptorsClassName = PACKAGE_OF_GENERATE_FILE + DOT + NAME_OF_INTERCEPTOR + SEPARATOR + moduleName;
            try {
                IRouteInterceptor routeInterceptor = (IRouteInterceptor) (
                        Class.forName(interceptorsClassName).getConstructor().newInstance());
                routeInterceptor.loadInto(DataSource.TABLE_ROUTES_INTERCEPTORS);
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        }
    }

    static void unregisterModules(String[] names) {
        for (String moduleName : names) {
            Map<String, RouteMeta> metas = DataSource.TABLE_ROUTES.remove(moduleName);
            if (metas == null) {
                Logger.i("Cannot find this module: " + moduleName);
            }
        }
    }

    // /////////////////////////////////// Interceptor /////////////////////////////////////////

    static void addGlobalInterceptor(IInterceptor interceptor) {
        DataSource.GLOBAL_INTERCEPTORS.add(interceptor);
    }

    static void addGlobalInterceptorUri(String interceptorUri) {
        DataSource.GLOBAL_INTERCEPTOR_URIS.add(interceptorUri);
    }

    // /////////////////////////////////// Adapter /////////////////////////////////////////

    static void addCallAdapter(ICallAdapter adapter) {
        DataSource.CALL_ADAPTERS.add(adapter);
    }

    // /////////////////////////////////// Query /////////////////////////////////////////

    static <T> void bindQuery(T binder, Bundle args) {
        Class binderClass = binder.getClass();
        String queryBindingClassName = binderClass.getName() + SEPARATOR + SUFFIX_OF_QUERY_BINDING;
        try {
            // 1. fetch constructor from cache.
            Class queryBindingClass = Class.forName(queryBindingClassName);
            Constructor constructor = DataSource.QUERY_BINDING_CONSTRUCTORS.get(queryBindingClassName);
            if (constructor == null) {
                constructor = queryBindingClass.getConstructor();
                DataSource.QUERY_BINDING_CONSTRUCTORS.put(queryBindingClassName, constructor);
            }
            // 2. instantiate queryBinding.
            IQueryBinding queryBinding = (IQueryBinding) constructor.newInstance();
            // 3. invoke bind method.
            queryBinding.bind(binder, args);
        } catch (Throwable e) {
            Logger.e(e.getMessage() == null ? "" : e.getMessage(), e);
        }
    }

    // /////////////////////////////////// Request build /////////////////////////////////////////

    static Request request(String authority, String path) {
        return Request.create(authority, path);
    }

    static Request request(String uri) {
        return Request.parseUri(uri);
    }

    static Request request(Intent forwardIntent) {
        return Request.parseForwardIntent(forwardIntent);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createApi(Class<T> templateClass) {
        if (!templateClass.isInterface()) {
            throw new UnsupportedOperationException("Please ensure the template class "
                    + templateClass.getSimpleName() + " is interface");
        }
        return (T) Proxy.newProxyInstance(
                templateClass.getClassLoader(),
                new Class[]{templateClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Request request = RouterApiUtil.parseMethod(method, args);
                        if (request == null) {
                            Logger.e("Cannot parse method to SRouter Request, method is: " + method.getName());
                            return null;
                        }
                        // Get an instance of ICall.
                        ICall call = null;
                        if (args != null) {
                            for (Object arg : args) {
                                if (arg instanceof Context) {
                                    call = SRouter.newCall((Context) arg, request);
                                    break;
                                }
                            }
                        }
                        if (call == null) {
                            call = SRouter.newCall(null, request);
                        }
                        // adapt 2 target type.
                        return call.adaptTo(method.getReturnType());
                    }
                }
        );
    }


    // /////////////////////////////////// ForwardIntentBuilder /////////////////////////////////////////

    static ForwardIntentBuilder forwardIntentBuilder() {
        return new ForwardIntentBuilder();
    }

    // /////////////////////////////////// Start Navigation /////////////////////////////////////////

    static void navigation(final Context context, final Request request, final LambdaCallback successCallback) {
        newCall(context, request).post(new DispatchCallback() {

            @Override
            public void onSuccess(@NonNull Response response) {
                if (successCallback != null) {
                    successCallback.onDispatched(response);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                Logger.e(throwable.getMessage(), throwable);
            }

            @Override
            public void onCanceled() {
                Logger.e("Dispatch canceled.");
            }
        });
    }

    @NonNull
    static ICall newCall(final Context context, final Request request) {
        // 1. completion request.
        try {
            completion(request);
        } catch (NoRouteFoundException e) {
            Logger.e(e.getMessage(), e);
            return ICall.FAILED;
        }
        // 2. completion interceptors.
        final List<IInterceptor> interceptors = new ArrayList<>();
        // 2.1 add user interceptors
        if (request.isGreenChannel()) {
            Logger.i("Request is green channel, ignore interceptors that not global.");
        } else {
            interceptors.addAll(request.getInterceptors());
            instantiateAndSortInterceptorUris(
                    Arrays.asList(request.getRouteMeta().getRouteInterceptorURIs()),
                    interceptors
            );
        }
        // 2.2 add global interceptors.
        instantiateAndSortInterceptorUris(DataSource.GLOBAL_INTERCEPTOR_URIS, interceptors);
        interceptors.addAll(DataSource.GLOBAL_INTERCEPTORS);
        // 2.3 Add finalize navigation Interceptor.
        interceptors.add(new NavigationInterceptor());
        // 3. Create call.
        return RealCall.create(null == context ? sAppContext : context, request, interceptors);
    }

    private static void instantiateAndSortInterceptorUris(List<String> sourceSet, List<IInterceptor> destSet) {
        // 1. Sort interceptor URIs by priority.
        final List<InterceptorMeta> orderedMetas = new LinkedList<>();
        for (String value : sourceSet) {
            InterceptorMeta meta = DataSource.TABLE_ROUTES_INTERCEPTORS.get(value);
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
        // 2. Put sorted metas to interceptors.
        for (InterceptorMeta meta : orderedMetas) {
            try {
                IInterceptor interceptor = (IInterceptor) meta.getInterceptorClass().newInstance();
                destSet.add(interceptor);
            } catch (IllegalAccessException e) {
                Logger.e(meta.getInterceptorClass().getName() + " cannot access, please ensure this class " +
                        "have public and no args Constructor", e);
            } catch (InstantiationException e) {
                Logger.e(meta.getInterceptorClass().getName() + " instantiation failed.", e);
            }
        }
    }

    /**
     * Fetch data from warehouse and then inject to request.
     */
    private static void completion(Request request) throws NoRouteFoundException {
        // Fetch authority.
        String authority = request.getAuthority();
        Map<String, RouteMeta> routeMetas = DataSource.TABLE_ROUTES.get(authority);
        if (null == routeMetas) {
            throw new NoRouteFoundException("SRouter cannot found authority: " + authority);
        }
        // Fetch path.
        String path = request.getPath();
        RouteMeta routeMeta = routeMetas.get(path);
        if (null == routeMeta) {
            throw new NoRouteFoundException("SRouter cannot found path: " + path);
        }
        // Load data to request before navigation
        request.setRouteMeta(routeMeta);
        // If Type is SERVICE, the request cannot be intercepted.
        request.setGreenChannel(routeMeta.getType() == RouteMeta.Type.SERVICE);
    }

}
