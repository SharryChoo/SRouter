package com.sharry.srouter.annotation;

/**
 * Thanks EventBus
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2/21/2019 7:37 PM
 */
public enum ThreadMode {

    /**
     * Action will be called directly in the same thread, which is posting the event. This is the default. Event delivery
     * implies the least overhead because it avoids thread switching completely. Thus this is the recommended mode for
     * simple tasks that are known to complete in a very short time without requiring the main thread. Event handlers
     * using this mode must return quickly to avoid blocking the posting thread, which may be the main thread.
     */
    SYNC,

    /**
     * On Android, action will be called in Android's main thread (UI thread). If the posting thread is
     * the main thread, action methods will be called directly, blocking the posting thread. Otherwise the event
     * is queued for delivery (non-blocking). Action using this mode must return quickly to avoid blocking the main thread.
     * If not on Android, behaves the same as {@link #SYNC}.
     */
    MAIN,

    /**
     * Action will be called in a separate thread. This is always independent from the posting thread and the
     * main thread. Posting events never wait for action methods using this mode. Action methods should
     * use this mode if their execution might take some time, e.g. for network access. Avoid triggering a large number
     * of long running asynchronous action methods at the same time to limit the number of concurrent threads. EventBus
     * uses a thread pool to efficiently reuse threads from completed asynchronous action notifications.
     */
    ASYNC
}
