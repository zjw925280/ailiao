package com.lovechatapp.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.ChatUserInfo;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.CodeUtil;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.SystemUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.util.VerifyUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：登录页面
 * 作者：
 * 创建时间：2018/6/22
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PhoneLoginActivity extends BaseActivity {

    @BindView(R.id.send_verify_tv)
    TextView mSendVerifyTv;//发送验证码
    @BindView(R.id.mobile_et)
    EditText mMobileEt;
    @BindView(R.id.code_et)
    EditText mCodeEt;
    //账号密码登录
    @BindView(R.id.account_v)
    View mAccountV;
    @BindView(R.id.account_small_tv)
    TextView mAccountSmallTv;
    @BindView(R.id.account_big_tv)
    TextView mAccountBigTv;
    //验证码登录
    @BindView(R.id.verify_small_tv)
    TextView mVerifySmallTv;
    @BindView(R.id.verify_big_tv)
    TextView mVerifyBigTv;
    @BindView(R.id.verify_v)
    View mVerifyV;
    @BindView(R.id.down_text_tv)
    TextView mDownTextTv;
    //忘记密码
    @BindView(R.id.forget_tv)
    TextView mForgetTv;

    @BindView(R.id.agree_cb)
    CheckBox checkBox;

    private CountDownTimer mCountDownTimer;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_login_layout);
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        mCodeEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mAccountV.getVisibility() == View.VISIBLE) {//账号密码登录
                        requestAccountLogin();
                    } else {//短信验证码登录
                        requestSmsLogin();
                    }
                    return true;
                }
                return false;
            }
        });
        switchPosition(0);
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    public boolean  checkAgree() {

        if (!checkBox.isChecked()) {
            ToastUtil.INSTANCE.showToast("请阅读并同意《用户协议》和《隐私政策》");
            return true;
        }
        return false;
    }

    @OnClick({R.id.private_tv,R.id.agree_tv,R.id.login_tv, R.id.send_verify_tv, R.id.account_ll, R.id.verify_code_ll, R.id.register_tv, R.id.forget_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.private_tv: {//隐私协议
                //隐私协议
                Intent intent = new Intent(this, CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, getString(R.string.private_detail));
//                intent.putExtra(Constant.URL, "file:///android_asset/private.html");
                intent.putExtra(Constant.URL, "http://api.lnqianlian.top:8080/tmApp/file/privacy.txt");
                startActivity(intent);
                break;
            }
            case R.id.agree_tv: {//用户协议
                //用户协议
                Intent intent = new Intent(this, CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, getString(R.string.agree_detail));
                intent.putExtra(Constant.URL, "http://api.zhongzhiqian.cn:8080/tmApp/file/agreement.txt");
                startActivity(intent);
                break;
            }
            case R.id.login_tv: {//登录
                if (checkAgree()) {
                    return;
                }
                if (mAccountV.getVisibility() == View.VISIBLE) {//账号密码登录
                    requestAccountLogin();
                } else {//短信验证码登录
                    requestSmsLogin();
                }
                break;
            }
            case R.id.send_verify_tv: {//发送短信验证码
                showVerifyDialog();
                break;
            }
            case R.id.account_ll: {//账号密码登录
                switchPosition(1);
                break;
            }
            case R.id.verify_code_ll: {//验证码登录
                switchPosition(0);
                break;
            }
            case R.id.register_tv: {//注册
                int joinTypeRegister = 0;//注册
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra(Constant.JOIN_TYPE, joinTypeRegister);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.forget_tv: {//忘记密码
                int joinTypeForget = 1;//忘记密码
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra(Constant.JOIN_TYPE, joinTypeForget);
                startActivity(intent);
                finish();
                break;
            }
        }
    }

    /**
     * 切换
     */
    private void switchPosition(int position) {
        if (position == 0) {//验证码
            if (mVerifyV.getVisibility() == View.VISIBLE) {
                return;
            }
            mVerifyBigTv.setVisibility(View.VISIBLE);
            mVerifySmallTv.setVisibility(View.GONE);
            mAccountBigTv.setVisibility(View.GONE);
            mAccountSmallTv.setVisibility(View.VISIBLE);
            mVerifyV.setVisibility(View.VISIBLE);
            mAccountV.setVisibility(View.GONE);
            mDownTextTv.setText(getString(R.string.verify_code));
            mCodeEt.setHint(getString(R.string.please_verify_code));
            mSendVerifyTv.setVisibility(View.VISIBLE);
            mForgetTv.setVisibility(View.INVISIBLE);
            mCodeEt.setInputType(InputType.TYPE_CLASS_NUMBER);
            mCodeEt.setText("");
            mMobileEt.setText("");
        } else {
            if (mAccountV.getVisibility() == View.VISIBLE) {
                return;
            }
            mAccountBigTv.setVisibility(View.VISIBLE);
            mAccountSmallTv.setVisibility(View.GONE);
            mVerifyBigTv.setVisibility(View.GONE);
            mVerifySmallTv.setVisibility(View.VISIBLE);
            mAccountV.setVisibility(View.VISIBLE);
            mVerifyV.setVisibility(View.GONE);
            mDownTextTv.setText(getString(R.string.password));
            mCodeEt.setHint(getString(R.string.please_input_password));
            mSendVerifyTv.setVisibility(View.GONE);
            mForgetTv.setVisibility(View.VISIBLE);
            mCodeEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mCodeEt.setText("");
            mMobileEt.setText("");
        }
    }

    /**
     * 账号密码登录
     */
    private void requestAccountLogin() {
        final String phone = mMobileEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.phone_number_null);
            return;
        }
        //密码
        String password = mCodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_password);
            return;
        }
        if (password.length() < 6 || password.length() > 16) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.length_wrong);
            return;
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phone", phone);
        paramMap.put("password", password);
        String channelId = AppManager.getInstance().getShareId();
        if (TextUtils.isEmpty(channelId)) {
            channelId = CodeUtil.getClipBoardContent(getApplicationContext());
        }
        paramMap.put("shareUserId", channelId);
        OkHttpUtils.post().url(ChatApi.USER_LOGIN())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
            @Override
            public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
                dismissLoadingDialog();
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ChatUserInfo userInfo = response.m_object;
                        if (userInfo != null) {
                            userInfo.phone = phone;
                            AppManager.getInstance().setUserInfo(userInfo);
                            SharedPreferenceHelper.saveAccountInfo(getApplicationContext(), userInfo);
                            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_success);
                            Intent intent;
                            if (userInfo.t_sex == 2) {
                                intent = new Intent(getApplicationContext(), ChooseGenderActivity.class);
                            } else {
                                intent = new Intent(getApplicationContext(), MainActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            if (!TextUtils.isEmpty(response.m_strMessage)) {
                                ToastUtil.INSTANCE.showToast(getApplicationContext(), response.m_strMessage);
                            } else {
                                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                            }
                        }
                    } else if (response.m_istatus == -1) {//被封号
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.INSTANCE.showToast(getApplicationContext(), message);
                        } else {
                            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                        }
                    } else if (response.m_istatus == -200) {//7天内已经登陆过其他账号
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.seven_days);
                    } else {
                        if (!TextUtils.isEmpty(response.m_strMessage)) {
                            ToastUtil.INSTANCE.showToast(getApplicationContext(), response.m_strMessage);
                        } else {
                            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                        }
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dismissLoadingDialog();
            }
        });

    }

    /**
     * 请求短信验证码登录
     */
    private void requestSmsLogin() {
        final String phone = mMobileEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.phone_number_null);
            return;
        }
        if (!VerifyUtils.isPhoneNum(phone)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.wrong_phone_number);
            return;
        }
        String verifyCode = mCodeEt.getText().toString().trim();
        if (TextUtils.isEmpty(verifyCode)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.verify_code_number_null);
            return;
        }
        //获取真实IP
        getRealIp(phone, verifyCode);
    }

    /**
     * 获取WX登录方式真实ip
     */
    private void getRealIp(final String phone, final String verifyCode) {
        OkHttpUtils.get().url(ChatApi.GET_REAL_IP())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                requestSmsLogin("0.0.0.0", phone, verifyCode);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.i("WX真实IP: " + response);
                if (!TextUtils.isEmpty(response) && response.contains("{") && response.contains("}")) {
                    try {
                        int startIndex = response.indexOf("{");
                        int endIndex = response.indexOf("}");
                        String content = response.substring(startIndex, endIndex + 1);
                        LogUtil.i("截取的: " + content);
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(content);
                        String cip = jsonObject.getString("cip");
                        if (!TextUtils.isEmpty(cip)) {
                            requestSmsLogin(cip, phone, verifyCode);
                        } else {
                            requestSmsLogin("0.0.0.0", phone, verifyCode);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        requestSmsLogin("0.0.0.0", phone, verifyCode);
                    }
                } else {
                    requestSmsLogin("0.0.0.0", phone, verifyCode);
                }
            }
        });
    }

    /**
     * 发送短信验证码
     */
    private void sendSmsVerifyCode(String imageCode) {
        String phone = mMobileEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.phone_number_null);
            return;
        }
        if (!VerifyUtils.isPhoneNum(phone)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.wrong_phone_number);
            return;
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phone", phone);
        paramMap.put("resType", "1");
        paramMap.put("verifyCode", imageCode);
        OkHttpUtils.post().url(ChatApi.SEND_SMS_CODE())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                LogUtil.i("获取短信验证码==--", JSON.toJSONString(response));
                dismissLoadingDialog();
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.send_success))) {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.send_success_des);
                        startCountDown();
                    }
                } else if (response != null && response.m_istatus == NetCode.FAIL) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message)) {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), message);
                    } else {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.send_code_fail);
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.send_code_fail);
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dismissLoadingDialog();
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 开始倒计时
     */
    private void startCountDown() {
        mSendVerifyTv.setClickable(false);
        mCountDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                String text = getResources().getString(R.string.re_send_one) + l / 1000 + getResources().getString(R.string.second);
                mSendVerifyTv.setText(text);
            }

            @Override
            public void onFinish() {
                mSendVerifyTv.setClickable(true);
                mSendVerifyTv.setText(R.string.get_code_one);
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                    mCountDownTimer = null;
                }
            }
        }.start();
    }


    /**
     * 验证码登录方式登录
     */
    private void requestSmsLogin(String ip, final String phone, String verifyCode) {
        //用于师徒
        String t_system_version = "Android " + SystemUtil.getSystemVersion();
        String deviceNumber = SystemUtil.getOnlyOneId(getApplicationContext());

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phone", phone);
        paramMap.put("smsCode", verifyCode);
        paramMap.put("t_phone_type", "Android");
        paramMap.put("t_system_version", TextUtils.isEmpty(t_system_version) ? "" : t_system_version);
        paramMap.put("deviceNumber", deviceNumber);
        paramMap.put("ip", ip);
        String channelId = AppManager.getInstance().getShareId();
        if (TextUtils.isEmpty(channelId)) {
            channelId = CodeUtil.getClipBoardContent(getApplicationContext());
        }
        paramMap.put("shareUserId", channelId);
        OkHttpUtils.post().url(ChatApi.LOGIN())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
            @Override
            public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
                LogUtil.i("短信验证码登录==--", JSON.toJSONString(response));
                dismissLoadingDialog();
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        CodeUtil.clearClipBoard(getApplicationContext());
                        ChatUserInfo userInfo = response.m_object;
                        if (userInfo != null) {
                            userInfo.phone = phone;
                            AppManager.getInstance().setUserInfo(userInfo);
                            SharedPreferenceHelper.saveAccountInfo(getApplicationContext(), userInfo);
                            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_success);
                            Intent intent;
                            if (userInfo.t_sex == 2) {
                                intent = new Intent(getApplicationContext(), ChooseGenderActivity.class);
                            } else {
                                intent = new Intent(getApplicationContext(), MainActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            if (!TextUtils.isEmpty(response.m_strMessage)) {
                                ToastUtil.INSTANCE.showToast(getApplicationContext(), response.m_strMessage);
                            } else {
                                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                            }
                        }
                    } else if (response.m_istatus == -1) {//被封号
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            showBeenCloseDialog(message);
                        } else {
                            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                        }
                    } else if (response.m_istatus == -200) {//7天内已经登陆过其他账号
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.seven_days);
                    } else {
                        if (!TextUtils.isEmpty(response.m_strMessage)) {
                            ToastUtil.INSTANCE.showToast(getApplicationContext(), response.m_strMessage);
                        } else {
                            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                        }
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dismissLoadingDialog();
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 被封号
     */
    private void showVerifyDialog() {
        String phone = mMobileEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.phone_number_null);
            return;
        }
        if (!VerifyUtils.isPhoneNum(phone)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.wrong_phone_number);
            return;
        }

        final Dialog mDialog = new Dialog(PhoneLoginActivity.this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(PhoneLoginActivity.this).inflate(R.layout.dialog_sms_verify_layout, null);
        setVerifyDialogView(view, mDialog, phone);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置查看微信号提醒view
     */
    private void setVerifyDialogView(View view, final Dialog mDialog, final String phone) {
        //关闭
        ImageView cancel_iv = view.findViewById(R.id.cancel_iv);
        cancel_iv.setOnClickListener(v -> mDialog.dismiss());
        //图片
        final ImageView code_iv = view.findViewById(R.id.code_iv);
        final String url = ChatApi.GET_VERIFY() + phone;
        //加载
        Glide.with(PhoneLoginActivity.this).load(url)
//                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(code_iv);
        //点击换一张
        TextView change_tv = view.findViewById(R.id.change_tv);
        change_tv.setOnClickListener(v ->
                Glide.with(PhoneLoginActivity.this).load(url)
//              .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(code_iv));
        //验证码
        final EditText code_et = view.findViewById(R.id.code_et);
        //确认
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(v -> {
            String code = code_et.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_image_code);
                return;
            }
            checkVerifyCode(code, phone);
            mDialog.dismiss();
        });
    }

    /**
     * 验证图形验证码是否正确
     */
    private void checkVerifyCode(final String code, String phone) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phone", phone);
        paramMap.put("verifyCode", code);
        OkHttpUtils.post().url(ChatApi.GET_VERIFY_CODE_IS_CORRECT())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    sendSmsVerifyCode(code);
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.wrong_image_code);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.wrong_image_code);
            }
        });
    }

    /**
     * 被封号
     */
    private void showBeenCloseDialog(String des) {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_been_close_layout, null);
        setDialogView(view, mDialog, des);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置查看微信号提醒view
     */
    private void setDialogView(View view, final Dialog mDialog, String des) {
        //描述
        TextView see_des_tv = view.findViewById(R.id.des_tv);
        see_des_tv.setText(des);
        //取消
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //查看规则
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, getResources().getString(R.string.agree_detail));
                intent.putExtra(Constant.URL, "file:///android_asset/agree.html");
                startActivity(intent);
                mDialog.dismiss();
            }
        });
    }

}
