package com.sharry.component2;

import android.util.Log;
import android.widget.Toast;

import com.sharry.srouterannotation.RouteInterceptor;
import com.sharry.sroutersupport.data.Result;
import com.sharry.sroutersupport.interceptors.IInterceptor;

/**
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019/2/19 20:15
 */
@RouteInterceptor(
        path = "component2/LoginInterceptor",
        priority = 10
)
public class LoginInterceptor implements IInterceptor {

    @Override
    public Result process(Chain chain) {
        Log.e("TAG", "LoginInterceptor");
        Toast.makeText(chain.context(), "请先登录.", Toast.LENGTH_SHORT).show();
        return null;
    }

}
