package com.lovechatapp.chat.helper;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.listener.OnCommonListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 监听应用前后台状态
 */
public class ActivityRegister implements Application.ActivityLifecycleCallbacks {

    private static ActivityRegister activityRegister;

    private int onCount;

    private boolean isBackground;

    private List<OnCommonListener<Boolean>> onCommonListeners;

    private ActivityRegister() {
        onCommonListeners = new ArrayList<>();
        AppManager.getInstance().registerActivityLifecycleCallbacks(this);
    }

    public static ActivityRegister get() {
        if (activityRegister == null) {
            activityRegister = new ActivityRegister();
        }
        return activityRegister;
    }

    public final boolean isBackground() {
        return isBackground;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        onCount++;
        update();
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        onCount--;
        update();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public final void add(OnCommonListener<Boolean> listener) {
        if (listener != null && !onCommonListeners.contains(listener)) {
            onCommonListeners.add(listener);
        }
    }

    private void update() {
        boolean b = onCount == 0;
        if (b != isBackground) {
            isBackground = b;
            for (OnCommonListener<Boolean> onCommonListener : onCommonListeners) {
                onCommonListener.execute(isBackground);
            }
        }
    }
}