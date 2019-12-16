package com.sharry.srouter.support;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.FLAG_NO_CREATE;
import static android.app.PendingIntent.FLAG_ONE_SHOT;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

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
    public static <T> T createApi(Class<T> templateClass) {
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
     * <p>
     * the url query value will inject to Bundle.
     * the Bundle mapping special key is {@link Constants#INTENT_EXTRA_URL_DATUM}
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
    @NonNull
    public static ICall newCall(@Nullable Context context, @NonNull Request request) {
        if (!sHasInit) {
            throw new RouteUninitializedException();
        }
        Preconditions.checkNotNull(request);
        return SRouterImpl.newCall(context, request);
    }

    @IntDef(flag = true,
            value = {
                    FLAG_ONE_SHOT,
                    FLAG_NO_CREATE,
                    FLAG_CANCEL_CURRENT,
                    FLAG_UPDATE_CURRENT,
                    FLAG_IMMUTABLE,

                    Intent.FILL_IN_ACTION,
                    Intent.FILL_IN_DATA,
                    Intent.FILL_IN_CATEGORIES,
                    Intent.FILL_IN_COMPONENT,
                    Intent.FILL_IN_PACKAGE,
                    Intent.FILL_IN_SOURCE_BOUNDS,
                    Intent.FILL_IN_SELECTOR,
                    Intent.FILL_IN_CLIP_DATA
            })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    @NonNull
    public static PendingIntent newPendingIntent(@Flags int flag, @NonNull PendingRunnable pendingRunnable) {
        Preconditions.checkNotNull(pendingRunnable);
        return SRouterImpl.newPendingIntent(flag, pendingRunnable);
    }

    /**
     * @param debug if true open logger, false will close.
     */
    public static void isDebug(boolean debug) {
        SRouterImpl.isDebug(debug);
    }
}
