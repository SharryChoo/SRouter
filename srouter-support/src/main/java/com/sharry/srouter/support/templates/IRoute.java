package com.sharry.srouter.support.templates;

import com.sharry.srouter.support.data.RouteMeta;

import java.util.Map;

/**
 * 路由生成类模板接口
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public interface IRoute {

    void loadInto(Map<String, RouteMeta> routes);

}
