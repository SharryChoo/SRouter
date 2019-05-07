package com.sharry.srouter.support.data;

import androidx.fragment.app.Fragment;

import com.sharry.srouter.annotation.Route;
import com.sharry.srouter.support.service.IService;

/**
 * Save data associated with {@link Route} marked class.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public class RouteMeta {

    /**
     * Navigation route type.
     */
    private Type type;
    /**
     * Navigation route target class.
     */
    private Class<?> routeClass;
    /**
     * Navigation interceptorURIs.
     */
    private String[] routeInterceptorURIs;

    RouteMeta() {
    }

    /**
     * Get an instance of RouteMeta
     */
    public static RouteMeta create(Type type, Class<?> routeCls, String[] interceptorURIs) {
        RouteMeta result = new RouteMeta();
        result.type = type;
        result.routeClass = routeCls;
        result.routeInterceptorURIs = interceptorURIs;
        return result;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Class<?> getRouteClass() {
        return routeClass;
    }

    public void setRouteClass(Class<?> routeClass) {
        this.routeClass = routeClass;
    }

    public String[] getRouteInterceptorURIs() {
        return routeInterceptorURIs;
    }

    public void setRouteInterceptorURIs(String[] routeInterceptorURIs) {
        this.routeInterceptorURIs = routeInterceptorURIs;
    }

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
         * Type of {@link IService}.
         */
        SERVICE,

        /**
         * Type unsupported.
         */
        UNKNOWN
    }

}
