package com.sharry.srouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link RouteInterceptor} will auto generate target java file when build.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/16 23:20
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface RouteInterceptor {

    /**
     * Route key associated with RouteInterceptor URI.
     */
    String value();

    /**
     * Priority for the interceptor.
     * <pre>
     * Priority key Range in
     *      [
     *           {@link PriorityRange#MINIMUM},
     *           {@link PriorityRange#MAXIMUM}
     *      ]
     * </pre>
     * If two priority key is equivalent, the path mapper special IInterceptor
     * will be performed in {@link Route#interceptorURIs()} add order.
     */
    int priority() default 1;

}
