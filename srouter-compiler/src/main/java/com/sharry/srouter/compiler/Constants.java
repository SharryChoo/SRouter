package com.sharry.srouter.compiler;

/**
 * Some constants used in processors
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/18 21:42
 */
class Constants {

    /**
     * Base element will be used verify component name.
     */
    static final String KEY_MODULE_NAME = "moduleName";

    /**
     * Class name will be used route verify work.
     */
    static final String CLASS_NAME_ACTIVITY = "android.app.Activity";
    static final String CLASS_NAME_FRAGMENT = "android.app.Fragment";
    static final String CLASS_NAME_PARCELABLE = "android.os.Parcelable";
    static final String CLASS_NAME_BUNDLE = "android.os.Bundle";
    static final String CLASS_NAME_FRAGMENT_V4 = "android.support.v4.app.Fragment";
    static final String CLASS_NAME_FRAGMENT_X = "androidx.fragment.app.Fragment";
    static final String CLASS_NAME_ISERVICE = "com.sharry.srouter.support.service.IService";
    static final String CLASS_NAME_IINTERCEPTOR = "com.sharry.srouter.support.interceptors.IInterceptor";
    static final String PACKAGE_NAME_OF_SROUTER_CONSTANTS = "com.sharry.srouter.support.utils.Constants";

    /**
     * Java type
     */
    private static final String LANG = "java.lang";
    static final String BYTE = LANG + ".Byte";
    static final String SHORT = LANG + ".Short";
    static final String INTEGER = LANG + ".Integer";
    static final String LONG = LANG + ".Long";
    static final String FLOAT = LANG + ".Float";
    static final String DOUBLE = LANG + ".Double";
    static final String BOOLEAN = LANG + ".Boolean";
    static final String CHAR = LANG + ".Character";
    static final String STRING = LANG + ".String";
    static final String SERIALIZABLE = "java.io.Serializable";

    /**
     * Sub element for simple class name.
     */
    static final String PACKAGE_NAME_OF_GENERATE_FILE = "com.sharry.srouter.generate";

    /**
     * Description parents for generation classes.
     */
    static final String PACKAGE_NAME_TEMPLATE = "com.sharry.srouter.support.templates";
    static final String SIMPLE_NAME_IROUTE = "IRoute";
    static final String SIMPLE_NAME_IROUTE_INTERCEPTOR = "IRouteInterceptor";
    static final String SIMPLE_NAME_IQUERY_BINDING = "IQueryBinding";

    /**
     * IRoute override method name.
     */
    static final String METHOD_LOAD_INTO = "loadInto";
    static final String METHOD_LOAD_INTO_PARAMETER_NAME_ROUTE_CACHES = "caches";
    static final String METHOD_LOAD_INTO_PARAMETER_NAME_INTERCEPTION_CACHES = "caches";
    static final String METHOD_BIND = "bind";
    static final String METHOD_BIND_PARAMETER_NAME_TARGET = "target";


    /**
     * Write data class name
     */
    static final String PACKAGE_NAME_DATA = "com.sharry.srouter.support.data";
    static final String SIMPLE_NAME_ROUTE_META = "RouteMeta";
    static final String SIMPLE_NAME_INTERCEPTOR_META = "InterceptorMeta";
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
     * Simple class name suffix: XXX$$QueryBinding
     */
    static final String SIMPLE_NAME_SUFFIX_OF_QUERY_BINDING = SIMPLE_NAME_SUB_SEPARATOR + "QueryBinding";

}