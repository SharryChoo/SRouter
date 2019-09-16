package com.sharry.srouter.support;

import androidx.annotation.NonNull;

/**
 * @author think <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/15
 */
class Logger {

    private static final String TAG_DEFAULT = Logger.class.getSimpleName();
    private static final ILoggerEngine sLoggerEngine = new ILoggerEngine.DefaultEngine();

    private Logger() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * =============================== Verbose =================================
     */
    public static void v(@NonNull CharSequence content) {
        v(defaultTag(), content.toString());
    }

    static void v(@NonNull String tag, @NonNull CharSequence content) {
        sLoggerEngine.v(tag, content.toString());
    }

    static void v(@NonNull CharSequence content, @NonNull Throwable e) {
        v(defaultTag(), e, content.toString());
    }

    static void v(@NonNull String tag, @NonNull Throwable e, @NonNull CharSequence content) {
        sLoggerEngine.v(tag, e, content.toString());
    }

    /**
     * =============================== Debug =================================
     */
    static void d(@NonNull CharSequence content) {
        d(defaultTag(), content.toString());
    }

    static void d(@NonNull String tag, @NonNull CharSequence content) {
        sLoggerEngine.d(tag, content.toString());
    }

    static void d(@NonNull CharSequence content, @NonNull Throwable e) {
        d(defaultTag(), e, content.toString());
    }

    static void d(@NonNull String tag, @NonNull Throwable e, @NonNull CharSequence content) {
        sLoggerEngine.d(tag, e, content.toString());
    }

    /**
     * =============================== Info =================================
     */
    static void i(@NonNull CharSequence content) {
        i(defaultTag(), content.toString());
    }

    static void i(@NonNull String tag, @NonNull CharSequence content) {
        sLoggerEngine.i(tag, content.toString());
    }

    public static void i(@NonNull CharSequence content, @NonNull Throwable e) {
        i(defaultTag(), e, content.toString());
    }

    static void i(@NonNull String tag, @NonNull Throwable e, @NonNull CharSequence content) {
        sLoggerEngine.i(tag, e, content.toString());
    }

    /**
     * =============================== Warn =================================
     */
    static void w(@NonNull CharSequence content) {
        w(defaultTag(), content.toString());
    }

    static void w(@NonNull String tag, @NonNull CharSequence content) {
        sLoggerEngine.w(tag, content.toString());
    }

    public static void w(@NonNull CharSequence content, @NonNull Throwable e) {
        w(defaultTag(), e, content.toString());
    }

    static void w(@NonNull String tag, @NonNull Throwable e, @NonNull CharSequence content) {
        sLoggerEngine.w(tag, e, content.toString());
    }

    /**
     * =============================== Error =================================
     */
    static void e(@NonNull CharSequence content) {
        e(defaultTag(), content.toString());
    }

    static void e(@NonNull String tag, @NonNull CharSequence content) {
        sLoggerEngine.e(tag, content.toString());
    }

    static void e(@NonNull CharSequence content, @NonNull Throwable e) {
        e(defaultTag(), e, content.toString());
    }

    static void e(@NonNull String tag, @NonNull Throwable e, @NonNull CharSequence content) {
        sLoggerEngine.e(tag, e, content.toString());
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
