package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.ChargeActivity;
import com.lovechatapp.chat.activity.VipCenterActivity;
import com.lovechatapp.chat.fragment.MansionManFragment;

/**
 * 府邸权限提示
 */
public class MansionPermissionDialog extends Dialog implements View.OnClickListener {

    private Activity activity;

    public MansionPermissionDialog(@NonNull Activity context) {
        super(context);
        activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_add_mansion);

        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.setAttributes(lp);

        setCanceledOnTouchOutside(false);
        setCancelable(false);

        findViewById(R.id.cancel_btn).setOnClickListener(this);
        findViewById(R.id.charge_btn).setOnClickListener(this);
        findViewById(R.id.vip_btn).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.charge_btn) {
            activity.startActivity(new Intent(activity, ChargeActivity.class));
        } else if (v.getId() == R.id.vip_btn) {
            activity.startActivity(new Intent(activity, VipCenterActivity.class));
        } else {
            dismiss();
        }
    }

    public final void show(MansionManFragment.MansionPermission mansionPermission) {
        show();

        String firstText = mansionPermission.mansionMoney + "金币";
        String secondText = "vip会员、svip会员";
        String content = String.format("只有消费超过%1$s或者开通%2$s才可以开通府邸", firstText, secondText);
        int firstIndex = content.indexOf(firstText);
        int secondIndex = content.indexOf(secondText);
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.red_fe2947)),
                firstIndex,
                firstIndex + firstText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.red_fe2947)),
                secondIndex,
                secondIndex + secondText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = findViewById(R.id.info_tv);
        textView.setText(spannableString);
    }
}