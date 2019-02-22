package com.sharry.sroutersupport.facade;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.sharry.sroutersupport.data.Request;
import com.sharry.sroutersupport.data.Response;
import com.sharry.sroutersupport.exceptions.RouteUninitializedException;
import com.sharry.sroutersupport.utils.Logger;

/**
 * Route facade.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public class SRouter {

    private static volatile SRouter sInstance = null;
    private static boolean sHasInit = false;

    /**
     * Get instance of router. F
     * All function U use, will be start here.
     *
     * @return Route singleton
     */
    public static SRouter getInstance() {
        if (null == sInstance) {
            synchronized (SRouter.class) {
                if (null == sInstance) {
                    sInstance = new SRouter();
                }
            }
        }
        return sInstance;
    }

    /**
     * Initialize, it must be invoke before used router.
     */
    public static synchronized void init(Application application) {
        if (!sHasInit) {
            sHasInit = SRouterImpl.init(application);
        } else {
            Logger.i("Route already has been initialized.");
        }
    }

    /**
     * Build router navigation path.
     */
    public Request build(String path) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        return SRouterImpl.getInstance().build(path);
    }

    /**
     * perform navigation.
     */
    public Response navigation(Context context, @NonNull Request request) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        return SRouterImpl.getInstance().navigation(context, request);
    }
}
