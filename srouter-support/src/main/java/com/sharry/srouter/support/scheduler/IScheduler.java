package com.sharry.srouter.support.scheduler;

/**
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-05-05
 */
public interface IScheduler {

    void schedule(Runnable runnable, long delay);

}
