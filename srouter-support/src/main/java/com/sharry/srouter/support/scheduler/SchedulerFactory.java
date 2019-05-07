package com.sharry.srouter.support.scheduler;

/**
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-05-05
 */
public class SchedulerFactory {

    public static IScheduler create(ThreadMode mode) {
        IScheduler result;
        switch (mode) {
            case ASYNC:
                result = AsyncScheduler.getInstance();
                break;
            case MAIN_THREAD:
            default:
                result = MainScheduler.getInstance();
                break;
        }
        return result;
    }

}
