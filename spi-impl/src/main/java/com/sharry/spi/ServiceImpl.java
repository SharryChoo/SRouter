package com.sharry.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServiceImpl {

    /**
     * 接口
     */
    Class<?> api();

    /**
     * 是否需要延时初始化，{@code true}则表示在使用时初始化，否则会在app启动时实例化
     * {@see sigleton}，非单例模式下，每次都会重新初始化
     */
    boolean delay() default true;

    /**
     * 默认是单例
     */
    boolean singleton() default true;

}