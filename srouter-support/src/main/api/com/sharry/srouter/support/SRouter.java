package com.sharry.srouter.support;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
     * @param debug if true open logger, false will close.
     */
    public static void isDebug(boolean debug) {
        SRouterImpl.isDebug(debug);
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
     * Add interceptor used in global.
     */
    public static void addGlobalInterceptor(@NonNull IInterceptor interceptor) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        Preconditions.checkNotNull(interceptor);
        SRouterImpl.addGlobalInterceptor(interceptor);
    }

    /**
     * Add interceptor used in global.
     */
    public static void addGlobalInterceptorUri(@NonNull String interceptorUri) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        Preconditions.checkNotEmpty(interceptorUri);
        SRouterImpl.addGlobalInterceptorUri(interceptorUri);
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
     * Create an instance of Router template interfaces.
     *
     * @param <T> the type of class.
     * @return an instance of Router template class.
     */
    public static <T> T createApi(@NonNull Class<T> templateClass) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        Preconditions.checkNotNull(templateClass);
        return SRouterImpl.createApi(templateClass);
    }

    /**
     * Parse intent and inject to fields that @Query marked.
     *
     * @param binder The intent holder.
     */
    public static <T> void bindQuery(@Nullable T binder, @Nullable Bundle args) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        if (binder == null || args == null) {
            return;
        }
        SRouterImpl.bindQuery(binder, args);
    }

    /**
     * Build router navigation path.
     */
    @NonNull
    public static Request request(@Nullable String authority, @Nullable String path) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        return SRouterImpl.request(authority, path);
    }

    /**
     * Build router navigation path.
     * <p>
     * the url query value will inject to Bundle.
     * the Bundle mapping special key is {@link Constants#INTENT_EXTRA_URL_DATUM}
     */
    @NonNull
    public static Request request(@Nullable String uri) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        return SRouterImpl.request(uri);
    }

    /**
     * Build router navigation path.
     */
    @NonNull
    public static Request request(@Nullable Intent forwardIntent) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        return SRouterImpl.request(forwardIntent);
    }

    /**
     * Create an instance of ForwardIntentBuilder
     */
    @NonNull
    public static ForwardIntentBuilder forwardIntentBuilder() {
        return SRouterImpl.forwardIntentBuilder();
    }

    /**
     * perform navigation.
     */
    public static void navigation(@Nullable Context context, @NonNull Request request) {
        navigation(context, request, null);
    }

    public static void navigation(@Nullable Context context, @NonNull Request request, @Nullable LambdaCallback success) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        Preconditions.checkNotNull(request);
        SRouterImpl.navigation(context, request, success);
    }

    /**
     * Build an instance of navigation post.
     */
    @NonNull
    public static ICall newCall(@Nullable Context context, @NonNull Request request) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        Preconditions.checkNotNull(request);
        return SRouterImpl.newCall(context, request);
    }

}
