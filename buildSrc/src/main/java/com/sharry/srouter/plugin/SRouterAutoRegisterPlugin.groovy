package com.sharry.srouter.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.sharry.srouter.plugin.core.RegisterTransform
import com.sharry.srouter.plugin.util.ScanSetting
import com.sharry.srouter.plugin.util.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * SRouter 自动 register 依赖的插件
 *
 * <code>apply 'com.sharry.plugin'</code>
 */
class SRouterAutoRegisterPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def isApp = project.plugins.hasPlugin(AppPlugin)
        // only application module needs this plugin to generate register code
        if (isApp) {
            Logger.make(project)
            Logger.i('Project enable arouter-register plugin')
            def android = project.extensions.getByType(AppExtension)
            // init srouter-auto-register settings
            ArrayList<ScanSetting> list = new ArrayList<>(2)
            list.add(new ScanSetting('IRoute'))
            list.add(new ScanSetting('IRouteInterceptor'))
            // register this plugin
            def transformImpl = new RegisterTransform(project, list)
            android.registerTransform(transformImpl)
        }
    }

}