package com.sharry.sroutersupport.exceptions;

/**
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 2/22/2019 10:50 AM
 */
public class RouteUninitializedException extends RuntimeException {

    private static final String EXCEPTION_MSG = "SRoute uninitialized. Please invoke SRoute.getInstance.init(...) " +
            "first before invoke this method.";

    public RouteUninitializedException() {
        super(EXCEPTION_MSG);
    }

}
