package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.ApplyCompanyActivity;

/**
 * 申请公会
 */
public class VerifyCompanyDialog extends Dialog {

    private Activity activity;

    public VerifyCompanyDialog(@NonNull Activity context) {
        super(context, R.style.DialogStyle_Dark_Background);
        activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_connect_qq_layout);

        setDialogView( this);
        Point outSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = activity.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER);
        }
        setCanceledOnTouchOutside(true);
    }


    /**
     * 设置view
     */
    private void setDialogView(final Dialog mDialog) {

        //取消
        ImageView cancel_iv = findViewById(R.id.cancel_iv);
        cancel_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        //确定
        TextView confirm_tv = findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ApplyCompanyActivity.class);
                activity.startActivity(intent);
                mDialog.dismiss();
            }
        });
    }
}