package com.sharry.srouter.module.base;

import androidx.annotation.NonNull;

import com.sharry.srouter.support.ICall;
import com.sharry.srouter.support.ICallAdapter;

/**
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 5/6/2019 6:10 PM
 */
public class RxJavaAdapter implements ICallAdapter<ResponseObservable> {

    @Override
    public ResponseObservable adapt(@NonNull ICall call) {
        return new ResponseObservable(call);
    }

    @Override
    public Class<ResponseObservable> adaptType() {
        return ResponseObservable.class;
    }

}
