package com.sharry.srouter.support;

import com.sharry.srouter.annotation.compiler.RouteInterceptor;

/**
 * Save data associated with {@link RouteInterceptor} marked class.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public class InterceptorMeta {

    /**
     * navigation route target class.
     */
    private Class<?> interceptorClass;
    /**
     * navigation route thread mode.
     */
    private int priority;

    private InterceptorMeta() {
    }

    /**
     * Get an instance of InterceptorMeta
     */
    public static InterceptorMeta create(Class<?> interceptorClass, int priority) {
        InterceptorMeta result = new InterceptorMeta();
        result.interceptorClass = interceptorClass;
        result.priority = priority;
        return result;
    }

    public Class<?> getInterceptorClass() {
        return interceptorClass;
    }

    public int getPriority() {
        return priority;
    }

}
