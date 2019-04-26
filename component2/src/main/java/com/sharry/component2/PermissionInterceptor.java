package com.sharry.component2;

import android.util.Log;

import com.sharry.srouter.annotation.RouteInterceptor;
import com.sharry.srouter.support.data.Response;
import com.sharry.srouter.support.interceptors.IInterceptor;

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
