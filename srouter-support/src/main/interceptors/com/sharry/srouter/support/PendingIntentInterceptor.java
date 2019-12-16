package com.sharry.srouter.support;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

/**
 * The final navigation interceptor.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2/20/2019 9:29 AM
 */
class PendingIntentInterceptor implements IInterceptor {

    @Override
    public void intercept(@NonNull final Chain chain) {
        ChainContext context = chain.chainContext();
        Request request = context.request;
        Response response = new Response(request);
        RouteMeta meta = request.getRouteMeta();
        assert meta != null;
        switch (meta.getType()) {
            case ACTIVITY:
                Intent intent = new Intent(context, meta.getRouteClass());
                // Inject user extra info to intent.
                intent.putExtras(request.getDatum());
                // build PendingIntent and callback to outside.
                getActivityPendingIntent(context, intent, request, response, chain.callback());
                break;
            case FRAGMENT:
            case FRAGMENT_X:
            case FRAGMENT_V4:
            case SERVICE:
            default:
                // nothing.
                chain.callback().onFailed(new UnsupportedOperationException());
                break;
        }
    }

    private void getActivityPendingIntent(Context context, Intent intent, Request request,
                                          Response response, ChainCallback callback) {
        // Inject flags.
        if (Request.NON_FLAGS != request.getActivityFlags()) {
            intent.setFlags(request.getActivityFlags());
        }
        // perform launch activity.
        Activity activity = Utils.findActivity(context);
        if (Utils.isIllegalState(activity)) {
            // Activity at illegal state, use application context do jump.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivityPendingIntentActual(
                    SRouterImpl.sAppContext,
                    intent,
                    request.getActivityOptions(),
                    request.getPendingIntentFlags(),
                    response,
                    callback
            );
        } else {
            getActivityPendingIntentActual(
                    activity,
                    intent,
                    request.getActivityOptions(),
                    request.getPendingIntentFlags(),
                    response,
                    callback
            );
        }
    }

    /**
     * Perform fetch PendingIntent actual.
     */
    private void getActivityPendingIntentActual(Context context,
                                                Intent intent,
                                                Bundle activityOptions,
                                                int flags,
                                                Response response,
                                                ChainCallback callback) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, flags, activityOptions);
        context.startActivity(intent, activityOptions);
        response.setPendingIntent(pendingIntent);
        callback.onSuccess(response);
    }

}
