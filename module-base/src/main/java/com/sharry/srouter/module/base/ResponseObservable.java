package com.sharry.srouter.module.base;

import androidx.annotation.NonNull;

import com.sharry.srouter.support.ICancelable;
import com.sharry.srouter.support.ICall;
import com.sharry.srouter.support.Response;
import com.sharry.srouter.support.IInterceptor;

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
public final class ResponseObservable extends Observable<Response> {

    private final ICall call;

    ResponseObservable(ICall call) {
        this.call = call;
    }

    @Override
    protected void subscribeActual(final Observer<? super Response> observer) {
        // Since Call is a one-shot type, clone it for each new observer.
        ICancelable cancelable = call.post(new IInterceptor.ChainCallback() {
            @Override
            public void onSuccess(@NonNull Response response) {
                observer.onNext(response);
            }

            @Override
            public void onFailed(Throwable throwable) {
                observer.onError(throwable);
            }

            @Override
            public void onCanceled() {
                // do nothing.
            }
        });
        observer.onSubscribe(new CallDisposable(cancelable));
    }

    private static final class CallDisposable implements Disposable {

        private final ICancelable cancelable;

        CallDisposable(ICancelable cancelable) {
            this.cancelable = cancelable;
        }

        @Override
        public void dispose() {
            cancelable.cancel();
        }

        @Override
        public boolean isDisposed() {
            return cancelable.isCanceled();
        }
    }
}