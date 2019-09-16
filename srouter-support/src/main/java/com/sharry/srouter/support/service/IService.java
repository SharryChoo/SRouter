package com.sharry.srouter.support.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sharry.srouter.support.interceptors.ChainContext;

/**
 * The service use to connect modules.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public interface IService {

    /**
     * Attach ChainContext when IService instantiation.
     *
     * @param chainContext context for Request chain.
     */
    void attach(@NonNull ChainContext chainContext);

    /**
     * Connect service sync.
     *
     * @return data from service provider.
     */
    @Nullable
    Object connect();

    /**
     * Connect service async.
     *
     * @param callback the callback invoking when service connect succeed.
     */
    void connectAsync(@Nullable Callback callback);

    interface Callback {

        /**
         * invoking when service connect succeed.
         *
         * @param data data from service provider.
         */
        void onConnected(@Nullable Object data);

    }

}
