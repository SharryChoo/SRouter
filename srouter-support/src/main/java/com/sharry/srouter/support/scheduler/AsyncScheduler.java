package com.sharry.srouter.support.scheduler;

import androidx.annotation.NonNull;

import com.sharry.srouter.support.utils.Logger;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-05-05
 */
public class AsyncScheduler extends ScheduledThreadPoolExecutor implements IScheduler {

    public static AsyncScheduler getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private AsyncScheduler(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                Logger.e("Task rejected, too many task!");
            }
        });
    }

    /**
     * Handle exceptions when thread has completed.
     *
     * @param r the runnable that has completed
     * @param t the exception that caused termination, or null if
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?>) {
            try {
                ((Future<?>) r).get();
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // ignore/reset
            }
        }
        if (t != null) {
            Logger.w("Running task appeared exception! Thread [" +
                    Thread.currentThread().getName() + "], because [" + t.getMessage() + "]\n" +
                    t.getMessage());
        }
    }

    @Override
    public void schedule(Runnable runnable, long delay) {
        schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    private static class InstanceHolder {

        private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
        private static final int INIT_THREAD_COUNT = CPU_COUNT + 1;

        private static AsyncScheduler INSTANCE = new AsyncScheduler(
                INIT_THREAD_COUNT,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(@NonNull Runnable r) {
                        Thread thread = new Thread(r, AsyncScheduler.class.getSimpleName());
                        thread.setDaemon(false);
                        return thread;
                    }
                }
        );

    }

}
