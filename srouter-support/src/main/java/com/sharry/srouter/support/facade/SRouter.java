package com.sharry.srouter.support.facade;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sharry.srouter.support.data.Request;
import com.sharry.srouter.support.data.Response;
import com.sharry.srouter.support.exceptions.RouteUninitializedException;
import com.sharry.srouter.support.utils.Logger;
import com.sharry.srouter.support.utils.Preconditions;

/**
 * Route facade.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public class SRouter {

    private static boolean sHasInit = false;

    /**
     * Initialize, it must be invoke before used router.
     */
    public static synchronized void init(@NonNull Application application) {
        Preconditions.checkNotNull(application);
        if (!sHasInit) {
            sHasInit = SRouterImpl.init(application);
        } else {
            Logger.i("Route already has been initialized.");
        }
    }

    /**
     * Register modules
     *
     * @param names the module name must be consistent of U setup module name.
     */
    public static void registerComponents(@NonNull String... names) {
        Preconditions.checkNotNull(names);
        SRouterImpl.registerComponents(names);
    }

    /**
     * Register modules
     *
     * @param names the module name must be consistent of U setup module name.
     */
    public static void unregisterComponents(@NonNull String... names) {
        Preconditions.checkNotNull(names);
        SRouterImpl.unregisterComponents(names);
    }

    /**
     * Get instance of router. S
     * All function U use, will be start here.
     *
     * @return Route singleton
     */
    public static SRouter getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Build router navigation path.
     */
    public Request build(@NonNull String authority, @NonNull String path) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        Preconditions.checkNotEmpty(authority);
        Preconditions.checkNotEmpty(path);
        return SRouterImpl.getInstance().build(authority, path);
    }

    /**
     * perform navigation.
     */
    public void navigation(@Nullable Context context, @NonNull Request request) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        Preconditions.checkNotNull(request);
        SRouterImpl.getInstance().navigation(context, request);
    }

    private static final class InstanceHolder {
        private static final SRouter INSTANCE = new SRouter();
    }

}
