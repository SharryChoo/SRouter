package com.sharry.srouterannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for class, will auto generate target java file when build.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/16 23:20
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface RouteInterceptor {

    /**
     * Route authority in module.
     */
    String authority();

    /**
     * Priority for the interceptor.
     */
    int priority() default 1;

}
