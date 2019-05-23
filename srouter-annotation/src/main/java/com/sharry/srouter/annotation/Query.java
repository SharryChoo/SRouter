package com.sharry.srouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for field, will auto generate target java file when build.
 *
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-05-23
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface Query {

    String value() default "";

}
