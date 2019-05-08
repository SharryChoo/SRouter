package com.sharry.srouter.support.call;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sharry.srouter.support.data.Response;
import com.sharry.srouter.support.facade.Callback;
import com.sharry.srouter.support.interceptors.IInterceptor;

/**
 * The post associated with once navigation.
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 5/6/2019 1:59 PM
 */
public interface ICall {

    ICall DEFAULT = new ICall() {

        @Override
        public void post(@NonNull IInterceptor.ChainCallback callback) {

        }

        @Override
        @NonNull
        public <T> T adaptTo(@NonNull Class<T> adaptType) {
            throw new UnsupportedOperationException("This ICall cannot support adapt operation.");
        }

    };

    /**
     * Get response on Callback.(Recommend.)
     */
    void post(@NonNull IInterceptor.ChainCallback callback);

    /**
     * Adapter ICall to target.
     *
     * @return The target<T> instance.
     */
    @NonNull
    <T> T adaptTo(@NonNull Class<T> adaptType);

}
