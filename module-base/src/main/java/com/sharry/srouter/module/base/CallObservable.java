package com.sharry.srouter.module.base;

import androidx.annotation.NonNull;

import com.sharry.srouter.support.call.ICall;
import com.sharry.srouter.support.data.Response;
import com.sharry.srouter.support.facade.Callback;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Thanks for RxJava.
 *
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 5/6/2019 6:16 PM
 */
final class CallObservable extends Observable<Response> {

    private final ICall originalCall;

    CallObservable(ICall originalCall) {
        this.originalCall = originalCall;
    }

    @Override
    protected void subscribeActual(final Observer<? super Response> observer) {
        // Since Call is a one-shot type, clone it for each new observer.
        ICall call = originalCall;
        observer.onSubscribe(new CallDisposable(call));
        Response response = call.call();
        observer.onNext(response);
    }

    private static final class CallDisposable implements Disposable {

        private final ICall call;

        CallDisposable(ICall call) {
            this.call = call;
        }

        @Override
        public void dispose() {

        }

        @Override
        public boolean isDisposed() {
            return false;
        }
    }
}