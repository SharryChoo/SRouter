package com.sharry.srouter.support;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author think <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/15
 */
class Logger {

    private static final String TAG_DEFAULT = "SRouter";
    private static boolean isDebug = false;
    private static final ILoggerEngine REAL_ENGINE = new ILoggerEngine.DefaultEngine();
    private static final ILoggerEngine PROXY_ENGINE = (ILoggerEngine) Proxy.newProxyInstance(
            ILoggerEngine.class.getClassLoader(),
            new Class<?>[]{ILoggerEngine.class},
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (isDebug) {
                        return method.invoke(REAL_ENGINE, args);
                    }
                    return null;
                }
            });

    private Logger() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    static void isDebug(boolean debug) {
        isDebug = debug;
    }

    /**
     * =============================== Verbose =================================
     */
    static void v(@NonNull CharSequence content) {
        v(defaultTag(), content.toString());
    }

    static void v(@NonNull String tag, @NonNull CharSequence content) {
        PROXY_ENGINE.v(tag, content.toString());
    }

    static void v(@NonNull CharSequence content, @NonNull Throwable e) {
        v(defaultTag(), e, content.toString());
    }

    static void v(@NonNull String tag, @NonNull Throwable e, @NonNull CharSequence content) {
        PROXY_ENGINE.v(tag, e, content.toString());
    }

    /**
     * =============================== Debug =================================
     */
    static void d(@NonNull CharSequence content) {
        d(defaultTag(), content.toString());
    }

    static void d(@NonNull String tag, @NonNull CharSequence content) {
        PROXY_ENGINE.d(tag, content.toString());
    }

    static void d(@NonNull CharSequence content, @NonNull Throwable e) {
        d(defaultTag(), e, content.toString());
    }

    static void d(@NonNull String tag, @NonNull Throwable e, @NonNull CharSequence content) {
        PROXY_ENGINE.d(tag, e, content.toString());
    }

    /**
     * =============================== Info =================================
     */
    static void i(@NonNull CharSequence content) {
        i(defaultTag(), content.toString());
    }

    static void i(@NonNull String tag, @NonNull CharSequence content) {
        PROXY_ENGINE.i(tag, content.toString());
    }

    public static void i(@NonNull CharSequence content, @NonNull Throwable e) {
        i(defaultTag(), e, content.toString());
    }

    static void i(@NonNull String tag, @NonNull Throwable e, @NonNull CharSequence content) {
        PROXY_ENGINE.i(tag, e, content.toString());
    }

    /**
     * =============================== Warn =================================
     */
    static void w(@NonNull CharSequence content) {
        w(defaultTag(), content.toString());
    }

    static void w(@NonNull String tag, @NonNull CharSequence content) {
        PROXY_ENGINE.w(tag, content.toString());
    }

    public static void w(@NonNull CharSequence content, @NonNull Throwable e) {
        w(defaultTag(), e, content.toString());
    }

    static void w(@NonNull String tag, @NonNull Throwable e, @NonNull CharSequence content) {
        PROXY_ENGINE.w(tag, e, content.toString());
    }

    /**
     * =============================== Error =================================
     */
    static void e(@NonNull CharSequence content) {
        e(defaultTag(), content.toString());
    }

    static void e(@NonNull String tag, @NonNull CharSequence content) {
        PROXY_ENGINE.e(tag, content.toString());
    }

    static void e(@NonNull CharSequence content, @NonNull Throwable e) {
        e(defaultTag(), e, content.toString());
    }

    static void e(@NonNull String tag, @NonNull Throwable e, @NonNull CharSequence content) {
        PROXY_ENGINE.e(tag, e, content.toString());
    }

    private static String defaultTag() {
        return TAG_DEFAULT;
    }

}
