package com.sharry.srouter.support.interceptors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import com.sharry.srouter.support.data.ActivityOptions;
import com.sharry.srouter.support.data.Request;
import com.sharry.srouter.support.data.Response;
import com.sharry.srouter.support.providers.IProvider;
import com.sharry.srouter.support.utils.Logger;
import com.sharry.srouter.support.utils.RouterCallbackFragment;

/**
 * 最终用于导航的拦截器
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2/20/2019 9:29 AM
 */
public class NavigationInterceptor implements IInterceptor {

    @Override
    public void process(@NonNull final Chain chain) {
        ChainContext chainContext = chain.chainContext();
        navigationActual(
                chainContext.getBaseContext(),
                chainContext.getRequest(),
                chainContext.getCallback()
        );
    }

    /**
     * Actual perform navigation.
     */
    private void navigationActual(Context context, Request request, IInterceptor.Chain.Callback callback) {
        Response response = new Response();
        switch (request.getType()) {
            case ACTIVITY:
                Intent intent = new Intent(context, request.getRouteClass());
                // Inject user extra info to intent.
                intent.putExtras(request.getDatum());
                // Real perform activity launch.
                performLaunchActivity(context, intent, request.getActivityOptions(), response, callback);
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
                    // process success.
                    callback.onDispatched(response);
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
                    // process success.
                    callback.onDispatched(response);
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
                    // process success.
                    callback.onDispatched(response);
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
                                       ActivityOptions options,
                                       Response response,
                                       IInterceptor.Chain.Callback callback) {
        // Inject user config flags to intent.
        if (options == null) {
            options = new ActivityOptions.Builder().build();
        }
        // Verify flags.
        if (ActivityOptions.NON_FLAGS != options.getFlags()) {
            intent.setFlags(options.getFlags());
        } else if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        // perform launch activity.
        if (ActivityOptions.NON_REQUEST_CODE != options.getRequestCode()) {
            if (context instanceof Activity) {
                launchActivityForResultActual((Activity) context, intent, options.getRequestCode(),
                        options.getActivityOptions(), response, callback);
            } else {
                launchActivityActual(context, intent, options.getActivityOptions());
            }
        } else {
            launchActivityActual(context, intent, options.getActivityOptions());
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
                callback.onDispatched(response);
            }
        });
        // Launch activity with Activity options.
        if (activityOptions != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            callbackFragment.startActivityForResult(intent, requestCode, activityOptions.toBundle());
        } else { // Launch activity without Activity options.
            callbackFragment.startActivityForResult(intent, requestCode);
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
