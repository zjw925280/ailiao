package com.lovechatapp.chat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;

import com.lovechatapp.chat.R;

/*
 * Copyright (C)
 * 版权所有
 *
 * 功能描述：检测到不规范行为dialog
 * 作者：
 * 创建时间：
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class WarnDialog extends Dialog implements View.OnClickListener {

    public WarnDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_warn_layout);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setGravity(Gravity.CENTER);

        findViewById(R.id.confirm_tv).setOnClickListener(this);
    }

    public void onClick(View v) {
        dismiss();
    }

}