package com.sharry.srouter.support.call;

import androidx.annotation.Nullable;

import com.sharry.srouter.support.facade.Callback;

/**
 * The call associated with once navigation.
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 5/6/2019 1:59 PM
 */
public interface ICall {

    ICall DEFAULT = new ICall() {
        @Override
        public void call() {

        }

        @Override
        public void call(@Nullable Callback callback) {
        }

        @Override
        public <T> T adaptTo(Class<T> adaptType) {
            return null;
        }

    };

    void call();

    void call(@Nullable Callback callback);

    <T> T adaptTo(Class<T> adaptType);

}
