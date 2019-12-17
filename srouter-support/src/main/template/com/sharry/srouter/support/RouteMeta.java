package com.sharry.srouter.support;

import androidx.annotation.NonNull;

import com.sharry.srouter.annotation.compiler.Route;

/**
 * Save data associated with {@link Route} marked class.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public final class RouteMeta {

    /**
     * Get an instance of RouteMeta
     */
    public static RouteMeta create(Type type, Class<?> routeCls, @NonNull String[] interceptorURIs) {
        return new RouteMeta(type, routeCls, interceptorURIs);
    }

    /**
     * Navigation route type.
     */
    private final Type type;
    /**
     * Navigation route target class.
     */
    private final Class<?> routeClass;
    /**
     * Navigation interceptorURIs.
     */
    @NonNull
    private final String[] routeInterceptorURIs;

    private RouteMeta(@NonNull Type type, @NonNull Class<?> routeClass, @NonNull String[] routeInterceptorURIs) {
        this.type = type;
        this.routeClass = routeClass;
        this.routeInterceptorURIs = routeInterceptorURIs;
    }

    public Type getType() {
        return type;
    }

    public Class<?> getRouteClass() {
        return routeClass;
    }

    @NonNull
    public String[] getRouteInterceptorURIs() {
        return routeInterceptorURIs;
    }

    /**
     * The types for Route target.
     */
    public enum Type {
        /**
         * Type of {@link android.app.Activity}.
         */
        ACTIVITY("android.app.Activity"),

        /**
         * Type of {@link android.app.Fragment}.
         */
        FRAGMENT("android.app.Fragment"),

        /**
         * Type of {@link android.support.v4.app.Fragment}.
         */
        FRAGMENT_V4("android.support.v4.app.Fragment"),

        /**
         * Type of {@link androidx.fragment.app.Fragmennt}.
         */
        FRAGMENT_X("androidx.fragment.app.Fragmennt"),

        /**
         * Type of {@link IService}.
         */
        SERVICE(IService.class.getName()),

        /**
         * Type unsupported.
         */
        UNKNOWN("");

        public String typeName;

        Type(String typeName) {
            this.typeName = typeName;
        }
    }

}
