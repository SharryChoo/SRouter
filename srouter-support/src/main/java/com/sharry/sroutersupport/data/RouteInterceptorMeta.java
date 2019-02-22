package com.sharry.sroutersupport.data;

import com.sharry.srouterannotation.RouteInterceptor;

/**
 * Save data associated with {@link RouteInterceptor} marked class.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public class RouteInterceptorMeta {

    /**
     * Get an instance of RouteInterceptorMeta
     */
    public static RouteInterceptorMeta create(Class<?> interceptorClass, int priority) {
        RouteInterceptorMeta result = new RouteInterceptorMeta();
        result.interceptorClass = interceptorClass;
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
