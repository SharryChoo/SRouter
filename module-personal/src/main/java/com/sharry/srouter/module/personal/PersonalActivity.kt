package com.sharry.srouter.module.personal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sharry.srouter.annotation.compiler.Query
import com.sharry.srouter.annotation.compiler.Route
import com.sharry.srouter.module.base.ModuleConstants
import com.sharry.srouter.support.SRouter
import kotlinx.android.synthetic.main.personal_activity_personal.*

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
        interceptorURIs = [ModuleConstants.Login.LOGIN_INTERCEPTOR],
        desc = "个人中心页面"
)
class PersonalActivity : AppCompatActivity() {

    @JvmField
    @Query(key = "content")
    var content: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personal_activity_personal)
        SRouter.bindQuery(this, intent.extras)
        tvPersonal.text = "${content} personal center"
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            SRouter.bindQuery(this, it.extras)
        }
    }

}
