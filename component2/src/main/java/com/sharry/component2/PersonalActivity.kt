package com.sharry.component2

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sharry.srouterannotation.Route
import com.sharry.srouterannotation.ThreadMode

/**
 * 第二个 Module 中的 Activity.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/22 20:14
 */
@Route(
        path = "component2/PersonalActivity",
        mode = ThreadMode.MAIN,
        interceptorPaths = ["component2/PermissionInterceptor", "app/LoginInterceptor"],
        desc = "个人中心页面"
)
class PersonalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component2_activity_personal)
    }

}
