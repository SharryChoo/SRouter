package com.sharry.srouter.support.interceptors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import com.sharry.srouter.support.data.ActivityConfig;
import com.sharry.srouter.support.data.Request;
import com.sharry.srouter.support.data.Response;
import com.sharry.srouter.support.scheduler.IScheduler;
import com.sharry.srouter.support.scheduler.SchedulerFactory;
import com.sharry.srouter.support.scheduler.ThreadMode;
import com.sharry.srouter.support.service.IService;
import com.sharry.srouter.support.utils.Logger;
import com.sharry.srouter.support.utils.RouterCallbackFragment;
import com.sharry.srouter.support.utils.Utils;

/**
 * The final navigation interceptor.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2/20/2019 9:29 AM
 */
public class NavigationInterceptor implements IInterceptor {

    /**
     * The lock use to wait activity result.
     */
    private final Object mLock = new Object();

    @Override
    public Response intercept(@NonNull final Chain chain) {
        ChainContext context = chain.chainContext();
        Request request = context.request;
        Response response = new Response(request);
        switch (request.getType()) {
            case ACTIVITY:
                Intent intent = new Intent(context, request.getRouteClass());
                // Inject user extra info to intent.
                intent.putExtras(request.getDatum());
                // Real perform activity launch.
                performLaunchActivity(context, intent, request.getActivityConfig(), response, callback);
                break;
            case FRAGMENT:
                try {
                    // Instantiation fragment by class name.
                    android.app.Fragment fragment = (android.app.Fragment) request.getRouteClass().newInstance();
                    fragment.setArguments(request.getDatum());
                    // Inject fragment to request provider.
                    response.setFragment(fragment);
                } catch (InstantiationException e) {
                    Logger.e("Instantiation " + request.getRouteClass().getSimpleName()
                            + " failed.", e);
                } catch (IllegalAccessException e) {
                    Logger.e("Please ensure " + request.getRouteClass() +
                            "  empty arguments constructor is assessable.", e);
                }
                break;
            case FRAGMENT_V4:
                try {
                    // Instantiation fragment by class name.
                    Fragment fragmentV4 = (Fragment) request.getRouteClass().newInstance();
                    fragmentV4.setArguments(request.getDatum());
                    // Inject fragment to request provider.
                    response.setFragmentV4(fragmentV4);
                } catch (InstantiationException e) {
                    Logger.e("Instantiation " + request.getRouteClass().getSimpleName()
                            + " failed.", e);
                } catch (IllegalAccessException e) {
                    Logger.e("Please ensure " + request.getRouteClass() +
                            "  empty arguments constructor is assessable.", e);
                }
                break;
            case SERVICE:
                try {
                    IService service = (IService) request.getRouteClass().newInstance();
                    service.init(context);
                    response.setService(service);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    Logger.e("Instantiation " + request.getRouteClass().getSimpleName()
                            + " failed.", e);
                } catch (IllegalAccessException e) {
                    Logger.e("Please ensure " + request.getRouteClass() +
                            "  empty arguments constructor is assessable.", e);
                }
                break;
            default:
                break;
        }
        return response;
    }

    /**
     * Perform launch target activity.
     */
    private void performLaunchActivity(Context context, Intent intent, Request request, Response response) {
        // Inject flags.
        if (Request.NON_FLAGS != request.getFlags()) {
            intent.setFlags(request.getFlags());
        }
        // perform launch activity.
        Activity activity = Utils.findActivity(context);
        if (Utils.isIllegalState(activity)) {
            // Activity at illegal state, use application context do jump.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchActivityActual(context, intent, request.getActivityOptions());
        } else {
            if (Request.NON_REQUEST_CODE != request.getRequestCode()) {
                launchActivityForResultActual(activity, intent, request.getRequestCode(),
                        request.getActivityOptions(), response);
            } else {
                launchActivityActual(activity, intent, request.getActivityOptions());
            }
        }
    }

    /**
     * Perform launch activity for result actual.
     */
    private void launchActivityForResultActual(final Activity activity,
                                               final Intent intent,
                                               final int requestCode,
                                               final ActivityOptionsCompat activityOptions,
                                               final Response response) {
        IScheduler scheduler = SchedulerFactory.create(ThreadMode.MAIN_THREAD);
        // Launch activity with Activity options.
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                // Observer activity onActivityResult Callback.
                final RouterCallbackFragment callbackFragment = RouterCallbackFragment.getInstance(activity);
                callbackFragment.setCallback(new RouterCallbackFragment.Callback() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, Intent data) {
                        // Activity data is injected.
                        response.setActivityResult(requestCode, resultCode, data);
                        // Release lock.
                        synchronized (mLock) {
                            mLock.notifyAll();
                        }
                    }
                });
                // Launch activity with Activity options.
                if (activityOptions != null && Utils.isJellyBean()) {
                    callbackFragment.startActivityForResult(intent, requestCode, activityOptions.toBundle());
                } else {
                    // Launch activity without Activity options.
                    callbackFragment.startActivityForResult(intent, requestCode);
                }
            }
        });
        // Waiting for activity data result.
        try {
            synchronized (mLock) {
                Logger.i(Thread.currentThread().getName() + " is blocking for activity result.");
                mLock.wait();
                Logger.i(Thread.currentThread().getName() + " blocking is released.");
            }
        } catch (InterruptedException e) {
            Logger.e(e.getMessage(), e);
        }
    }

    /**
     * Perform launch activity actual.
     */
    private void launchActivityActual(Context context, Intent intent, ActivityOptionsCompat activityOptions) {
        if (activityOptions != null && Utils.isJellyBean()) {
            context.startActivity(intent, activityOptions.toBundle());
        } else {
            context.startActivity(intent);
        }
    }

}
