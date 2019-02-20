package com.sharry.sroutersupport.data;

/**
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public class RouteInterceptorMeta {

    /**
     * Get an instance of RouteInterceptorMeta
     */
    public static RouteInterceptorMeta create(Class<?> routeCls, int priority) {
        RouteInterceptorMeta result = new RouteInterceptorMeta();
        result.interceptorClass = routeCls;
        result.priority = priority;
        return result;
    }

    /**
     * navigation route target class.
     */
    private Class<?> interceptorClass;

    /**
     * navigation route thread mode.
     */
    private int priority;

    private RouteInterceptorMeta() {
    }

    public Class<?> getInterceptorClass() {
        return interceptorClass;
    }

    public int getPriority() {
        return priority;
    }

}
