package com.sharry.srouter.support;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

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
        int key = getIntent().getIntExtra(EXTRA_INTENT_KEY, 10);
        if (key == -1) {
            finish();
            return;
        }
        // fetch PendingRunnable
        PendingRunnable pendingRunnable = DataSource.PENDING_RUNNABLES.get(key);
        if (pendingRunnable != null) {
            // run it.
            pendingRunnable.run(this);
            // remove from cache.
            DataSource.PENDING_RUNNABLES.remove(key);
        } else {
            finish();
        }
    }

}
