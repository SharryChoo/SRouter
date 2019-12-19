package com.sharry.srouter.support;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * The default forward activity to forward route request.
 *
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-12-16
 */
public final class DefaultForwardActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performRealAction(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        performRealAction(intent);
    }

    private void performRealAction(Intent intent) {
        ICall call = SRouter.request(intent)
                .newCall(this);
        call.post(new DispatchCallback() {
            @Override
            public void onSuccess(@NonNull Response response) {
                finish();
            }

            @Override
            public void onFailed(Throwable throwable) {
                finish();
            }

            @Override
            public void onCanceled() {
                finish();
            }
        });
    }

}
