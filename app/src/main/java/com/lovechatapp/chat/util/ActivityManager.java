package com.lovechatapp.chat.util;

import android.app.Activity;
import android.content.Intent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class ActivityManager {

    private List<WeakReference<Activity>> mActivityList;
    private static ActivityManager mInstance;

    private ActivityManager() {
        mActivityList = new ArrayList<WeakReference<Activity>>();
    }

    public synchronized static ActivityManager getInstance() {
        if (mInstance == null) {
            mInstance = new ActivityManager();
        }
        return mInstance;
    }

    /**
     * 添加Activity
     *
     * @param act
     */
    public void addActivity(Activity act) {
        if (mActivityList != null && act != null) {
            mActivityList.add(new WeakReference<Activity>(act));
        }
    }

    /**
     * 从列表中移除元素
     *
     * @param targetAct
     */
    public void removeActivity(Activity targetAct) {
        if (mActivityList == null || targetAct == null) {
            return;
        }

        Iterator<WeakReference<Activity>> it = mActivityList.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> wefAct = it.next();
            Activity act = null;
            if (wefAct == null || (act = wefAct.get()) == null) {
                if (wefAct != null && act == null) {
                    it.remove();
                }
                continue;
            }

            if (act == targetAct) {
                it.remove();
            }
        }
    }

    /**
     * 结束单个(或多个)activity
     *
     * @param cls
     */
    public void finishActivity(Class cls) {
        if (mActivityList == null || cls == null) {
            return;
        }

        Iterator<WeakReference<Activity>> it = mActivityList.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> wefAct = it.next();
            Activity act = null;
            if (wefAct == null || (act = wefAct.get()) == null) {
                if (wefAct != null && act == null) {
                    it.remove();
                }
                continue;
            }

            if (act.getClass() == cls) {
                it.remove();
                act.finish();
                act = null;
            }
        }
    }

    /**
     * 结束所有Activity
     *
     * @param excludeCls 不包含该Activity
     */
    public void finishAllActivity(Class excludeCls) {
        if (mActivityList == null) {
            return;
        }

        Iterator<WeakReference<Activity>> it = mActivityList.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> wefAct = it.next();
            Activity act = null;
            if (wefAct == null || (act = wefAct.get()) == null) {
                if (wefAct != null && act == null) {
                    it.remove();
                }
                continue;
            }

            //跳过不含的activity
            if (act.getClass() == excludeCls) {
                continue;
            }

            it.remove();
            act.finish();
            act = null;
        }
    }

    /**
     * 获取顶部Activity
     *
     * @return
     */
    public Activity getTopActivity() {
        if (mActivityList == null || mActivityList.isEmpty()) {
            return null;
        }

        WeakReference<Activity> wf = mActivityList.get(mActivityList.size() - 1);
        return wf.get();
    }

    /**
     * 退出应用程序
     */
    public void exit() {
        try {
            finishAllActivity(null);
            //System.exit(0);
        } catch (Exception e) {
        }
    }

    public static void startActivityDelay(final Activity activity, final Class tClass, long delay) {
        try {
            activity.getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(activity, tClass);
                    activity.startActivity(intent);
                }
            }, delay);
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(activity, tClass);
            activity.startActivity(intent);
        }
    }

}
