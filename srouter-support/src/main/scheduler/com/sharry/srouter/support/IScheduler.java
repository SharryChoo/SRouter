package com.sharry.srouter.support;

/**
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-05-05
 */
interface IScheduler {

    void schedule(Runnable runnable);

    void schedule(Runnable runnable, long delay);

}
