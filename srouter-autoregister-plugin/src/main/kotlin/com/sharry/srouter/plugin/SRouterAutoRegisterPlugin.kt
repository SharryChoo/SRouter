package com.sharry.srouter.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.sharry.srouter.plugin.core.RegisterTransform
import com.sharry.srouter.plugin.util.Logger
import com.sharry.srouter.plugin.util.ScanSetting
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 自动注册插件的 Plugin
 *
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 4/14/21
 */
class SRouterAutoRegisterPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val isApp = project.plugins.hasPlugin(AppPlugin::class.java)
        // only application module needs this com.sharry.srouter.plugin to generate register code
        if (isApp) {
            Logger.i("Project enable arouter-register com.sharry.srouter.plugin")
            // init srouter-auto-register settings
            val scanSettingList = ArrayList<ScanSetting>(2).apply {
                add(ScanSetting("IRoute"))
                add(ScanSetting("IRouteInterceptor"))
            }
            // register this com.sharry.srouter.plugin
            val android = project.extensions.getByType(AppExtension::class.java)
            android.registerTransform(
                    RegisterTransform(project, scanSettingList)
            )
        }
    }

}