package com.sharry.srouter.app.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sharry.srouter.R
import com.sharry.srouter.module.base.ModuleConstants
import com.sharry.srouter.support.facade.SRouter

class HomePageActivity : AppCompatActivity() {

    private lateinit var foundFragment: Fragment

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
                    SRouter.request(ModuleConstants.Personal.NAME, ModuleConstants.Personal.PERSONAL_ACTIVITY)
                            .setActivityOptions(ActivityOptionsCompat.makeBasic().toBundle())
                            .navigation(this)
                    false
                }
            }
            false
        }
    }

    private fun initData() {
        // 路由获取 Fragment 并展示
        SRouter.request("router://found/found_fragment?title=found&content=FoundContent")
                .navigation(this) {
                    val transaction = supportFragmentManager.beginTransaction()
                    foundFragment = it.getFragment()
                    transaction.add(R.id.fl_container, foundFragment)
                    transaction.commitAllowingStateLoss()
                }
    }
}
