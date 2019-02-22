package com.sharry.sroutersupport.interceptors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;

import com.sharry.sroutersupport.data.ActivityConfigs;
import com.sharry.sroutersupport.data.Request;
import com.sharry.sroutersupport.data.Response;
import com.sharry.sroutersupport.providers.IProvider;
import com.sharry.sroutersupport.utils.RouterCallbackFragment;
import com.sharry.sroutersupport.utils.Logger;

/**
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2/20/2019 9:29 AM
 */
public class NavigationInterceptor implements IInterceptor {

    @Override
    public Response process(Chain chain) {
        return navigationActual(chain.context(), chain.request());
    }

    /**
     * Actual perform navigation.
     */
    private Response navigationActual(@NonNull Context context, Request request) {
        Response response = new Response();
        switch (request.getType()) {
            case ACTIVITY:
                Intent intent = new Intent(context, request.getRouteClass());
                // Inject user extra info to intent.
                intent.putExtras(request.getDatum());
                // Real perform activity launch.
                performLaunchActivity(
                        context,
                        intent,
                        request.getActivityConfigs()
                );
                break;
            case SERVICE:
                intent = new Intent(context, request.getRouteClass());
                context.startActivity(intent);
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
            case PROVIDER:
                try {
                    IProvider provider = (IProvider) request.getRouteClass().newInstance();
                    response.setProvider(provider);
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
    private void performLaunchActivity(@NonNull Context context,
                                       @NonNull Intent intent,
                                       @Nullable ActivityConfigs configs) {
        // Inject user config flags to intent.
        if (configs == null) {
            configs = new ActivityConfigs.Builder().build();
        }
        // Verify flags.
        if (ActivityConfigs.NON_FLAGS != configs.getFlags()) {
            intent.setFlags(configs.getFlags());
        } else if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        // perform launch activity.
        if (ActivityConfigs.NON_REQUEST_CODE != configs.getRequestCode()) {
            if (context instanceof Activity) {
                launchActivityForResultActual((Activity) context, intent, configs.getRequestCode(),
                        configs.getActivityOptions(), configs.getCallback());
            } else {
                launchActivityActual(context, intent, configs.getActivityOptions());
            }
        } else {
            launchActivityActual(context, intent, configs.getActivityOptions());
        }
    }

    /**
     * Perform launch activity for result actual.
     */
    private void launchActivityForResultActual(@NonNull Activity activity,
                                               @NonNull Intent intent,
                                               int requestCode,
                                               @Nullable ActivityOptionsCompat activityOptions,
                                               @Nullable ActivityConfigs.Callback callback) {
        // Launch activity with Activity options.
        if (activityOptions != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // Observer activity onActivityResult Callback.
            if (callback != null) {
                RouterCallbackFragment callbackFragment = RouterCallbackFragment.getInstance(activity);
                callbackFragment.setCallback(callback);
                callbackFragment.startActivityForResult(intent, requestCode, activityOptions.toBundle());
            } else {
                activity.startActivityForResult(intent, requestCode, activityOptions.toBundle());
            }
        } else { // Launch activity without Activity options.
            // Observer activity onActivityResult Callback.
            if (callback != null) {
                RouterCallbackFragment callbackFragment = RouterCallbackFragment.getInstance(activity);
                callbackFragment.setCallback(callback);
                callbackFragment.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivityForResult(intent, requestCode);
            }
        }
    }

    /**
     * Perform launch activity actual.
     */
    private void launchActivityActual(@NonNull Context context, @NonNull Intent intent,
                                      @Nullable ActivityOptionsCompat activityOptions) {
        if (activityOptions != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            context.startActivity(intent, activityOptions.toBundle());
        } else {
            context.startActivity(intent);
        }
    }

}
