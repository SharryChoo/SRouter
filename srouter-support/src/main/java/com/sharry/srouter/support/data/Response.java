package com.sharry.srouter.support.data;

import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.sharry.srouter.support.providers.IProvider;

/**
 * The result associated with a navigation.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2/20/2019 10:41 AM
 */
public class Response {

    private IProvider provider;
    private Fragment fragmentV4;
    private android.app.Fragment fragment;
    private ActivityResult activityResult;

    public Fragment getFragmentV4() {
        return fragmentV4;
    }

    public void setFragmentV4(Fragment fragmentV4) {
        this.fragmentV4 = fragmentV4;
    }

    public android.app.Fragment getFragment() {
        return fragment;
    }

    public void setFragment(android.app.Fragment fragment) {
        this.fragment = fragment;
    }

    public IProvider getProvider() {
        return provider;
    }

    public void setProvider(IProvider provider) {
        this.provider = provider;
    }

    public void setActivityResult(int requestCode, int resultCode, Intent data) {
        activityResult = ActivityResult.create(requestCode, resultCode, data);
    }

    public ActivityResult getActivityResult() {
        return activityResult;
    }

}
