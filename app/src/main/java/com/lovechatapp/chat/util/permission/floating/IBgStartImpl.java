package com.lovechatapp.chat.util.permission.floating;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.util.permission.floating.api.BgStart;
import com.lovechatapp.chat.util.permission.floating.api.PermissionLisenter;
import com.lovechatapp.chat.util.permission.floating.bridge.BridgeActivity;
import com.lovechatapp.chat.util.permission.floating.bridge.BridgeBroadcast;


/**
 * Copyright (C), 2015-2020
 * FileName: IBgStartImpl
 * Author: zx
 * Date: 2020/4/26 9:36
 * Description:
 */
public class IBgStartImpl implements BgStart {
    private static final int TIME_DELAY = 600;
    private Handler mHhandler = new Handler();
    private CheckRunable mRunnable;


    @Override
    public void startActivity(Activity context, Intent intent, String className) {
        if (context == null || intent == null || TextUtils.isEmpty(className)) {
            return;
        }
        if (Miui.isMIUI()) {
            if (Miui.isAllowed(context)) {
                context.startActivity(intent);
            } else {
                //TODO custom notify
            }
        } else if (PermissionUtil.hasPermission(context)) {
            context.startActivity(intent);
        } else {
            context.startActivity(intent);
            if (mRunnable == null)
                mRunnable = new CheckRunable(className, intent, context);
            if (mRunnable.isPostDelayIsRunning()) {
                mHhandler.removeCallbacks(mRunnable);
            }
            mRunnable.setPostDelayIsRunning(true);
            mHhandler.postDelayed(mRunnable, TIME_DELAY);
        }

    }

    @Override
    public void requestStartPermission(final Activity activity, final PermissionLisenter lisenter) {
        PermissionLisenter li = new PermissionLisenter() {
            @Override
            public void onGranted() {
                req(activity, lisenter);
            }

            @Override
            public void cancel() {
                if (lisenter != null) {
                    lisenter.cancel();
                }
            }

            @Override
            public void onDenied() {

            }
        };
        if (Miui.isMIUI()) {
            if (Miui.isAllowed(activity)) {
                if (lisenter != null) {
                    lisenter.onGranted();
                }
            } else {
                new MiuiSource().show(activity, li);
            }
        } else if (PermissionUtil.hasPermission(activity) && !Miui.isMIUI()) {
            if (lisenter != null) {
                lisenter.onGranted();
            }
        } else {
            new AlertDialog.Builder(activity)
                    .setMessage("打开悬浮窗，不漏过一个来电")
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton("去打开", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            req(activity, lisenter);
                        }
                    }).create().show();
        }
    }

    private void req(Activity activity, PermissionLisenter lisenter) {
        try {
            BridgeBroadcast bridgeBroadcast = new BridgeBroadcast(lisenter);
            bridgeBroadcast.register(activity);
            Intent intent = new Intent(activity, BridgeActivity.class);
            activity.startActivityForResult(intent, SystemAlertWindow.REQUEST_OVERLY);
        } catch (Exception e) {
            ToastUtil.INSTANCE.showToast("跳转设置失败");
        }

    }
}
