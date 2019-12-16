package com.sharry.srouter.support;

import android.content.Context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import static com.sharry.srouter.support.Constants.DOT;
import static com.sharry.srouter.support.Constants.NAME_OF_INTERCEPTOR;
import static com.sharry.srouter.support.Constants.NAME_OF_ROUTERS;
import static com.sharry.srouter.support.Constants.PACKAGE_OF_GENERATE_FILE;
import static com.sharry.srouter.support.Constants.SEPARATOR;
import static com.sharry.srouter.support.Constants.SUFFIX_OF_QUERY_BINDING;

/**
 * Perform Route logistics, it contains all of the map.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
class LogisticsCenter {

    /**
     * Register components.
     */
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

    /**
     * Unregister components.
     */
    static void unregisterModules(String[] names) {
        for (String moduleName : names) {
            Map<String, RouteMeta> metas = DataSource.TABLE_ROUTES.remove(moduleName);
            if (metas == null) {
                Logger.i("Cannot find this module: " + moduleName);
            }
        }
    }

    /**
     * Parse intent and inject to fields that @Query marked.
     */
    static <T> void bindQuery(T binder) {
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
            // 3. fetch method from cache
            Method bindMethod = DataSource.QUERY_BINDING_METHOD_BINDS.get(queryBindingClassName);
            if (bindMethod == null) {
                bindMethod = queryBindingClass.getMethod(Constants.METHOD_NAME_OF_BIND, binderClass);
                DataSource.QUERY_BINDING_METHOD_BINDS.put(queryBindingClassName, bindMethod);
            }
            // 4. invoke bind method.
            bindMethod.invoke(queryBinding, binder);
        } catch (Throwable e) {
            Logger.e(e.getMessage() == null ? "" : e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T createApi(Class<T> templateClass) {
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
                                    call = SRouter.newNavigationCall((Context) arg, request);
                                    break;
                                }
                            }
                        }
                        if (call == null) {
                            call = SRouter.newNavigationCall(null, request);
                        }
                        // adapt 2 target type.
                        return call.adaptTo(method.getReturnType());
                    }
                }
        );
    }

    /**
     * Fetch data from warehouse and then inject to request.
     */
    static void completion(Request request) throws NoRouteFoundException {
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

    public static void addCallAdapter(ICallAdapter adapter) {
        DataSource.CALL_ADAPTERS.add(adapter);
    }
}
