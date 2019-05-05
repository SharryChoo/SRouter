package com.sharry.srouter.support.adapter;

import com.sharry.srouter.support.data.Response;

/**
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-05-05
 */
public interface ResponseAdapter<R> {

    R adpter(Response source);

}
