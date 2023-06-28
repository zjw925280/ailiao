package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.CommonWebViewActivity;
import com.lovechatapp.chat.activity.ScrollLoginActivity;
import com.lovechatapp.chat.constant.Constant;


/**
 * 首冲弹窗
 */
public class AgreementDialog extends Dialog implements View.OnClickListener {

    private Activity activity;

    public AgreementDialog(@NonNull Activity context) {
        super(context);
        activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_agreement);

        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.setAttributes(lp);

        setCancelable(false);

        findViewById(R.id.confirm_tv).setOnClickListener(this);
        findViewById(R.id.cancel_tv).setOnClickListener(this);

        String content = activity.getString(R.string.alert_agreement_des);
        int index1 = content.indexOf("《用户协议》");
        int index2 = content.indexOf("《隐私政策》");
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.main)),
                index1,
                index1 + 6,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
                                    @Override
                                    public void onClick(@NonNull View widget) {
                                        //用户协议
                                        Intent intent = new Intent(activity, CommonWebViewActivity.class);
                                        intent.putExtra(Constant.TITLE, activity.getString(R.string.agree_detail));
                                        intent.putExtra(Constant.URL, "file:///android_asset/agree.html");
                                        activity.startActivity(intent);
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        ds.setUnderlineText(false);
                                    }
                                },
                index1,
                index1 + 6,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.main)),
                index2,
                index2 + 6,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {

                                    @Override
                                    public void onClick(@NonNull View widget) {
                                        //隐私协议
                                        Intent intent = new Intent(activity, CommonWebViewActivity.class);
                                        intent.putExtra(Constant.TITLE, activity.getString(R.string.private_detail));
                                        intent.putExtra(Constant.URL, "file:///android_asset/private.html");
                                        activity.startActivity(intent);
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        ds.setUnderlineText(false);
                                    }
                                },
                index2,
                index2 + 6,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView desTv = findViewById(R.id.des_tv);
        desTv.setMovementMethod(LinkMovementMethod.getInstance());
        desTv.setText(spannableString);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.confirm_tv) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
            sp.edit().putBoolean("agree", true).apply();
            activity.startActivity(new Intent(activity, ScrollLoginActivity.class));
        } else {
            activity.finish();
        }
        dismiss();
    }

}