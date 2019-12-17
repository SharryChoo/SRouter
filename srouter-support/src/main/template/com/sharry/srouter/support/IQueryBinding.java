package com.sharry.srouter.support;

import android.os.Bundle;

/**
 * 路由 query 注入模板定义
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public interface IQueryBinding<T> {

    void bind(T target, Bundle data);

}
