package com.sharry.srouter.support.utils;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sharry.srouter.annotation.QueryParam;
import com.sharry.srouter.annotation.RouteMethod;
import com.sharry.srouter.support.data.Request;
import com.sharry.srouter.support.exceptions.NoRouteMethodAnnotationFoundException;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 2019-06-04 13:41
 */
public class RouterApiUtil {

    private static final Map<String, Request> CACHE = new HashMap<>();

    /**
     * Parse method annotation and inject to Request.
     *
     * @param method the method need parse
     * @param args   the method parameters.
     * @return injected request.
     */
    public static Request parseMethod(@NonNull Method method, @Nullable Object[] args) {
        Preconditions.checkNotNull(method);
        // Get request from cache.
        Request request = CACHE.get(method.getName());
        // Create request and add to cache.
        if (request == null) {
            // Build request.
            RouteMethod routeMethod = null;
            Annotation[] annotations = method.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof RouteMethod) {
                    routeMethod = (RouteMethod) annotation;
                }
            }
            if (routeMethod == null) {
                throw new NoRouteMethodAnnotationFoundException("Cannot find @RouteMethod annotation on "
                        + method.getName());
            }
            request = Request.create(routeMethod.authority(), routeMethod.path());
            request.addInterceptorURIs(routeMethod.interceptorURIs());
            // Update cache.
            CACHE.put(method.getName(), request);
        }
        // Parse parameter annotation
        Bundle bundle = parseParameter(method, args);
        request.setDatum(bundle);
        return request;
    }

    @NonNull
    private static Bundle parseParameter(Method method, Object[] args) {
        Bundle bundle = new Bundle();
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            Annotation[] paramAnnotations = annotations[i];
            QueryParam queryParam = null;
            for (Annotation annotation : paramAnnotations) {
                if (annotation instanceof QueryParam) {
                    queryParam = (QueryParam) annotation;
                }
            }
            if (queryParam != null) {
                completionBundle(bundle, queryParam, args[i]);
            }
        }
        return bundle;
    }

    private static void completionBundle(Bundle bundle,
                                         QueryParam queryParam,
                                         Object value) {
        if (value instanceof Boolean) {
            bundle.putBoolean(queryParam.key(), (Boolean) value);
        } else if (value instanceof Byte) {
            bundle.putByte(queryParam.key(), (Byte) value);
        } else if (value instanceof Short) {
            bundle.putShort(queryParam.key(), (Short) value);
        } else if (value instanceof Integer) {
            bundle.putInt(queryParam.key(), (Integer) value);
        } else if (value instanceof Long) {
            bundle.putLong(queryParam.key(), (Long) value);
        } else if (value instanceof Character) {
            bundle.putChar(queryParam.key(), (Character) value);
        } else if (value instanceof Double) {
            bundle.putDouble(queryParam.key(), (Double) value);
        } else if (value instanceof String) {
            bundle.putString(queryParam.key(), (String) value);
        } else if (value instanceof Serializable) {
            bundle.putSerializable(queryParam.key(), (Serializable) value);
        } else if (value instanceof Parcelable) {
            bundle.putParcelable(queryParam.key(), (Parcelable) value);
        } else {
            // waiting support more.
        }
    }
}
