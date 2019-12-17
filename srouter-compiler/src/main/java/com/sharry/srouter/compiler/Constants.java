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
     * Class name of Android type.
     */
    static final String CLASS_NAME_ACTIVITY = "android.app.Activity";
    static final String CLASS_NAME_FRAGMENT = "android.app.Fragment";
    static final String CLASS_NAME_PARCELABLE = "android.os.Parcelable";
    static final String CLASS_NAME_BUNDLE = "android.os.Bundle";
    static final String CLASS_NAME_FRAGMENT_V4 = "android.support.v4.app.Fragment";
    static final String CLASS_NAME_FRAGMENT_X = "androidx.fragment.app.Fragment";

    /**
     * Class name of Java type
     */
    private static final String PACKAGE_NAME_LANG = "java.lang";
    static final String CLASS_NAME_BYTE = PACKAGE_NAME_LANG + ".Byte";
    static final String CLASS_NAME_SHORT = PACKAGE_NAME_LANG + ".Short";
    static final String CLASS_NAME_INTEGER = PACKAGE_NAME_LANG + ".Integer";
    static final String CLASS_NAME_LONG = PACKAGE_NAME_LANG + ".Long";
    static final String CLASS_NAME_FLOAT = PACKAGE_NAME_LANG + ".Float";
    static final String CLASS_NAME_DOUBLE = PACKAGE_NAME_LANG + ".Double";
    static final String CLASS_NAME_BOOLEAN = PACKAGE_NAME_LANG + ".Boolean";
    static final String CLASS_NAME_CHAR = PACKAGE_NAME_LANG + ".Character";
    static final String CLASS_NAME_STRING = PACKAGE_NAME_LANG + ".String";
    static final String CLASS_NAME_SERIALIZABLE = "java.io.Serializable";

    /**
     * Package name of srouter-compeiler generate.
     */
    static final String PACKAGE_NAME_OF_GENERATE = "com.sharry.srouter.generate";

    /**
     * Package name of srouter-support.
     */
    static final String PACKAGE_NAME_SROUTER = "com.sharry.srouter.support";

    /**
     * Class name of srouter-support.
     */
    static final String CLASS_NAME_OF_SROUTER_ISERVICE = "com.sharry.srouter.support.IService";
    static final String CLASS_NAME_OF_SROUTER_IINTERCEPTOR = "com.sharry.srouter.support.IInterceptor";
    static final String CLASS_NAME_OF_SROUTER_CONSTANTS = "com.sharry.srouter.support.Constants";

    private static final String SIMPLE_NAME_SUB_PROJECT = "SRouter";
    private static final String SIMPLE_NAME_SUB_SEPARATOR = "$$";

    ///////////////////////////////////////////// com.sharry.srouter.generate.SRouter$$Routes$$XXXX //////////////////////////////////////////////////
    static final String SIMPLE_NAME_PREFIX_OF_ROUTERS = SIMPLE_NAME_SUB_PROJECT +
            SIMPLE_NAME_SUB_SEPARATOR + "Routes" + SIMPLE_NAME_SUB_SEPARATOR;
    static final String SIMPLE_NAME_IROUTE = "IRoute";
    static final String SIMPLE_NAME_ROUTE_META = "RouteMeta";
    static final String METHOD_LOAD_INTO = "loadInto";
    static final String METHOD_LOAD_INTO_PARAMETER_NAME_ROUTE_CACHES = "caches";

    ///////////////////////////////////////////// com.sharry.srouter.generate.SRouter$$Routes$$XXXX //////////////////////////////////////////////////
    static final String SIMPLE_NAME_PREFIX_OF_INTERCEPTOR = SIMPLE_NAME_SUB_PROJECT +
            SIMPLE_NAME_SUB_SEPARATOR + "Interceptors" + SIMPLE_NAME_SUB_SEPARATOR;
    static final String SIMPLE_NAME_IROUTE_INTERCEPTOR = "IRouteInterceptor";
    static final String METHOD_LOAD_INTO_PARAMETER_NAME_INTERCEPTION_CACHES = "caches";
    static final String SIMPLE_NAME_INTERCEPTOR_META = "InterceptorMeta";

    ///////////////////////////////////////////// com.sharry.srouter.generate.XXX$$QueryBinding //////////////////////////////////////////////////
    static final String SIMPLE_NAME_SUFFIX_OF_QUERY_BINDING = SIMPLE_NAME_SUB_SEPARATOR + "QueryBinding";
    static final String SIMPLE_NAME_IQUERY_BINDING = "IQueryBinding";
    static final String METHOD_BIND = "bind";
    static final String METHOD_BIND_PARAMETER_NAME_TARGET = "target";
    static final String METHOD_BIND_PARAMETER_NAME_DATA = "data";

}