package com.sharry.frouter;

import android.app.Application;

import com.sharry.sroutersupport.facade.SRouter;

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
    }

}
