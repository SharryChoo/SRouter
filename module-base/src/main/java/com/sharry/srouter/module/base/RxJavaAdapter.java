package com.sharry.srouter.module.base;

import androidx.annotation.NonNull;

import com.sharry.srouter.support.call.ICall;
import com.sharry.srouter.support.call.ICallAdapter;
import com.sharry.srouter.support.data.Response;

import io.reactivex.Observable;

/**
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 5/6/2019 6:10 PM
 */
public class RxJavaAdapter implements ICallAdapter<Observable<Response>> {

    @Override
    public Observable<Response> adapt(@NonNull ICall call) {
        return new CallObservable(call);
    }

    @Override
    public Class<Observable> adaptType() {
        return Observable.class;
    }

}
