package com.sharry.srouter.module.found;


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
import com.sharry.srouter.support.Callback;
import com.sharry.srouter.support.Response;
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

    private void sendNotification() {
        // 构建点击时的 Intent
        SRouter.request("SRouter://login/login_activity?email=123456@Gmail.com&password=123456")
                .pendingIntent(getContext(), 0, new Callback() {
                    @Override
                    public void onSuccess(@NonNull Response response) {
                        PendingIntent pendingIntent = response.getPendingIntent();
                        // 构建 Notification
                        Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)// 设置 Notification 在状态栏显示的优先级
                                .setDefaults(NotificationCompat.DEFAULT_ALL)// 根据手机情况默认设置Led灯, 震动, 和铃声
                                .setWhen(System.currentTimeMillis())// 该 Notification 被创建的时间
                                .setSmallIcon(R.mipmap.app_launcher)
                                .setAutoCancel(true)
                                .setTicker(CHANNEL_NAME)
                                .setContentTitle("SRouter pendingIntent 构建测试")
                                .setContentText("测试路由构建 PendingIntent")
                                .setContentIntent(pendingIntent)
                                .build();
                        int notifyId = 566;
                        getNotificationManager().notify(notifyId, notification);
                    }
                });
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
