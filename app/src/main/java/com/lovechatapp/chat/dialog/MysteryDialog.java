package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.VipCenterActivity;

/**
 * 神秘人弹窗
 */
public class MysteryDialog extends Dialog implements View.OnClickListener {

    public MysteryDialog(@NonNull Activity context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_mystery);

        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        win.setAttributes(lp);

        setCanceledOnTouchOutside(true);
        setCancelable(true);

        findViewById(R.id.vip_btn).setOnClickListener(this);
        findViewById(R.id.dismiss_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vip_btn) {
            VipCenterActivity.start(getContext(), false);
        }
        dismiss();
    }
}