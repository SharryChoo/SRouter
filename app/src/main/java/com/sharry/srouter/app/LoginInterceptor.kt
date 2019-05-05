package com.sharry.srouter.app

import android.app.Activity.RESULT_OK
import com.sharry.srouter.annotation.RouteInterceptor
import com.sharry.srouter.module.base.ModuleConstants
import com.sharry.srouter.support.data.ActivityConfigs
import com.sharry.srouter.support.data.Response
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

    override fun process(chain: IInterceptor.Chain): Response? {
        // 若没有登录, 则先跳转到登录页面
        if (!ModuleConstants.App.isLogin) {
            // 构建 Activity 的配置
            val configs = ActivityConfigs.Builder()
                    .setRequestCode(100)
                    .setActivityCallback { _, resultCode, _ ->
                        // 登录成功之后, 重新导航到目标页面
                        if (resultCode == RESULT_OK) {
                            SRouter.getInstance().navigation(chain.context(), chain.request())
                        }
                    }
                    .build()
            // 跳转到登录页面
            SRouter.getInstance()
                    .build(ModuleConstants.App.NAME, ModuleConstants.App.LOGIN_ACTIVITY)
                    .setActivityConfigs(configs)
                    .addInterceptorURI(ModuleConstants.Personal.PERMISSION_INTERCEPTOR)
                    .navigation(chain.context())
            return null
        }
        // 若已经登录, 则正常分发
        return chain.dispatch()
    }

}
