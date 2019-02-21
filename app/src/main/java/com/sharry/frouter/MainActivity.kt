package com.sharry.frouter

import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import com.sharry.sroutersupport.facade.SRouter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        // 验证 Activity 跳转
        tvJumpToOne.setOnClickListener {
            SRouter.getInstance()
                    .build("component1/Component1Activity")
                    .setActivityOptions(ActivityOptionsCompat.makeScaleUpAnimation(tvJumpToOne,
                            tvJumpToOne.x.toInt(), tvJumpToOne.y.toInt(), tvJumpToOne.width, tvJumpToOne.height))
                    .withString("extra_string", "")
                    .navigation(this)
        }
        // 验证 Fragment 的获取
        tvInflateFragment.setOnClickListener {
            val response = SRouter.getInstance()
                    .build("component2/Component2Fragment")
                    .withString("extra_string", "")
                    .navigation()
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragmentContainer, response.fragmentV4)
            ft.commit()
        }
    }

}
