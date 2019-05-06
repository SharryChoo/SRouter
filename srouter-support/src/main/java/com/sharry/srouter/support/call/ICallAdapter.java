package com.sharry.srouter.support.call;

import androidx.annotation.NonNull;

/**
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 5/6/2019 4:48 PM
 */
public interface ICallAdapter<T> {

    ICallAdapter<ICall> DEFAULT = new ICallAdapter<ICall>() {
        @Override
        public ICall adapt(@NonNull ICall call) {
            return call;
        }

        @Override
        public Class<ICall> adaptType() {
            return ICall.class;
        }

    };

    T adapt(@NonNull ICall call);

    Class<?> adaptType();

}
