package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.ChargeActivity;

/**
 * 金币不足
 */
public class MoneyNotEnoughDialog extends Dialog {

    private Activity activity;

    public MoneyNotEnoughDialog(@NonNull Activity context) {
        super(context, R.style.DialogStyle);
        activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_money_not_enough_layout);
        setCanceledOnTouchOutside(true);

        //取消
        TextView ignore_tv = findViewById(R.id.ignore_tv);
        ignore_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //充值
        TextView charge_tv = findViewById(R.id.charge_tv);
        charge_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ChargeActivity.class);
                activity.startActivity(intent);
                dismiss();
            }
        });
    }


}