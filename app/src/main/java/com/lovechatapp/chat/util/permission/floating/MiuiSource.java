package com.lovechatapp.chat.util.permission.floating;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.util.permission.floating.api.PermissionLisenter;
import com.lovechatapp.chat.util.permission.floating.api.ShowSource;


/**
 * Copyright (C), 2015-2020
 * FileName: MiuiSource
 * Author: zx
 * Date: 2020/4/26 9:35
 * Description:
 */
public class MiuiSource implements ShowSource {

    @Override
    public void show(final Activity context, final PermissionLisenter permissionListener) {
        new AlertDialog.Builder(context).setCancelable(false)
                .setTitle("温馨提示")
                .setMessage("未允许【后台弹出页面】，收不到来电提醒哦")
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(permissionListener!=null){
                            permissionListener.onGranted();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (permissionListener != null)
                            permissionListener.cancel();
                    }
                }).show();
    }
}
