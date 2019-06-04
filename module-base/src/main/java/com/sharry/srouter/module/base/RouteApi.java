package com.sharry.srouter.module.base;

import android.content.Context;

import com.sharry.srouter.annotation.QueryParam;
import com.sharry.srouter.annotation.RouteMethod;

/**
 * @author Sharry <a href="xiaoyu.zhu@1hai.cn">Contact me.</a>
 * @version 1.0
 * @since 2019-06-04 11:12
 */
public interface RouteApi {

    @RouteMethod(
            authority = ModuleConstants.Found.NAME,
            path = ModuleConstants.Found.FOUND_FRAGMENT
    )
    ResponseObservable foundFragment(
            @QueryParam(key = "title") int title,
            @QueryParam(key = "content") String content
    );

    /**
     * @param context 若无 context 参数, 会使用 application context 跳转
     */
    @RouteMethod(
            authority = ModuleConstants.Personal.NAME,
            path = ModuleConstants.Personal.PERSONAL_ACTIVITY,
            interceptorURIs = ModuleConstants.App.LOGIN_INTERCEPTOR,
            desc = "跳转到个人中心页面"
    )
    ResponseObservable personalCenter(Context context);

}
