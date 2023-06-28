package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.VipCenterActivity;

/**
 * vip提示
 */
public class VipDialog extends Dialog {

    private String messsge;
    private Activity activity;

    public VipDialog(@NonNull Activity context) {
        super(context);
        activity = context;
    }

    public VipDialog(@NonNull Activity context, String message) {
        super(context);
        this.messsge = message;
        activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vip_dialog);

        getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCancelable(false);

        if (!TextUtils.isEmpty(messsge)) {
            ((TextView) findViewById(R.id.content_tv)).setText(messsge);
        }

        findViewById(R.id.confirm_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                VipCenterActivity.start(activity, false);
            }
        });

        findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}