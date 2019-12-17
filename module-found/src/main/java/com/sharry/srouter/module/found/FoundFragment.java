package com.sharry.srouter.module.found;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.sharry.srouter.annotation.compiler.Query;
import com.sharry.srouter.annotation.compiler.Route;
import com.sharry.srouter.module.base.ModuleConstants;
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
        SRouter.bindQuery(this, getArguments());
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
     * 方案 1
     * 交由用户指定一个 Hook Activity, 然后将参数通过 Intent 传入, 最终通过路由跳转
     * 用户使用成本稍高, 需要在 HookActivity 中接收我们传入的参数, 没有内存泄漏的风险
     * <p>
     * 方案 2
     * 自定义 Hook Activity, 使用静态集合记录用户的 PendingRunnable, PendingIntent 触发时, 在 HookActivity 中执行这个 PendingRunnable
     * 优点: 使用方便, 用户对自己的操作可见
     * 缺点:
     * 因为使用的静态集合, 若是通知被移除, 则会导致 PendingRunnable 常驻在静态缓存中, 即使使用 SoftReference 依旧会导致 PendingRunnable 生存周期较长
     * 进程被杀, 意味着静态集合被清空, 若是通知尚在, 点击便是无意义的操作
     */
    private void sendNotification() {
        Intent proxyIntent = SRouter.proxyIntentBuilder()
                .uri("SRouter://login/login_activity?email=123456@Gmail.com&password=123456")
                .build();
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(),
                0, proxyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
                .setContentIntent(pendingIntent)
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
