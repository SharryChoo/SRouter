package com.sharry.sroutercompiler;

/**
 * Some constants used in processors
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/18 21:42
 */
class Constants {

    // Generate java file folder.
    static final String PACKAGE_OF_GENERATE_FILE = "com.sharry.android.srouter";

    // Base element
    static final String KEY_MODULE_NAME = "moduleName";

    /**
     * Class name will be used route verify work.
     */
    static final String CLASS_NAME_ACTIVITY = "android.app.Activity";
    static final String CLASS_NAME_FRAGMENT = "android.app.Fragment";
    static final String CLASS_NAME_FRAGMENT_V4 = "android.support.v4.app.Fragment";
    static final String CLASS_NAME_SERVICE = "android.app.Service";
    static final String CLASS_NAME_IPROVIDER = "com.sharry.sroutersupport.template.IProvider";
    static final String CLASS_NAME_IINTERCEPTOR = "com.sharry.sroutersupport.template.IInterceptor";

    /**
     * Sub element for simple class name.
     */
    private static final String SIMPLE_NAME_SUB_PROJECT = "SRouter";
    private static final String SIMPLE_NAME_SUB_SEPARATOR = "$$";

    /**
     * Simple class name prefix: SRouter$$Routes$$
     */
    static final String SIMPLE_NAME_PREFIX_OF_ROUTERS = SIMPLE_NAME_SUB_PROJECT +
            SIMPLE_NAME_SUB_SEPARATOR + "Routes" + SIMPLE_NAME_SUB_SEPARATOR;

    /**
     * Simple class name prefix: SRouter$$Interceptors$$
     */
    static final String SIMPLE_NAME_PREFIX_OF_INTERCEPTOR = SIMPLE_NAME_SUB_PROJECT +
            SIMPLE_NAME_SUB_SEPARATOR + "Interceptors" + SIMPLE_NAME_SUB_SEPARATOR;

    /**
     * Description parents for generation classes.
     */
    static final String TEMPLATE_PACKAGE_NAME = "com.sharry.sroutersupport.template";
    static final String SIMPLE_NAME_IROUTE = "IRoute";
    static final String SIMPLE_NAME_IROUTE_INTERCEPTOR = "IRouteInterceptor";

    /**
     * IRoute override method name.
     */
    static final String METHOD_LOAD_INTO = "loadInto";

    /**
     * Write data class name
     */
    static final String DATA_PACKAGE_NAME = "com.sharry.sroutersupport.data";
    static final String SIMPLE_NAME_ROUTE_META = "RouteMeta";
    static final String SIMPLE_NAME_ROUTE_INTERCEPTOR_META = "RouteInterceptorMeta";

}