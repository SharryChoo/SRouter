package com.sharry.sroutersupport.templates;

import com.sharry.sroutersupport.data.RouteInterceptorMeta;

import java.util.Map;

/**
 * 路由拦截器 生成类模板接口
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/13
 */
public interface IRouteInterceptor {

    void loadInto(Map<String, RouteInterceptorMeta> interceptors);

}
