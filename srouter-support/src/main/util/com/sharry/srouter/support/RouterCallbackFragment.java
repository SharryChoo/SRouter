package com.sharry.srouter.support;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

/**
 * The Fragment use to receive Target activity result data with callback.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/11/30 15:46
 */
public final class RouterCallbackFragment extends Fragment {

    public static final String TAG = RouterCallbackFragment.class.getSimpleName();
    private Callback mCallback;

    /**
     * 获取一个添加到 Activity 中的 Fragment 的实例
     *
     * @param bind The activity associated with this fragment.
     * @return an instance of RouterCallbackFragment.
     */
    public static RouterCallbackFragment getInstance(@NonNull Activity bind) {
        RouterCallbackFragment callbackFragment = findFragmentFromActivity(bind);
        if (callbackFragment == null) {
            callbackFragment = RouterCallbackFragment.newInstance();
            FragmentManager fragmentManager = bind.getFragmentManager();
            fragmentManager.beginTransaction()
                    .add(callbackFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return callbackFragment;
    }

    private static RouterCallbackFragment findFragmentFromActivity(@NonNull Activity activity) {
        return (RouterCallbackFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    private static RouterCallbackFragment newInstance() {
        return new RouterCallbackFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != mCallback) {
            mCallback.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Set callback for the fragment.
     */
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public interface Callback {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

}
