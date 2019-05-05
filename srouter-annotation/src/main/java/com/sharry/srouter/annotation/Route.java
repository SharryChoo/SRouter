package com.sharry.srouter.annotation;

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
public @interface Route {

    /**
     * The authority associated with Route URI.
     */
    String authority();

    /**
     * Route path associated with Route URI.
     */
    String path();

    /**
     * Thread mode for this Route.
     */
    ThreadMode mode() default ThreadMode.SYNC;

    /**
     * The interceptor URIs for this Route.
     */
    String[] interceptorURIs() default {};

    /**
     * Description for this Route.
     */
    String desc() default "";

}
