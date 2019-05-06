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
import com.sharry.srouter.support.service.IService;
import com.sharry.srouter.support.utils.Logger;
import com.sharry.srouter.support.utils.RouterCallbackFragment;
import com.sharry.srouter.support.utils.Utils;

/**
 * The final interceptor.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2/20/2019 9:29 AM
 */
public class FinalInterceptor implements IInterceptor {

    @Override
    public void intercept(@NonNull final Chain chain) {
        ChainContext context = chain.chainContext();
        Request request = context.request;
        Chain.Callback callback = context.callback;
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
                    // intercept success.
                    callback.onCompleted(response);
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
                    // intercept success.
                    callback.onCompleted(response);
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
                    // intercept success.
                    callback.onCompleted(response);
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
    }

    /**
     * Perform launch target activity.
     */
    private void performLaunchActivity(Context context,
                                       Intent intent,
                                       ActivityConfig config,
                                       Response response,
                                       Chain.Callback callback) {
        // Inject user config flags to intent.
        if (config == null) {
            config = new ActivityConfig.Builder().build();
        }
        // Inject flags.
        if (ActivityConfig.NON_FLAGS != config.getFlags()) {
            intent.setFlags(config.getFlags());
        }
        // perform launch activity.
        Activity activity = Utils.findActivity(context);
        if (Utils.isIllegalState(activity)) {
            // Activity is illegal, use application context jump.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchActivityActual(context, intent, config.getActivityOptions());
        } else {
            if (ActivityConfig.NON_REQUEST_CODE != config.getRequestCode()) {
                launchActivityForResultActual(activity, intent, config.getRequestCode(),
                        config.getActivityOptions(), response, callback);
            } else {
                launchActivityActual(activity, intent, config.getActivityOptions());
            }
        }
    }

    /**
     * Perform launch activity for result actual.
     */
    private void launchActivityForResultActual(Activity activity,
                                               Intent intent,
                                               int requestCode,
                                               ActivityOptionsCompat activityOptions,
                                               final Response response,
                                               final IInterceptor.Chain.Callback callback) {
        // Observer activity onActivityResult Callback.
        RouterCallbackFragment callbackFragment = RouterCallbackFragment.getInstance(activity);
        callbackFragment.setCallback(new RouterCallbackFragment.Callback() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                response.setActivityResult(requestCode, resultCode, data);
                callback.onCompleted(response);
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

    /**
     * Perform launch activity actual.
     */
    private void launchActivityActual(Context context, Intent intent,
                                      ActivityOptionsCompat activityOptions) {
        if (activityOptions != null && Utils.isJellyBean()) {
            context.startActivity(intent, activityOptions.toBundle());
        } else {
            context.startActivity(intent);
        }
    }

}
