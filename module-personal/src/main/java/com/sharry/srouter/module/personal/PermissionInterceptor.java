package com.sharry.srouter.module.personal;

import android.util.Log;

import androidx.annotation.NonNull;

import com.sharry.srouter.annotation.compiler.RouteInterceptor;
import com.sharry.srouter.module.base.ModuleConstants;
import com.sharry.srouter.support.interceptors.IInterceptor;

/**
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019/2/19 20:15
 */
@RouteInterceptor(
        value = ModuleConstants.Personal.PERMISSION_INTERCEPTOR,
        priority = 2
)
public class PermissionInterceptor implements IInterceptor {

    @Override
    public void intercept(@NonNull Chain chain) {
        Log.e("TAG", "权限获取成功");
        chain.dispatch();
    }

}
