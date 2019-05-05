package com.sharry.srouter.module.component1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sharry.srouter.annotation.Route;
import com.sharry.srouter.annotation.ThreadMode;

/**
 * 第一个 Module 中的 Activity.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/22 20:15
 */
@Route(
        authority = "modulecomponent1",
        path = "FoundActivity",
        mode = ThreadMode.MAIN,
        desc = "组件1的入口页面"
)
public class FoundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_activity_found);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
