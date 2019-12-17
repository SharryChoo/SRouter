package com.sharry.srouter.support;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;

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
    private Object fragment;
    private ActivityResult activityResult;
    private PendingIntent pendingIntent;

    public Response(@NonNull Request request) {
        this.request = request;
    }

    /**
     * Get Request that the called.
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Get a parcel of ActivityResult that route fetched.
     *
     * @return A parcel of ActivityResult
     */
    public ActivityResult getActivityResult() {
        return activityResult;
    }

    /**
     * Get a fragment instance that route fetched.
     *
     * @param <Fragment> the version that u target Fragment.
     * @return A fragment instance that route fetched.
     */
    public <Fragment> Fragment getFragment() {
        return (Fragment) fragment;
    }

    /**
     * Get An IService instance that route fetched.
     *
     * @return An IService instance that route fetched.
     */
    public IService getService() {
        return service;
    }


    /**
     * Get An PendingIntent that route fetched.
     */
    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setFragment(Object fragment) {
        this.fragment = fragment;
    }

    public void setService(IService service) {
        this.service = service;
    }

    public void setActivityResult(int requestCode, int resultCode, Intent data) {
        activityResult = ActivityResult.create(requestCode, resultCode, data);
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }

}
