package com.sharry.srouter.support.call;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sharry.srouter.support.data.Response;
import com.sharry.srouter.support.facade.Callback;

/**
 * The enqueue associated with once navigation.
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 5/6/2019 1:59 PM
 */
public interface ICall {

    ICall DEFAULT = new ICall() {
        @Override
        public Response call() {
            return null;
        }

        @Override
        public void enqueue(@Nullable Callback callback) {
        }

        @Override
        @NonNull
        public <T> T adaptTo(@NonNull Class<T> adaptType) {
            throw new UnsupportedOperationException("This ICall cannot support adapt operation.");
        }

    };

    /**
     * Get response directly.(Dangerous.)
     * <p>
     * If call activity for result. do not use sync call. It will blocking current thread.
     */
    @Nullable
    Response call();

    /**
     * Get response on Callback.(Recommend.)
     */
    void enqueue(@Nullable Callback callback);

    /**
     * Adapter ICall to target.
     *
     * @return The target<T> instance.
     */
    @NonNull
    <T> T adaptTo(@NonNull Class<T> adaptType);

}
