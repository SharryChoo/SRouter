package com.sharry.srouter.support.utils;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * LoggerEngine
 *
 * @author think <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/15
 */
public interface ILoggerEngine {

    void v(@NonNull String tag, @NonNull String content);

    void v(@NonNull String tag, @NonNull Throwable e, @NonNull String content);

    void d(@NonNull String tag, @NonNull String content);

    void d(@NonNull String tag, @NonNull Throwable e, @NonNull String content);

    void i(@NonNull String tag, @NonNull String content);

    void i(@NonNull String tag, @NonNull Throwable e, @NonNull String content);

    void w(@NonNull String tag, @NonNull String content);

    void w(@NonNull String tag, @NonNull Throwable e, @NonNull String content);

    void e(@NonNull String tag, @NonNull String content);

    void e(@NonNull String tag, @NonNull Throwable e, @NonNull String content);

    class DefaultEngine implements ILoggerEngine {

        @Override
        public void v(@NonNull String tag, @NonNull String content) {
            Log.v(tag, content);
        }

        @Override
        public void v(@NonNull String tag, @NonNull Throwable e, @NonNull String content) {
            Log.v(tag, content, e);
        }

        @Override
        public void d(@NonNull String tag, @NonNull String content) {
            Log.d(tag, content);
        }

        @Override
        public void d(@NonNull String tag, @NonNull Throwable e, @NonNull String content) {
            Log.d(tag, content, e);
        }

        @Override
        public void i(@NonNull String tag, @NonNull String content) {
            Log.i(tag, content);
        }

        @Override
        public void i(@NonNull String tag, @NonNull Throwable e, @NonNull String content) {
            Log.i(tag, content, e);
        }

        @Override
        public void w(@NonNull String tag, @NonNull String content) {
            Log.w(tag, content);
        }

        @Override
        public void w(@NonNull String tag, @NonNull Throwable e, @NonNull String content) {
            Log.w(tag, content, e);
        }

        @Override
        public void e(@NonNull String tag, @NonNull String content) {
            Log.e(tag, content);
        }

        @Override
        public void e(@NonNull String tag, @NonNull Throwable e, @NonNull String content) {
            Log.e(tag, content, e);
        }
    }
}
