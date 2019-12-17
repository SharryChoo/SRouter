package com.sharry.srouter.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The final navigation interceptor.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2/20/2019 9:29 AM
 */
class NavigationInterceptor implements IInterceptor {

    @Override
    public void intercept(@NonNull final Chain chain) {
        ChainContext context = chain.context();
        Request request = context.request;
        Response response = new Response(request);
        RouteMeta meta = request.getRouteMeta();
        assert meta != null;
        switch (meta.getType()) {
            case ACTIVITY:
                Intent intent = new Intent(context, meta.getRouteClass());
                // Inject user extra info to intent.
                intent.putExtras(request.getDatum());
                // Real perform activity launch.
                performLaunchActivity(context, intent, request, response, chain.callback());
                break;
            case FRAGMENT:
            case FRAGMENT_X:
            case FRAGMENT_V4:
                try {
                    // Instantiation fragment by class name.
                    Object fragment = meta.getRouteClass().newInstance();
                    // Invoke Fragment.setArguments.
                    Method method = meta.getRouteClass().getMethod("setArguments",
                            Bundle.class);
                    method.invoke(fragment, request.getDatum());
                    // Inject fragment to request provider.
                    response.setFragment(fragment);
                    chain.callback().onSuccess(response);
                } catch (InstantiationException e) {
                    Logger.e("Instantiation " + meta.getRouteClass().getSimpleName()
                            + " failed.", e);
                    chain.callback().onFailed(e);
                } catch (IllegalAccessException e) {
                    Logger.e("Please ensure " + meta.getRouteClass() +
                            "  empty arguments constructor is assessable.", e);
                    chain.callback().onFailed(e);
                } catch (NoSuchMethodException e) {
                    chain.callback().onFailed(e);
                } catch (InvocationTargetException e) {
                    chain.callback().onFailed(e);
                }
                break;
            case SERVICE:
                try {
                    IService service = (IService) meta.getRouteClass().newInstance();
                    service.attach(context);
                    response.setService(service);
                } catch (InstantiationException e) {
                    Logger.e("Instantiation " + meta.getRouteClass().getSimpleName()
                            + " failed.", e);
                    chain.callback().onFailed(e);
                } catch (IllegalAccessException e) {
                    Logger.e("Please ensure " + meta.getRouteClass() +
                            "  empty arguments constructor is assessable.", e);
                    chain.callback().onFailed(e);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Perform launch target activity.
     */
    private void performLaunchActivity(Context context, Intent intent, Request request,
                                       Response response, DispatchCallback callback) {
        // Inject flags.
        if (Request.NON_FLAGS != request.getActivityFlags()) {
            intent.setFlags(request.getActivityFlags());
        }
        // perform launch activity.
        Activity activity = Utils.findActivity(context);
        if (Utils.isIllegalState(activity)) {
            // Activity at illegal state, use application context do jump.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchActivityActual(SRouterImpl.sAppContext, intent, request.getActivityOptions(), response, callback);
        } else {
            if (Request.NON_REQUEST_CODE != request.getRequestCode()) {
                launchActivityForResultActual(activity, intent, request.getRequestCode(),
                        request.getActivityOptions(), response, callback);
            } else {
                launchActivityActual(activity, intent, request.getActivityOptions(), response, callback);
            }
        }
    }

    /**
     * Perform launch activity for result actual.
     */
    private void launchActivityForResultActual(final Activity activity,
                                               final Intent intent,
                                               final int requestCode,
                                               final Bundle activityOptions,
                                               final Response response,
                                               final DispatchCallback callback) {
        // Observer activity onActivityResult LambdaCallback.
        final RouterCallbackFragment callbackFragment = RouterCallbackFragment.getInstance(activity);
        callbackFragment.setCallback(new RouterCallbackFragment.Callback() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                // Activity data is injected.
                response.setActivityResult(requestCode, resultCode, data);
                callback.onSuccess(response);
            }
        });
        // Launch activity with Activity options.
        callbackFragment.startActivityForResult(intent, requestCode, activityOptions);
    }

    /**
     * Perform launch activity actual.
     */
    private void launchActivityActual(Context context,
                                      Intent intent,
                                      Bundle activityOptions,
                                      Response response,
                                      DispatchCallback callback) {
        context.startActivity(intent, activityOptions);
        callback.onSuccess(response);
    }

}
