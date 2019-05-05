package com.sharry.srouter.support.scheduler;

import com.sharry.srouter.annotation.ThreadMode;

/**
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-05-05
 */
public class SchedulerFactory {

    public static Scheduler create(ThreadMode mode) {
        Scheduler result;
        switch (mode) {
            case ASYNC:
                result = AsyncScheduler.getInstance();
                break;
            case MAIN:
            default:
                result = HandlerScheduler.getInstance();
                break;
        }
        return result;
    }

}
