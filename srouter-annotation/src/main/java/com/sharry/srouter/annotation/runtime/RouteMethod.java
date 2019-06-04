package com.sharry.srouter.annotation.runtime;

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
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RouteMethod {

    /**
     * The authority associated with Route URI.
     */
    String authority();

    /**
     * Route path associated with Route URI.
     */
    String path();

    /**
     * The interceptor URIs for this Route.
     */
    String[] interceptorURIs() default {};

}
