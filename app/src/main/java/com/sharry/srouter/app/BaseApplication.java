package com.sharry.srouter.app;

import android.app.Application;

import com.sharry.srouter.module.base.ModuleConstants;
import com.sharry.srouter.support.facade.SRouter;

/**
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/15
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SRouter.init(this);
        SRouter.registerComponents(
                ModuleConstants.App.NAME,
                ModuleConstants.Found.NAME,
                ModuleConstants.Personal.NAME
        );
    }

}
