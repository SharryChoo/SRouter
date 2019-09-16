package com.sharry.srouter.support;

/**
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 2/22/2019 10:50 AM
 */
class RouteUninitializedException extends RuntimeException {

    private static final String EXCEPTION_MSG = "SRoute uninitialized. Please invoke SRoute.attach(...) " +
            "first before invoke this method.";

    RouteUninitializedException() {
        super(EXCEPTION_MSG);
    }

}
