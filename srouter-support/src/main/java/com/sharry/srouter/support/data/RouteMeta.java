package com.sharry.srouter.support.data;

import androidx.fragment.app.Fragment;

import com.sharry.srouter.annotation.Route;
import com.sharry.srouter.annotation.ThreadMode;
import com.sharry.srouter.support.providers.IProvider;

/**
 * Save data associated with {@link Route} marked class.
 *
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
         * Type of {@link Fragment}.
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
    public static RouteMeta create(Type type, ThreadMode mode, Class<?> routeCls, String[] interceptorURIs) {
        RouteMeta result = new RouteMeta();
        result.type = type;
        result.threadMode = mode;
        result.routeClass = routeCls;
        result.routeInterceptors = interceptorURIs;
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
     * Navigation interceptorURIs.
     */
    private String[] routeInterceptors;

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

    public String[] getRouteInterceptors() {
        return routeInterceptors;
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

    public void setRouteInterceptors(String[] routeInterceptors) {
        this.routeInterceptors = routeInterceptors;
    }
}
