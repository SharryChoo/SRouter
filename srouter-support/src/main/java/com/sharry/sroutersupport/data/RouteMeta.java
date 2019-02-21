package com.sharry.sroutersupport.data;

import com.sharry.srouterannotation.ThreadMode;
import com.sharry.sroutersupport.providers.IProvider;

/**
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public class RouteMeta {

    /**
     * The types for Route target.
     */
    public enum Type {
        /**
         * Type of {@link android.app.Activity}.
         */
        ACTIVITY,

        /**
         * Type of {@link android.app.Fragment}.
         */
        FRAGMENT,

        /**
         * Type of {@link android.support.v4.app.Fragment}.
         */
        FRAGMENT_V4,

        /**
         * Type of {@link android.app.Service}.
         */
        SERVICE,

        /**
         * Type of {@link android.content.ContentProvider}.
         */
        CONTENT_PROVIDER,

        /**
         * Type of {@link android.content.BroadcastReceiver}.
         */
        BROADCAST,

        /**
         * Type of {@link IProvider}.
         */
        PROVIDER,

        /**
         * Type unsupported.
         */
        UNKNOWN
    }

    /**
     * Get an instance of RouteMeta
     */
    public static RouteMeta create(Type type, ThreadMode mode, Class<?> routeCls, String[] interceptors) {
        RouteMeta result = new RouteMeta();
        result.type = type;
        result.threadMode = mode;
        result.routeClass = routeCls;
        result.interceptors = interceptors;
        return result;
    }

    /**
     * Navigation route type.
     */
    private Type type;

    /**
     * Navigation route target class.
     */
    private Class<?> routeClass;

    /**
     * Navigation route thread mode.
     */
    private ThreadMode threadMode;
    /**
     * Navigation interceptorPaths.
     */
    private String[] interceptors;

    RouteMeta() {
    }

    public Type getType() {
        return type;
    }

    public Class<?> getRouteClass() {
        return routeClass;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public String[] getInterceptors() {
        return interceptors;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setRouteClass(Class<?> routeClass) {
        this.routeClass = routeClass;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public void setInterceptors(String[] interceptors) {
        this.interceptors = interceptors;
    }
}
