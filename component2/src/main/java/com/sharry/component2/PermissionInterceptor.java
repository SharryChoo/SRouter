package com.sharry.component2;

import android.util.Log;

import com.sharry.srouterannotation.RouteInterceptor;
import com.sharry.sroutersupport.data.Response;
import com.sharry.sroutersupport.interceptors.IInterceptor;

/**
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019/2/19 20:15
 */
@RouteInterceptor(
        path = "component2/PermissionInterceptor",
        priority = 2
)
public class PermissionInterceptor implements IInterceptor {

    @Override
    public Response process(Chain chain) {
        Log.e("TAG", "权限获取成功");
        return chain.dispatch();
    }

}
