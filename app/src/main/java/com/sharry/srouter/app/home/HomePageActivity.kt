package com.sharry.srouter.app.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sharry.srouter.R
import com.sharry.srouter.module.base.RouteApi
import com.sharry.srouter.support.facade.SRouter

class HomePageActivity : AppCompatActivity() {

    private lateinit var foundFragment: Fragment
    private val routeApi = SRouter.createApi(RouteApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_home_page)
        initView()
        initData()
    }

    private fun initView() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.show(foundFragment)
                    transaction.commitAllowingStateLoss()
                    true
                }
                R.id.navigation_personal -> {
                    // 通过模板方法跳转
                    val disposable = routeApi.personalCenter(
                            this,
                            11,
                            Intent.FLAG_ACTIVITY_NEW_TASK
                    ).subscribe()
                    false
                }
                else -> {
                    // do nothing.
                }
            }
            false
        }

    }

    private fun initData() {
        // 路由获取 Fragment 并展示
        SRouter.request("router://found/found_fragment?title=123.45&content=FoundContent")
                .navigation(this) {
                    val transaction = supportFragmentManager.beginTransaction()
                    foundFragment = it.getFragment()
                    transaction.add(R.id.fl_container, foundFragment)
                    transaction.commitAllowingStateLoss()
                }
    }

}
