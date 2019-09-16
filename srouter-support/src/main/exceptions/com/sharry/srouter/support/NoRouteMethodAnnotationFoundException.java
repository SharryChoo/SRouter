package com.sharry.srouter.support;

/**
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/19 21:22
 */
class NoRouteMethodAnnotationFoundException extends RuntimeException {

    NoRouteMethodAnnotationFoundException(String message) {
        super(message);
    }

}
