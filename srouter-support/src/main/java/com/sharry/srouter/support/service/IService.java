package com.sharry.srouter.support.service;

import androidx.annotation.NonNull;

import com.sharry.srouter.support.interceptors.ChainContext;

/**
 * The service use to connect modules.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public interface IService {

    void init(@NonNull ChainContext chainContext);

    void doConnect();

}
