package com.sharry.srouter.app

import android.app.Activity.RESULT_OK
import com.sharry.srouter.annotation.RouteInterceptor
import com.sharry.srouter.module.base.ModuleConstants
import com.sharry.srouter.support.data.ActivityOptions
import com.sharry.srouter.support.facade.SRouter
import com.sharry.srouter.support.interceptors.IInterceptor

/**
 * @author Sharry [Contact me.](SharryChooCHN@Gmail.com)
 * @version 1.0
 * @since 2019/2/19 20:15
 */
@RouteInterceptor(
        value = ModuleConstants.App.LOGIN_INTERCEPTOR,
        priority = 10
)
class LoginInterceptor : IInterceptor {

    override fun process(chain: IInterceptor.Chain) {
        // 若没有登录, 则先跳转到登录页面
        if (!ModuleConstants.App.isLogin) {
            // 跳转到登录页面
            SRouter.request(ModuleConstants.App.NAME, ModuleConstants.App.LOGIN_ACTIVITY)
                    // 构建 Activity 相关配置
                    .setActivityOptions(
                            ActivityOptions.Builder().setRequestCode(100).build()
                    )
                    .addInterceptorURI(ModuleConstants.Personal.PERMISSION_INTERCEPTOR)
                    .navigation(chain.chainContext()) {
                        if (it.activityResult.requestCode == RESULT_OK) {
                            SRouter.navigation(chain.chainContext(), chain.chainContext().request)
                        }
                    }
            return
        }
        // 若已经登录, 则正常分发
        chain.dispatch()
    }

}
