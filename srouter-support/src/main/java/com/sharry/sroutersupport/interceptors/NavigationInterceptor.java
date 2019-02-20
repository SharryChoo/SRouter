package com.sharry.sroutersupport.interceptors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.sharry.sroutersupport.data.Request;
import com.sharry.sroutersupport.data.Result;
import com.sharry.sroutersupport.frame.Logger;
import com.sharry.sroutersupport.template.IInterceptor;
import com.sharry.sroutersupport.template.IProvider;

/**
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 2/20/2019 9:29 AM
 */
public class NavigationInterceptor implements IInterceptor {

    @Override
    public Result process(Chain chain) {
        return navigationActual(chain.context(), chain.request());
    }

    /**
     * Actual perform navigation.
     */
    private Result navigationActual(@NonNull Context context, Request request) {
        Result result = new Result();
        switch (request.getType()) {
            case ACTIVITY:
                Intent intent = new Intent(context, request.getRouteClass());
                // Inject user extra info to intent.
                intent.putExtras(request.getBundle());
                // Inject user config flags to intent.
                if (-1 != request.getFlags()) {
                    intent.setFlags(request.getFlags());
                } else if (!(context instanceof Activity)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                // Real perform activity launch.
                performStartActivity(context, intent, request.getRequestCode());
                break;
            case SERVICE:
                intent = new Intent(context, request.getRouteClass());
                context.startActivity(intent);
                break;
            case FRAGMENT:
                try {
                    // Instantiation fragment by class name.
                    android.app.Fragment fragment = (android.app.Fragment) request.getRouteClass().newInstance();
                    fragment.setArguments(request.getBundle());
                    // Inject fragment to request provider.
                    result.setFragment(fragment);
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
                    fragmentV4.setArguments(request.getBundle());
                    // Inject fragment to request provider.
                    result.setFragmentV4(fragmentV4);
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
                    result.setProvider(provider);
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
        return result;
    }

    /**
     * Perform launch target activity.
     */
    private void performStartActivity(Context context, Intent intent, int requestCode) {
        if (requestCode != Request.NON_REQUEST_CODE && context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            context.startActivity(intent);
        }
    }

}
