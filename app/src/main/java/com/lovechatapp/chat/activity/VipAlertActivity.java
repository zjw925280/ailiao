package com.lovechatapp.chat.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.dialog.VipDialog;


/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   约豆不足页面
 * 作者：
 * 创建时间：2018/8/15
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class VipAlertActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, VipAlertActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected View getContentView() {
        return new View(this);
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        VipDialog vipDialog = new VipDialog(this);
        vipDialog.show();
        vipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
    }
}