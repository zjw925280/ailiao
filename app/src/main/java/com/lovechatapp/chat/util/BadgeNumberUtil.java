package com.lovechatapp.chat.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.SplashActivity;
import com.lovechatapp.chat.base.AppManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 角标工具
 */
public class BadgeNumberUtil {

    /**
     * 华为角标实现
     */
    private static void setHuawei(int badgenumber) {
        Bundle extra = new Bundle();
        extra.putString("package", AppManager.getInstance().getPackageName());
        extra.putString("class", SplashActivity.class.getName());
        extra.putInt("badgenumber", badgenumber);
        AppManager.getInstance().getContentResolver()
                .call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"),
                        "change_badge", null, extra);
    }

    private static void setXiaoMi(int mCount) {
        //小米的需要发一个通知，进入应用后红点会自动消失，测试的时候进程只能在后台，否则没有效果
        NotificationManager notificationManager = (NotificationManager) AppManager.getInstance()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        String title = "这个是标题";
        String desc = "这个是内容";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "default";
            String channelName = "默认通知";
            notificationManager.createNotificationChannel(new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH));
        }
        Intent intent = new Intent();//可以用intent设置点击通知后的页面
        Notification notification = new NotificationCompat.Builder(AppManager.getInstance(), "default")
                .setContentIntent(PendingIntent.getActivity(AppManager.getInstance(), 0, intent, 0))
                .setContentTitle(title)
                .setContentText(desc)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .build();
        try {
            Field field = notification.getClass().getDeclaredField("extraNotification");
            Object extraNotification = field.get(notification);
            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
            method.invoke(extraNotification, mCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //VIVO
    private static void setBadgeOfVIVO(int num) {
        Intent intent = new Intent();
        intent.setAction("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
        intent.putExtra("packageName", AppManager.getInstance().getPackageName());
        intent.putExtra("className", SplashActivity.class.getName());
        intent.putExtra("notificationNum", num);
//        intent.addFlags(Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND);
        AppManager.getInstance().sendBroadcast(intent);
    }

    public static void setCount(int missTimMessageCount) {
        if (RomUtil.isEmui()) {
            if (missTimMessageCount <= 99) {
                setHuawei(missTimMessageCount);
            } else {
                setHuawei(99);
            }
        } else if (RomUtil.isMiui()) {
            if (missTimMessageCount <= 99) {
                setXiaoMi(missTimMessageCount);
            } else {
                setXiaoMi(99);
            }
        } else if (RomUtil.isVivo()) {
            if (missTimMessageCount <= 99) {
                setBadgeOfVIVO(missTimMessageCount);
            } else {
                setBadgeOfVIVO(99);
            }
        }
    }
}
