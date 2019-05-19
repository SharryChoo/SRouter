package com.sharry.srouter.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.sharry.srouter.R
import com.sharry.srouter.module.base.ModuleConstants
import com.sharry.srouter.support.facade.SRouter
import kotlinx.android.synthetic.main.app_activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_main)
        initView()
    }

    private fun initView() {
        // 尝试跳转到发现页面
        btnFound.setOnClickListener {
            val url = "router://found/found_activity?opr=15533&password=1234456"
            SRouter.request(url)
                    .setDelay(1000)
                    .navigation(this)
        }
        // 尝试跳转到个人中心
        btnPersonal.setOnClickListener {
            SRouter.request(ModuleConstants.Personal.NAME, ModuleConstants.Personal.PERSONAL_ACTIVITY)
                    .setActivityOptions(ActivityOptionsCompat.makeBasic())
                    .navigation(this)
        }
    }

}
