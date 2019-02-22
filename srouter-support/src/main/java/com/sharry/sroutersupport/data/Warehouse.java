package com.sharry.sroutersupport.data;

import com.sharry.srouterannotation.Route;
import com.sharry.srouterannotation.RouteInterceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Route data repository.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public class Warehouse {

    /**
     * Key is path of the route request.{@link Route#path()}
     * <p>
     * The key mapper special value is data associated with @Route marked class.
     */
    public static final Map<String, RouteMeta> TABLE_ROUTES = new HashMap<>();

    /**
     * Key is path of the route interceptor.{@link RouteInterceptor#path()}
     * <p>
     * The key mapper special value is data associated with @RouteInterceptor marked class.
     */
    public static final Map<String, RouteInterceptorMeta> TABLE_ROUTES_INTERCEPTORS = new HashMap<>();

}
