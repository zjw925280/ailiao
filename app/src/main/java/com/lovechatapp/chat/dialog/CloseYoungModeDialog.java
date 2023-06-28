package com.lovechatapp.chat.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.PhoneVerifyActivity;
import com.lovechatapp.chat.activity.YoungModePasswordActivity;
import com.lovechatapp.chat.base.BaseActivity;


/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：设置未成年模式dialog
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CloseYoungModeDialog extends DialogFragment {

    private BaseActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mActivity = (BaseActivity) getActivity();
        return inflater.inflate(R.layout.dialog_close_yound_mode_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //关闭未成年模式
        TextView close_mode_tv = view.findViewById(R.id.close_mode_tv);
        close_mode_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    YoungModePasswordActivity.startYoungPasswordActivity(getActivity(), true);
                }
            }
        });
        //忘记密码
        TextView forget_tv = view.findViewById(R.id.forget_tv);
        forget_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    int fromForgetPassword = 2;
                    PhoneVerifyActivity.startPhoneVerifyActivity(getActivity(), fromForgetPassword);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
            if (window != null) {
                // 一定要设置Background，如果不设置，window属性设置无效
                window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
                DisplayMetrics dm = new DisplayMetrics();
                if (getActivity() != null) {
                    WindowManager windowManager = getActivity().getWindowManager();
                    if (windowManager != null) {
                        windowManager.getDefaultDisplay().getMetrics(dm);
                        WindowManager.LayoutParams params = window.getAttributes();
                        params.gravity = Gravity.CENTER;
                        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        window.setAttributes(params);
                    }
                }
            }
        }
    }

}
