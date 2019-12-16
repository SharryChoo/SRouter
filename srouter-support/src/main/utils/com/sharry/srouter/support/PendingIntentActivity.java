package com.sharry.srouter.support;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.lang.ref.SoftReference;

/**
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-12-16
 */
public final class PendingIntentActivity extends Activity {

    static final String EXTRA_INTENT_KEY = "EXTRA_INTENT_KEY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int key = getIntent().getIntExtra(EXTRA_INTENT_KEY, -1);
        if (key == -1) {
            finish();
            return;
        }
        // fetch from cache.
        SoftReference<PendingRunnable> sfPendingRunnable = DataSource.PENDING_RUNNABLES.get(key);
        // remove from cache.
        DataSource.PENDING_RUNNABLES.remove(key);
        // run it.
        PendingRunnable pendingRunnable;
        if (sfPendingRunnable != null && (pendingRunnable = sfPendingRunnable.get()) != null) {
            pendingRunnable.run(this);
        } else {
            finish();
        }
    }

}
