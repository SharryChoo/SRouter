package com.sharry.srouter.support.data;

import android.content.Context;

import com.sharry.srouter.support.call.ICall;
import com.sharry.srouter.support.call.ICallAdapter;
import com.sharry.srouter.support.exceptions.NoRouteFoundException;
import com.sharry.srouter.support.facade.SRouter;
import com.sharry.srouter.support.templates.IRoute;
import com.sharry.srouter.support.templates.IRouteInterceptor;
import com.sharry.srouter.support.utils.Logger;
import com.sharry.srouter.support.utils.RouterApiUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import static com.sharry.srouter.support.utils.Constants.DOT;
import static com.sharry.srouter.support.utils.Constants.NAME_OF_INTERCEPTOR;
import static com.sharry.srouter.support.utils.Constants.NAME_OF_ROUTERS;
import static com.sharry.srouter.support.utils.Constants.PACKAGE_OF_GENERATE_FILE;
import static com.sharry.srouter.support.utils.Constants.SEPARATOR;
import static com.sharry.srouter.support.utils.Constants.SUFFIX_OF_QUERY_BINDING;

/**
 * Perform Route logistics, it contains all of the map.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public class LogisticsCenter {

    /**
     * Register components.
     */
    public static void registerModules(String[] names) {
        for (String moduleName : names) {
            // 加载根元素(com.sharry.srouter.generate.SRouter$$Routes$$XXX)
            String routesClassName = PACKAGE_OF_GENERATE_FILE + DOT + NAME_OF_ROUTERS + SEPARATOR + moduleName;
            try {
                IRoute route = (IRoute) (Class.forName(routesClassName).getConstructor().newInstance());
                route.loadInto(Warehouse.TABLE_ROUTES);
            } catch (Exception e) {
                Logger.e(e.getMessage(), e);
            }
            // 加载拦截器标签元素(com.sharry.srouter.generate.SRouter$$Interceptors$$XXX)
            String interceptorsClassName = PACKAGE_OF_GENERATE_FILE + DOT + NAME_OF_INTERCEPTOR + SEPARATOR + moduleName;
            try {
                IRouteInterceptor routeInterceptor = (IRouteInterceptor) (
                        Class.forName(interceptorsClassName).getConstructor().newInstance());
                routeInterceptor.loadInto(Warehouse.TABLE_ROUTES_INTERCEPTORS);
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        }
    }

    /**
     * Unregister components.
     */
    public static void unregisterModules(String[] names) {
        for (String moduleName : names) {
            Map<String, RouteMeta> metas = Warehouse.TABLE_ROUTES.remove(moduleName);
            if (metas == null) {
                Logger.i("Cannot find this module: " + moduleName);
            }
        }
    }

    /**
     * Parse intent and inject to fields that @Query marked.
     */
    public static <T> void bindQuery(T binder) {
        Class binderClass = binder.getClass();
        String queryBindingClassName = binderClass.getName() + SEPARATOR + SUFFIX_OF_QUERY_BINDING;
        try {
            Constructor constructor = Warehouse.QUERY_BINDING_CONSTRUCTORS.get(queryBindingClassName);
            if (constructor == null) {
                Class queryBindingClass = Class.forName(queryBindingClassName);
                constructor = queryBindingClass.getConstructor(binderClass);
                constructor.setAccessible(true);
                Warehouse.QUERY_BINDING_CONSTRUCTORS.put(queryBindingClassName, constructor);
            }
            constructor.newInstance(binder);
        } catch (Throwable e) {
            Logger.e(e.getMessage() == null ? "" : e.getMessage(), e);
        }
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

    /**
     * Fetch data from warehouse and then inject to request.
     */
    public static void completion(Request request) throws NoRouteFoundException {
        // Fetch authority.
        String authority = request.getAuthority();
        Map<String, RouteMeta> routeMetas = Warehouse.TABLE_ROUTES.get(authority);
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
        request.setType(routeMeta.getType());
        request.setRouteClass(routeMeta.getRouteClass());
        request.setRouteInterceptorURIs(routeMeta.getRouteInterceptorURIs());
        // If Type is SERVICE, the request cannot be intercepted.
        request.setGreenChannel(routeMeta.getType() == RouteMeta.Type.SERVICE);
    }

    public static void addCallAdapter(ICallAdapter adapter) {
        Warehouse.CALL_ADAPTERS.add(adapter);
    }
}
