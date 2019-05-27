package com.sharry.srouter.support.facade;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sharry.srouter.support.call.ICall;
import com.sharry.srouter.support.call.ICallAdapter;
import com.sharry.srouter.support.data.Request;
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

    private SRouter() {
    }

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
    public static void registerModules(@NonNull String... names) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        Preconditions.checkNotNull(names);
        SRouterImpl.registerModules(names);
    }

    /**
     * Register modules
     *
     * @param names the module name must be consistent of U setup module name.
     */
    public static void unregisterModules(@NonNull String... names) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        Preconditions.checkNotNull(names);
        SRouterImpl.unregisterModules(names);
    }

    /**
     * Add ICall adapter.
     */
    public static void addCallAdapter(@NonNull ICallAdapter adapter) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        Preconditions.checkNotNull(adapter);
        SRouterImpl.addCallAdapter(adapter);
    }

    /**
     * Parse intent and inject to fields that @Query marked.
     *
     * @param binder The intent holder.
     */
    public static <T> void bindQuery(@NonNull T binder) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        Preconditions.checkNotNull(binder);
        SRouterImpl.bindQuery(binder);
    }

    /**
     * Build router navigation path.
     */
    public static Request request(@NonNull String authority, @NonNull String path) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        Preconditions.checkNotEmpty(authority);
        Preconditions.checkNotEmpty(path);
        return SRouterImpl.request(authority, path);
    }

    /**
     * Build router navigation path.
     */
    public static Request request(@NonNull String url) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        Preconditions.checkNotEmpty(url);
        return SRouterImpl.request(url);
    }

    /**
     * perform navigation.
     */
    public static void navigation(@Nullable Context context, @NonNull Request request) {
        navigation(context, request, null);
    }

    public static void navigation(@Nullable Context context, @NonNull Request request, @Nullable Callback callback) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        Preconditions.checkNotNull(request);
        SRouterImpl.navigation(context, request, callback);
    }

    /**
     * Build an instance of navigation post.
     */
    public static ICall newCall(@Nullable Context context, @NonNull Request request) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        Preconditions.checkNotNull(request);
        return SRouterImpl.newCall(context, request);
    }

}
