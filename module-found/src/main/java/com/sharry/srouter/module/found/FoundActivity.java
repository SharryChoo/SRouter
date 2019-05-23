package com.sharry.srouter.module.found;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sharry.srouter.annotation.Query;
import com.sharry.srouter.annotation.Route;
import com.sharry.srouter.module.base.ModuleConstants;
import com.sharry.srouter.support.facade.SRouter;

/**
 * 第一个 Module 中的 Activity.
 *
 * @author Sharry <a href="SharryChooCHN@Gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2018/8/22 20:15
 */
@Route(
        authority = ModuleConstants.Found.NAME,
        path = ModuleConstants.Found.FOUND_ACTIVITY,
        desc = "组件1的入口页面"
)
public class FoundActivity extends AppCompatActivity {

    @Query("opr")
    String oprNo;

    @Query("password")
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.found_activity_found);
        parseIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void parseIntent() {
        SRouter.bindQuery(this);
        TextView textView = findViewById(R.id.tv_desc);
        textView.setText("oprNo = " + oprNo + ", password = " + password);
    }
}
