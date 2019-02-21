package com.sharry.srouterannotation;

/**
 * description: thanks EventBus
 * author: Darren on 2018/1/23 08:57
 * email: 240336124@qq.com
 * version: 1.0
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
     * Get value for the enum element.
     */
    public int value() {
        return value;
    }

}
