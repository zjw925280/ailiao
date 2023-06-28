package com.lovechatapp.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.view.PwdEditText;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：未成年模式密码页面
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class YoungModePasswordActivity extends BaseActivity {

    //标题
    @BindView(R.id.title_password_tv)
    TextView mTitlePasswordTv;
    //密码
    @BindView(R.id.pass_code_et)
    PwdEditText mPassCodeEt;
    //操作
    @BindView(R.id.option_tv)
    TextView mOptionTv;
    //描述
    @BindView(R.id.des_tv)
    TextView mDesTv;

    //第一次输入的密码
    private String mLastPassword = "";
    private static final String IS_INPUT_PASSWORD = "from_type";

    public static void startYoungPasswordActivity(Context context, boolean isInputPassword) {
        Intent intent = new Intent(context, YoungModePasswordActivity.class);
        intent.putExtra(IS_INPUT_PASSWORD, isInputPassword);
        context.startActivity(intent);
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_young_mode_password_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.verify_password);
        mHeadLineV.setVisibility(View.INVISIBLE);
        initStart();
        showSoftInput();
    }

    /**
     * 初始化开始
     */
    private void initStart() {
        boolean isInputPassword = getIntent().getBooleanExtra(IS_INPUT_PASSWORD, false);
        if (isInputPassword) {//如果是输入密码
            mDesTv.setText(getString(R.string.close_password_des));
            mOptionTv.setText(getString(R.string.confirm));
        }
        mPassCodeEt.setOnTextChangeListeven(new PwdEditText.OnTextChangeListeven() {
            @Override
            public void onTextChange(String pwd) {
                if (!TextUtils.isEmpty(pwd) && pwd.length() >= 4) {
                    hideSoft();
                }
            }
        });
    }

    @OnClick({R.id.option_tv})
    public void onClick(View v) {
        //操作
        if (v.getId() == R.id.option_tv) {
            doOption();
        }
    }

    /**
     * 操作
     */
    private void doOption() {
        //密码
        String password = mPassCodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(password) || password.length() == 0) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_password);
            return;
        }
        if (password.length() < 4) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_long_password);
            return;
        }

        String text = mOptionTv.getText().toString().trim();
        if (text.equals(getString(R.string.next_step))) {//下一步
            mLastPassword = password;
            mTitlePasswordTv.setText(getString(R.string.please_input_password_again));
            mOptionTv.setText(getString(R.string.finish));
            mPassCodeEt.clearText();
            showSoftInput();
        } else if (text.equals(getString(R.string.finish))) {
            if (!TextUtils.equals(mLastPassword, password)) {
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.password_wrong);
                return;
            }
            //保存密码
            SharedPreferenceHelper.setYoungPassword(getApplicationContext(), password);
            finish();
        } else if (text.equals(getString(R.string.confirm))) {
            //保存的密码
            String savePassword = SharedPreferenceHelper.getYoungPassword(getApplicationContext());
            if (!TextUtils.equals(savePassword, password)) {
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.wrong_password);
                return;
            }
            //输入正确的话, 删掉保存的
            SharedPreferenceHelper.setYoungPassword(getApplicationContext(), "");
            finish();
        }
    }

    /**
     * 隐藏软件盘
     */
    private void hideSoft() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && mPassCodeEt != null) {
                imm.hideSoftInputFromWindow(mPassCodeEt.getWindowToken(), 0);
                mPassCodeEt.clearFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示软件盘
     */
    private void showSoftInput() {
        try {
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null && mPassCodeEt != null && mPassCodeEt.requestFocus()) {
                        imm.showSoftInput(mPassCodeEt, 0);
                    }
                }
            }, 200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
