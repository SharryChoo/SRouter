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
public @interface Route {

    /**
     * Route path in module.
     */
    String path();

    /**
     * Thread mode for this Route.
     */
    ThreadMode mode() default ThreadMode.MAIN;

    /**
     * The interceptor Paths for this Route.
     */
    String[] interceptorPaths() default {};

    /**
     * Description for this Route.
     */
    String desc() default "";

}
