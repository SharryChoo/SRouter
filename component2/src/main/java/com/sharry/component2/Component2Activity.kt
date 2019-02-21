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
        path = "component2/Component2Activity",
        mode = ThreadMode.MAIN,
        interceptorPaths = ["component2/PermissionInterceptor", "component2/LoginInterceptor"],
        desc = "第二个 Module 中的 Activity"
)
class Component2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_component_second)
    }

}
