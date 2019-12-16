package com.sharry.srouter.module.found;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.sharry.srouter.annotation.compiler.Query;
import com.sharry.srouter.annotation.compiler.Route;
import com.sharry.srouter.module.base.ModuleConstants;
import com.sharry.srouter.support.PendingRunnable;
import com.sharry.srouter.support.SRouter;


/**
 * A simple {@link Fragment} subclass.
 */
@Route(
        authority = ModuleConstants.Found.NAME,
        path = ModuleConstants.Found.FOUND_FRAGMENT,
        desc = "组件1的入口页面"
)
public class FoundFragment extends Fragment {

    @Query(key = "title")
    String title = "default_title";

    @Query(key = "amount")
    double amount = 1.0;

    public FoundFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SRouter.bindQuery(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.found_fragment_found, container, false);
        TextView textView = view.findViewById(R.id.tv_center_text);
        textView.setText("title = " + title + ", amount = " + amount);
        view.findViewById(R.id.btn_notify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });
        return view;
    }

    private NotificationManager mManager;
    private static final String CHANNEL_ID = FoundFragment.class.getSimpleName();
    private static final String CHANNEL_NAME = "通知弹窗";

    /**
     * 该对象会添加到 static 集合, 虽然使用软引用保证 OOM 之前会被释放, 但生命周期依旧比较长
     * <p>
     * 请尽量不与外界 Context 关联, 减少内存泄漏
     */
    public static class MyPendingRunnable implements PendingRunnable {

        @Override
        public void run(@NonNull Activity hookActivity) {
            SRouter.request("SRouter://login/login_activity?email=123456@Gmail.com&password=123456")
                    .navigation();
            hookActivity.finish();
        }

    }

    private void sendNotification() {
        // 构建 Notification
        Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.app_launcher)
                .setAutoCancel(true)
                .setTicker(CHANNEL_NAME)
                .setContentTitle("SRouter pendingIntent 构建测试")
                .setContentText("测试路由构建 PendingIntent")
                // 构建 PendingIntent
                .setContentIntent(
                        SRouter.newPendingIntent(PendingIntent.FLAG_UPDATE_CURRENT, new MyPendingRunnable())
                )
                .build();
        int notifyId = 566;
        getNotificationManager().notify(notifyId, notification);
    }


    /**
     * Get instance of NotificationManager
     */
    private NotificationManager getNotificationManager() {
        if (null == mManager) {
            mManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= 26 && null != mManager) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.enableVibration(true);
                mManager.createNotificationChannel(channel);
            }
        }
        return mManager;
    }


}
