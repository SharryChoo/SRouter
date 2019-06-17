package com.sharry.srouter.support.data;

import com.sharry.srouter.annotation.compiler.Route;
import com.sharry.srouter.annotation.compiler.RouteInterceptor;
import com.sharry.srouter.support.call.ICallAdapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * The key mapper special key is data associated with @Route marked class.
     */
    public static final Map<String, Map<String, RouteMeta>> TABLE_ROUTES = new HashMap<>();

    /**
     * Key is path of the route interceptor.{@link RouteInterceptor#value()} ()}
     * <p>
     * The key mapper special key is data associated with @RouteInterceptor marked class.
     */
    public static final Map<String, InterceptorMeta> TABLE_ROUTES_INTERCEPTORS = new HashMap<>();

    /**
     * Cache post adapters associated with SRouter.
     */
    public static final List<ICallAdapter> CALL_ADAPTERS = new ArrayList<>();

    /**
     * Key is name of Class<XXX$$QueryBinding>
     * <p>
     * The key mapper special value is constructor of the class.
     */
    public static final Map<String, Constructor> QUERY_BINDING_CONSTRUCTORS = new HashMap<>();

    /**
     * Key is name of Class<XXX$$QueryBinding>
     * <p>
     * The key mapper special value is  <method> bind </method> of the class.
     */
    public static final Map<String, Method> QUERY_BINDING_METHOD_BINDS = new HashMap<>();

    static {
        CALL_ADAPTERS.add(ICallAdapter.DEFAULT);
    }

}
