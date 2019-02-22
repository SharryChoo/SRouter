package com.sharry.component2;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.sharry.libbase.AppConstants;
import com.sharry.srouterannotation.RouteInterceptor;
import com.sharry.sroutersupport.data.ActivityConfigs;
import com.sharry.sroutersupport.data.Response;
import com.sharry.sroutersupport.facade.SRouter;
import com.sharry.sroutersupport.interceptors.IInterceptor;

import static android.app.Activity.RESULT_OK;

/**
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019/2/19 20:15
 */
@RouteInterceptor(
        path = "component2/LoginInterceptor",
        priority = 9
)
public class LoginInterceptor implements IInterceptor {

    @Override
    public Response process(final Chain chain) {
        // 若没有登录, 则先跳转到登录页面
        if (!AppConstants.isLogin) {
            // 构建 Activity 的配置
            ActivityConfigs configs = new ActivityConfigs.Builder()
                    .setRequestCode(100)
                    .setActivityCallback(new ActivityConfigs.Callback() {
                        @Override
                        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                            if (resultCode == RESULT_OK) {
                                // 重新执行当前的导航请求
                                SRouter.getInstance().navigation(chain.context(), chain.request());
                            }
                        }
                    })
                    .build();
            // 跳转到登录页面
            SRouter.getInstance()
                    .build("app/LoginActivity")
                    .setActivityConfigs(configs)
                    .navigation(chain.context());
            return null;
        }
        return chain.dispatch();
    }

}
