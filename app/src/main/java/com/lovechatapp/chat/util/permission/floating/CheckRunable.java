package com.lovechatapp.chat.util.permission.floating;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.lovechatapp.chat.util.permission.floating.api.ShowSource;


/**
 * Copyright (C), 2015-2020
 * FileName: CheckRunable
 * Author: zx
 * Date: 2020/4/26 9:37
 * Description:
 */
public class CheckRunable implements Runnable {
    private String mClassName;
    private Intent mIntent;
    private boolean mPostDelayIsRunning;
    private StartType mType;
    private ShowSource mSource;
    private Activity mContext;

    public CheckRunable(String mClassName, Intent mIntent, Activity mContext) {
        this.mClassName = mClassName;
        this.mIntent = mIntent;
        this.mContext = mContext;
    }

    @Override
    public void run() {
        mPostDelayIsRunning = false;
        if (!isActivityOnTop()) {
            if (Miui.isMIUI()) {
                mSource = new NotifySource();
            } else {
                switch (mType) {
                    case FLOAT_WINDOW:
                        if (!PermissionUtil.hasPermission(mContext)) {
                            mSource = new FloatSource();
                        }
                        break;
                }
            }

        }
    }

    private boolean isActivityOnTop() {
        boolean result = false;
        String topActivityName = CustomActivityManager.getTopActivity();
        if (!TextUtils.isEmpty(topActivityName)) {
            if (topActivityName.contains(mClassName)) {
                result = true;
            }
        }
        return result;
    }

    public void setPostDelayIsRunning(boolean postDelayIsRunning) {
        this.mPostDelayIsRunning = postDelayIsRunning;
    }

    public boolean isPostDelayIsRunning() {
        return mPostDelayIsRunning;
    }
}
