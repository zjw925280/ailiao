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

import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.lovechatapp.chat.R;

/**
 * 非法聊天提示
 */
public class InvalidChatDialog extends Dialog {

    private String message;
    private Activity activity;

    public InvalidChatDialog(@NonNull Activity context) {
        super(context);
        activity = context;
    }

    public InvalidChatDialog(@NonNull Activity context, String message) {
        super(context);
        this.message = message;
        activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_invalid_chat);

        getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = ScreenUtil.getScreenWidth(getContext()) / 10 * 9;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCancelable(false);

        if (!TextUtils.isEmpty(message)) {
            ((TextView) findViewById(R.id.content_tv)).setText(message);
        }

        findViewById(R.id.confirm_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}