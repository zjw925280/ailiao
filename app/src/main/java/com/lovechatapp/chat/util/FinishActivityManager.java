package com.lovechatapp.chat.util;

import android.app.Activity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FinishActivityManager {

    public FinishActivityManager() {
    }
    private static FinishActivityManager sManager;
    private List<Activity> activityList;
    public static FinishActivityManager getManager() {
        if (sManager == null) {
            synchronized (FinishActivityManager.class) {
                if (sManager == null) {
                    sManager = new FinishActivityManager();
                }
            }
        }
        return sManager;
    }
    /**
     * 添加Activity到集合中
     */
    public void addActivity(Activity activity) {
        if (activityList == null) {
            activityList = new LinkedList<>();
        }
        activityList.add(activity);
    }
    /**
     * 关闭指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activityList != null && activity != null && activityList.contains(activity)) {
            //使用迭代器安全删除
            for (Iterator<Activity> it = activityList.iterator(); it.hasNext(); ) {
                Activity temp = it.next();
                // 清理掉已经释放的activity
                if (temp == null) {
                    it.remove();
                    continue;
                }
                if (temp == activity) {
                    it.remove();
                }
            }
            activity.finish();
        }
    }
    /**
     * 关闭指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        if (activityList != null) {
            // 使用迭代器安全删除
            for (Iterator<Activity> it = activityList.iterator(); it.hasNext(); ) {
                Activity activity = it.next();
                // 清理掉已经释放的activity
                if (activity == null) {
                    it.remove();
                    continue;
                }
                if (activity.getClass().equals(cls)) {
                    it.remove();
                    activity.finish();
                }
            }
        }
    }
    /**
     * 关闭所有的Activity
     */
    public void finishAllActivity() {
        if (activityList != null) {
            for (Iterator<Activity> it = activityList.iterator(); it.hasNext(); ) {
                Activity activity = it.next();
                if (activity != null) {
                    activity.finish();
                }
            }
            activityList.clear();
        }
    }
    /**
     * 退出应用程序
     */
    public void exitApp() {
        try {
            finishAllActivity();
            // 退出JVM,释放所占内存资源,0表示正常退出
            System.exit(0);
            // 从系统中kill掉应用程序
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
