package com.sharry.sroutersupport.thread;

import android.support.annotation.NonNull;

import com.sharry.sroutersupport.utils.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Default thread pool.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public class DefaultPoolExecutor extends ThreadPoolExecutor {

    //    Thread args
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int INIT_THREAD_COUNT = CPU_COUNT + 1;
    private static final int MAX_THREAD_COUNT = INIT_THREAD_COUNT;
    private static final long SURPLUS_THREAD_LIFE = 30L;

    private static DefaultPoolExecutor sInstance = null;

    static {
        sInstance = new DefaultPoolExecutor(
                INIT_THREAD_COUNT,
                MAX_THREAD_COUNT,
                SURPLUS_THREAD_LIFE,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(64),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(@NonNull Runnable r) {
                        Thread thread = new Thread(r, DefaultPoolExecutor.class.getSimpleName());
                        thread.setDaemon(false);
                        return thread;
                    }
                }
        );
    }

    public static DefaultPoolExecutor getInstance() {
        return sInstance;
    }

    private DefaultPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory,
                new RejectedExecutionHandler() {
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
}
