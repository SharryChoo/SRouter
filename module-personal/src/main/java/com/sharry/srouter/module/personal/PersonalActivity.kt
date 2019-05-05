package com.sharry.srouter.module.personal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sharry.srouter.annotation.Route
import com.sharry.srouter.annotation.ThreadMode
import com.sharry.srouter.module.base.ModuleConstants

/**
 * 第二个 Module 中的 Activity.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/22 20:14
 */
@Route(
        authority = ModuleConstants.Personal.NAME,
        path = ModuleConstants.Personal.PERSONAL_ACTIVITY,
        mode = ThreadMode.MAIN,
        interceptorURIs = [ModuleConstants.Personal.PERMISSION_INTERCEPTOR, ModuleConstants.App.LOGIN_INTERCEPTOR],
        desc = "个人中心页面"
)
class PersonalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component2_activity_personal)
    }

}
