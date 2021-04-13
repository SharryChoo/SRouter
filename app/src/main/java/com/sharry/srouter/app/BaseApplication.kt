package com.sharry.srouter.app

import android.app.Application
import com.sharry.srouter.BuildConfig
import com.sharry.srouter.module.base.LoggerGlobalInterceptor
import com.sharry.srouter.module.base.RxJavaAdapter
import com.sharry.srouter.support.SRouter

/**
 * @author Sharry [Contact me.](SharryChooCHN@Gmail.com)
 * @version 1.0
 * @since 2018/8/15
 */
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SRouter.init(this)
        // 是否开启调试日志打印
        SRouter.isDebug(BuildConfig.DEBUG)
        // 注册模块, 可以通过 AMS 直接注册
//        SRouter.registerModules(
//                ModuleConstants.Login.NAME,
//                ModuleConstants.Found.NAME,
//                ModuleConstants.Personal.NAME
//        );
        // 添加回调适配器
        SRouter.addCallAdapter(RxJavaAdapter())
        // 添加全局拦截器
        SRouter.addGlobalInterceptor(LoggerGlobalInterceptor())
        // 通过 URI 添加拦截器
        SRouter.addGlobalInterceptorUri("XXX")
    }

}