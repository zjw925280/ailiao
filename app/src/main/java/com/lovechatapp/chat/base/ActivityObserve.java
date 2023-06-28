package com.lovechatapp.chat.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.WindowManager;

import com.lovechatapp.chat.BuildConfig;
import com.lovechatapp.chat.activity.AudioChatActivity;
import com.lovechatapp.chat.activity.CallingActivity;
import com.lovechatapp.chat.activity.MultipleAudioActivity;
import com.lovechatapp.chat.activity.MultipleVideoActivity;
import com.lovechatapp.chat.activity.ScrollLoginActivity;
import com.lovechatapp.chat.activity.SplashActivity;
import com.lovechatapp.chat.activity.VideoChatActivity;
import com.lovechatapp.chat.activity.WaitActorActivity;
import com.lovechatapp.chat.im.ChatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityObserve implements Application.ActivityLifecycleCallbacks {

    private List<Activity> activityList;

    /**
     * 限制截屏的activity
     */
    private final Class[] secureActivities = {
            VideoChatActivity.class,
            MultipleVideoActivity.class,
            ChatActivity.class};

    private final Map<Class, Integer> secureMap = new HashMap<>();

    ActivityObserve() {
        activityList = new ArrayList<>();
        for (Class secureActivity : secureActivities) {
            secureMap.put(secureActivity, 0);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        if (!BuildConfig.DEBUG && AppManager.SecureEnable && secureMap.containsKey(activity.getClass())) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

        activityList.add(activity);

        if (isChatActivity(activity.getClass())) {
            for (Activity activity1 : activityList) {
                if (isWaitActivity(activity1.getClass())) {
                    activity1.finish();
                }
            }
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activityList.remove(activity);
    }

    private boolean isChatActivity(Class c) {
        return c == VideoChatActivity.class
                || c == AudioChatActivity.class
                || c == MultipleVideoActivity.class
                || c == MultipleAudioActivity.class;
    }

    private boolean isWaitActivity(Class c) {
        return c == WaitActorActivity.class || c == CallingActivity.class;
    }

    /**
     * 是否正在聊天
     */
    public final boolean isChatting() {
        for (Activity activity : activityList) {
            if (isChatActivity(activity.getClass())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否正在聊天或等待接听
     */
    public final boolean isWaitChatState() {
        for (Activity activity : activityList) {
            if (activity.getClass() == VideoChatActivity.class
                    || activity.getClass() == AudioChatActivity.class
                    || activity.getClass() == MultipleVideoActivity.class
                    || activity.getClass() == MultipleAudioActivity.class
                    || activity.getClass() == WaitActorActivity.class
                    || activity.getClass() == CallingActivity.class) {
                return true;
            }
        }
        return false;
    }

    public boolean isSplash() {
        for (Activity activity : activityList) {
            if (activity.getClass() == SplashActivity.class && !activity.isFinishing()) {
                return true;
            }
        }
        return false;
    }

    final void finishAllExcludeLogin() {
        for (Activity activity : activityList) {
            if (activity.getClass() != ScrollLoginActivity.class && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public final boolean isActive() {
        for (Activity activity : activityList) {
            if (activity.getClass() != SplashActivity.class) {
                return true;
            }
        }
        return false;
    }

}