package com.sharry.sroutersupport.data;

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

    public static final Map<String, RouteMeta> ROUTES = new HashMap<>();

    public static final Map<String, RouteInterceptorMeta> INTERCEPTORS = new HashMap<>();

    public static void clear() {
        ROUTES.clear();
        INTERCEPTORS.clear();
    }

}
