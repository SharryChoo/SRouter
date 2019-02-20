package com.sharry.component1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sharry.srouterannotation.Route;
import com.sharry.srouterannotation.ThreadMode;
import com.sharry.sroutersupport.facade.SRouter;

/**
 * 第一个 Module 中的 Activity.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/22 20:15
 */
@Route(
        authority = "component1/Component1Activity",
        mode = ThreadMode.MAIN,
        desc = "组件1的入口页面"
)
public class Component1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_one);
        findViewById(R.id.tvJump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SRouter.getInstance()
                        .build("component2/Component2Activity")
                        .withString("extra_string", "")
                        .navigation(v.getContext());
            }
        });
    }

}
