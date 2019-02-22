package com.sharry.sroutersupport.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.sharry.sroutersupport.data.ActivityConfigs;

/**
 * The Fragment use to receive Target activity result data and callback by {@link ActivityConfigs.Callback}.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/11/30 15:46
 */
public class CallbackFragment extends Fragment {

    public static final String TAG = CallbackFragment.class.getSimpleName();

    /**
     * 获取一个添加到 Activity 中的 Fragment 的实例
     *
     * @param bind The activity associated with this fragment.
     * @return an instance of CallbackFragment.
     */
    public static CallbackFragment getInstance(@NonNull Activity bind) {
        CallbackFragment callbackFragment = findFragmentFromActivity(bind);
        if (callbackFragment == null) {
            callbackFragment = CallbackFragment.newInstance();
            FragmentManager fragmentManager = bind.getFragmentManager();
            fragmentManager.beginTransaction()
                    .add(callbackFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return callbackFragment;
    }

    private static CallbackFragment findFragmentFromActivity(@NonNull Activity activity) {
        return (CallbackFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    private static CallbackFragment newInstance() {
        return new CallbackFragment();
    }

    private ActivityConfigs.Callback mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Set callback for the fragment.
     */
    public void setCallback(ActivityConfigs.Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != mCallback) {
            mCallback.onActivityResult(requestCode, resultCode, data);
        }
    }

}