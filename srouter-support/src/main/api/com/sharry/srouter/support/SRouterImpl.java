package com.sharry.srouter.support;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static android.app.PendingIntent.FLAG_NO_CREATE;

/**
 * Route feature's implementor.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
class SRouterImpl {

    static Context sAppContext;

    static synchronized boolean init(Application application) {
        sAppContext = application;
        Logger.i("Route initialize success.");
        return true;
    }

    static void registerModules(String[] names) {
        LogisticsCenter.registerModules(names);
    }

    static void unregisterModules(String[] names) {
        LogisticsCenter.unregisterModules(names);
    }

    static void addGlobalInterceptor(IInterceptor interceptor) {
        DataSource.GLOBAL_INTERCEPTORS.add(interceptor);
    }

    static void addGlobalInterceptorUri(String interceptorUri) {
        DataSource.GLOBAL_INTERCEPTOR_URIS.add(interceptorUri);
    }

    static void addCallAdapter(ICallAdapter adapter) {
        LogisticsCenter.addCallAdapter(adapter);
    }

    static void isDebug(boolean debug) {
        Logger.isDebug(debug);
    }

    static <T> void bindQuery(T binder) {
        LogisticsCenter.bindQuery(binder);
    }

    static Request request(String authority, String path) {
        return Request.create(authority, path);
    }

    static Request request(String url) {
        return Request.parseFrom(url);
    }

    public static <T> T createApi(Class<T> templateClass) {
        return LogisticsCenter.createApi(templateClass);
    }

    static void navigation(final Context context, final Request request, final Callback callback) {
        newCall(context, request).post(new IInterceptor.ChainCallback() {
            @Override
            public void onSuccess(@NonNull Response response) {
                if (callback != null) {
                    callback.onSuccess(response);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                Logger.e(throwable.getMessage(), throwable);
            }

            @Override
            public void onCanceled() {
                Logger.e("Dispatch canceled.");
            }
        });
    }

    @NonNull
    static ICall newCall(final Context context, final Request request) {
        // 1. completion request.
        try {
            LogisticsCenter.completion(request);
        } catch (NoRouteFoundException e) {
            Logger.e(e.getMessage(), e);
            return ICall.DEFAULT;
        }
        // 2. completion interceptors.
        final List<IInterceptor> interceptors = new ArrayList<>();
        // 2.1 add user interceptors
        if (request.isGreenChannel()) {
            Logger.i("Request is green channel, ignore interceptors that not global.");
        } else {
            interceptors.addAll(request.getInterceptors());
            instantiateAndSortInterceptorUris(
                    Arrays.asList(request.getRouteMeta().getRouteInterceptorURIs()),
                    interceptors
            );
        }
        // 2.2 add global interceptors.
        instantiateAndSortInterceptorUris(DataSource.GLOBAL_INTERCEPTOR_URIS, interceptors);
        interceptors.addAll(DataSource.GLOBAL_INTERCEPTORS);
        // 2.3 Add finalize navigation Interceptor.
        interceptors.add(new NavigationInterceptor());
        // 3. Create call.
        return RealCall.create(null == context ? sAppContext : context, request, interceptors);
    }

    @NonNull
    static PendingIntent newPendingIntent(int flag, PendingRunnable pendingRunnable) {
        // build key for the pendingRunnable
        int key = DataSource.sNextPendingRunanbleKey.addAndGet(1);
        Log.e("TAG", "key = " + key);
        // add to cache
        DataSource.PENDING_RUNNABLES.put(key, pendingRunnable);
        // build intent for PendingIntent
        Intent intent = new Intent(sAppContext, PendingIntentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PendingIntentActivity.EXTRA_INTENT_KEY, key);
        return PendingIntent.getActivity(sAppContext, -1, intent, flag);
    }

    private static void instantiateAndSortInterceptorUris(List<String> sourceSet, List<IInterceptor> destSet) {
        // 3.1 Sort interceptor URIs by priority.
        final List<InterceptorMeta> orderedMetas = new LinkedList<>();
        for (String value : sourceSet) {
            InterceptorMeta meta = DataSource.TABLE_ROUTES_INTERCEPTORS.get(value);
            if (null == meta) {
                continue;
            }
            // Add meta data to comfortable position.
            int insertIndex = 0;
            for (InterceptorMeta orderedMeta : orderedMetas) {
                if (orderedMeta.getPriority() < meta.getPriority()) {
                    break;
                } else {
                    insertIndex++;
                }
            }
            orderedMetas.add(insertIndex, meta);
        }
        // 3.2 Put sorted metas to interceptors.
        for (InterceptorMeta meta : orderedMetas) {
            try {
                IInterceptor interceptor = (IInterceptor) meta.getInterceptorClass().newInstance();
                destSet.add(interceptor);
            } catch (IllegalAccessException e) {
                Logger.e(meta.getInterceptorClass().getName() + " cannot access, please ensure this class " +
                        "have public and no args Constructor", e);
            } catch (InstantiationException e) {
                Logger.e(meta.getInterceptorClass().getName() + " instantiation failed.", e);
            }
        }
    }
}
