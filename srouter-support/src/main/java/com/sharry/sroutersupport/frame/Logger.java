package com.sharry.sroutersupport.frame;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * @author think <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/15
 */
public class Logger {

    // 静态常量
    private static final String TAG_DEFAULT = Logger.class.getSimpleName();

    // 外界可控变量
    private static String TAG = Logger.class.getSimpleName();

    // 网络加载引擎
    private static ILoggerEngine sLoggerEngine;

    private Logger() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void init(ILoggerEngine loggerEngine) {
        sLoggerEngine = loggerEngine;
    }

    /**
     * =============================== Verbose =================================
     */
    public static void v(@NonNull CharSequence content) {
        v(defaultTag(), content.toString());
    }

    public static void v(@NonNull String tag, @NonNull CharSequence content) {
        ensure();
        sLoggerEngine.v(tag, content.toString());
    }

    public static void v(@NonNull CharSequence content, @NonNull Throwable e) {
        v(defaultTag(), e, content.toString());
    }

    public static void v(@NonNull String tag, @NonNull Throwable e, @NonNull CharSequence content) {
        ensure();
        sLoggerEngine.v(tag, e, content.toString());
    }

    /**
     * =============================== Debug =================================
     */
    public static void d(@NonNull CharSequence content) {
        d(defaultTag(), content.toString());
    }

    public static void d(@NonNull String tag, @NonNull CharSequence content) {
        ensure();
        sLoggerEngine.d(tag, content.toString());
    }

    public static void d(@NonNull CharSequence content, @NonNull Throwable e) {
        d(defaultTag(), e, content.toString());
    }

    public static void d(@NonNull String tag, @NonNull Throwable e, @NonNull CharSequence content) {
        ensure();
        sLoggerEngine.d(tag, e, content.toString());
    }

    /**
     * =============================== Info =================================
     */
    public static void i(@NonNull CharSequence content) {
        i(defaultTag(), content.toString());
    }

    public static void i(@NonNull String tag, @NonNull CharSequence content) {
        ensure();
        sLoggerEngine.i(tag, content.toString());
    }

    public static void i(@NonNull CharSequence content, @NonNull Throwable e) {
        i(defaultTag(), e, content.toString());
    }

    public static void i(@NonNull String tag, @NonNull Throwable e, @NonNull CharSequence content) {
        ensure();
        sLoggerEngine.i(tag, e, content.toString());
    }

    /**
     * =============================== Warn =================================
     */
    public static void w(@NonNull CharSequence content) {
        w(defaultTag(), content.toString());
    }

    public static void w(@NonNull String tag, @NonNull CharSequence content) {
        ensure();
        sLoggerEngine.w(tag, content.toString());
    }

    public static void w(@NonNull CharSequence content, @NonNull Throwable e) {
        w(defaultTag(), e, content.toString());
    }

    public static void w(@NonNull String tag, @NonNull Throwable e, @NonNull CharSequence content) {
        ensure();
        sLoggerEngine.w(tag, e, content.toString());
    }

    /**
     * =============================== Error =================================
     */
    public static void e(@NonNull CharSequence content) {
        e(defaultTag(), content.toString());
    }

    public static void e(@NonNull String tag, @NonNull CharSequence content) {
        ensure();
        sLoggerEngine.e(tag, content.toString());
    }

    public static void e(@NonNull CharSequence content, @NonNull Throwable e) {
        e(defaultTag(), e, content.toString());
    }

    public static void e(@NonNull String tag, @NonNull Throwable e, @NonNull CharSequence content) {
        ensure();
        sLoggerEngine.e(tag, e, content.toString());
    }

    /**
     * 确认 LoggerEngine 引擎是否初始化
     */
    private static void ensure() {
        if (sLoggerEngine == null) {
            sLoggerEngine = new ILoggerEngine.DefaultEngine();
            Log.e(TAG, "Recommend init your custom LoggerEngine.");
        }
    }

    /**
     * 获取默认的 TAG
     */
    private static String defaultTag() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // 获取线程任务栈中深度为 4 的元素信息
        StackTraceElement log = stackTrace[4];
        String className = log.getClassName();
        if (className.isEmpty()) {
            return TAG_DEFAULT;
        }
        // 截取最后一个位置的信息
        int subIndex = className.lastIndexOf(".") + 1;
        return className.substring(subIndex);
    }

}
