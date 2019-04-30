package com.sharry.srouter.app

import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.appcompat.app.AppCompatActivity
import com.sharry.srouter.R
import com.sharry.srouter.support.data.ActivityConfigs
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
            SRouter.getInstance()
                    .build("component1/FoundActivity")
                    .navigation(this)
        }
        // 尝试跳转到个人中心
        btnPersonal.setOnClickListener {
            SRouter.getInstance()
                    .build("component2/PersonalActivity")
                    .setActivityConfigs(
                            ActivityConfigs.Builder()
                                    .setActivityOptions(ActivityOptionsCompat.makeClipRevealAnimation(
                                            it, it.x.toInt(), it.y.toInt(), it.width, it.height))
                                    .build()
                    )
                    .navigation(this)
        }
    }

}
