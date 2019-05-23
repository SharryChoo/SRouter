package com.sharry.srouter.annotation;

/**
 * The priority range of RouteInterceptor.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2/21/2019 7:45 PM
 */
public enum PriorityRange {

    /**
     * The minimum valid priority associated with the {@link RouteInterceptor}
     */
    MINIMUM(1),

    /**
     * The maximum valid priority associated with the {@link RouteInterceptor}
     */
    MAXIMUM(10);

    int value;

    PriorityRange(int value) {
        this.value = value;
    }

    /**
     * Get key for the enum element.
     */
    public int value() {
        return value;
    }

}
