package com.sharry.srouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for field, will auto generate target java file when build.
 * <p>
 * Current support bind filed type have {@link QueryType}
 *
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-05-23
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface Query {

    /**
     * The key that u want bind.
     */
    String key() default "";

    /**
     * The desc associated with marked field.
     */
    String desc() default "";

}
