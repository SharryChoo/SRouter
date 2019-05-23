package com.sharry.srouter.support.data;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.sharry.srouter.support.service.IService;

/**
 * The result associated with a navigation.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2/20/2019 10:41 AM
 */
public class Response {

    private final Request request;
    private IService service;
    private Fragment fragmentX;
    private android.app.Fragment fragment;
    private ActivityResult activityResult;

    public Response(@NonNull Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public Fragment getFragmentX() {
        return fragmentX;
    }

    public void setFragmentX(Fragment fragmentX) {
        this.fragmentX = fragmentX;
    }

    public android.app.Fragment getFragment() {
        return fragment;
    }

    public void setFragment(android.app.Fragment fragment) {
        this.fragment = fragment;
    }

    public IService getService() {
        return service;
    }

    public void setService(IService service) {
        this.service = service;
    }

    public void setActivityResult(int requestCode, int resultCode, Intent data) {
        activityResult = ActivityResult.create(requestCode, resultCode, data);
    }

    public ActivityResult getActivityResult() {
        return activityResult;
    }
}
