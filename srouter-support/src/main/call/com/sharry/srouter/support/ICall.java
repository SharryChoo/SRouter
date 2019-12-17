package com.sharry.srouter.support;

import androidx.annotation.NonNull;

/**
 * The post associated with once navigation.
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 5/6/2019 1:59 PM
 */
public interface ICall {

    ICall FAILED = new ICall() {

        @Override
        public ICancelable post(@NonNull DispatchCallback callback) {
            callback.onFailed(new NoRouteFoundException("Cannot find u want target."));
            return ICancelable.DEFAULT;
        }

        @Override
        @NonNull
        public <T> T adaptTo(@NonNull Class<T> adaptType) {
            throw new UnsupportedOperationException("This ICall cannot support adapt operation.");
        }

    };

    /**
     * Get response on LambdaCallback.(Recommend.)
     */
    ICancelable post(@NonNull DispatchCallback callback);

    /**
     * Adapter ICall to target.
     *
     * @return The target<T> instance.
     */
    @NonNull
    <T> T adaptTo(@NonNull Class<T> adaptType);

}
