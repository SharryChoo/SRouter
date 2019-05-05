package com.sharry.srouter.support.data;

import com.sharry.srouter.support.exceptions.NoRouteFoundException;
import com.sharry.srouter.support.templates.IRoute;
import com.sharry.srouter.support.templates.IRouteInterceptor;
import com.sharry.srouter.support.utils.Logger;

import java.util.Map;

import static com.sharry.srouter.support.utils.Constants.DOT;
import static com.sharry.srouter.support.utils.Constants.NAME_OF_INTERCEPTOR;
import static com.sharry.srouter.support.utils.Constants.NAME_OF_ROUTERS;
import static com.sharry.srouter.support.utils.Constants.PACKAGE_OF_GENERATE_FILE;
import static com.sharry.srouter.support.utils.Constants.SEPARATOR;

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
    public static void registerComponents(String[] names) {
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
    public static void unregisterComponents(String[] names) {
        for (String moduleName : names) {
            Map<String, RouteMeta> metas = Warehouse.TABLE_ROUTES.remove(moduleName);
            if (metas == null) {
                Logger.i("Cannot find this module: " + moduleName);
            }
        }
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
        request.setThreadMode(routeMeta.getThreadMode());
        request.setRouteInterceptorURIs(routeMeta.getRouteInterceptorURIs());
        // If Type is PROVIDER, the request cannot be intercepted.
        request.setGreenChannel(routeMeta.getType() == RouteMeta.Type.PROVIDER);
    }
}
