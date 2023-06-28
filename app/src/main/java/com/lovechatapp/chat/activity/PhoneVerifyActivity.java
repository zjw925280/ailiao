package com.lovechatapp.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.lovechatapp.chat.bean.UserCenterBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.util.VerifyUtils;
import com.zhy.http.okhttp.OkHttpUtils;

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
 * 功能描述：手机号码验证页面
 * 作者：
 * 创建时间：2018/7/23
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PhoneVerifyActivity extends BaseActivity {

    @BindView(R.id.get_tv)
    TextView mSendVerifyTv;//发送验证码

    @BindView(R.id.mobile_et)
    EditText mMobileEt;

    @BindView(R.id.code_et)
    EditText mCodeEt;

    @BindView(R.id.phone_tv)
    TextView phoneTv;

    private CountDownTimer mCountDownTimer;
    private static final String FROM_TYPE = "from_type";
    private final int NEED_JUMP_TO_YOUNG_MODE = 1;
    private final int FROM_FORGET_PASSWORD = 2;
    private int mFromType;

    private boolean isForce;

    public static void startPhoneVerifyActivity(Context context, int fromType) {
        Intent intent = new Intent(context, PhoneVerifyActivity.class);
        intent.putExtra(FROM_TYPE, fromType);
        context.startActivity(intent);
    }

    public static void start(Context context, String phone) {
        Intent starter = new Intent(context, PhoneVerifyActivity.class);
        starter.putExtra(FROM_TYPE, 0);
        starter.putExtra("phone", phone);
        context.startActivity(starter);
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_phone_verify_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.phone_verify);
        initStart();
    }

    /**
     * 初始化开始
     */
    private void initStart() {
        int FROM_MODIFY = 0;
        mFromType = getIntent().getIntExtra(FROM_TYPE, FROM_MODIFY);
        if (mFromType == FROM_FORGET_PASSWORD) {//青少年模式 忘记密码
            //获取绑定的手机号
            getInfo();
        }
        isForce = getIntent().getBooleanExtra("force", false);
        if (isForce) {
            setTitle("请绑定手机号");
            setBackVisibility(View.GONE);
        }

        String phone = getIntent().getStringExtra("phone");
        if (!TextUtils.isEmpty(phone) && phone.length() == 11) {
            phoneTv.setText(String.format("* 当前已绑定手机号：%s\n* 如需更换手机号请重新验证", phone));
            phoneTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (!isForce) {
            super.onBackPressed();
        }
    }

    /**
     * 获取个人中心信息
     */
    private void getInfo() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.INDEX())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<UserCenterBean>>() {
                    @Override
                    public void onResponse(BaseResponse<UserCenterBean> response, int id) {
                        if (response != null && response.m_istatus == NetCode.SUCCESS) {
                            UserCenterBean bean = response.m_object;
                            if (bean != null) {
                                if (!TextUtils.isEmpty(bean.t_phone)) {
                                    mMobileEt.setText(bean.t_phone);
                                    mMobileEt.setEnabled(false);
                                }
                            }
                        }
                    }
                });
    }

    @OnClick({R.id.get_tv, R.id.finish_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_tv: {
                showVerifyDialog();
                break;
            }
            case R.id.finish_tv: {
                finishVerify();
                break;
            }
        }
    }

    /**
     * 完成验证
     */
    private void finishVerify() {
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

        String url;
        if (mFromType == FROM_FORGET_PASSWORD) {
            url = ChatApi.GET_PHONE_SMS_STATUS();
        } else {
            url = ChatApi.UPDATE_PHONE();
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("phone", phone);
        paramMap.put("smsCode", verifyCode);
        OkHttpUtils.post().url(url)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response, int id) {
                        LogUtil.i("修改手机号==--", JSON.toJSONString(response));
                        dismissLoadingDialog();
                        if (response != null) {
                            AppManager.getInstance().getUserInfo().phone = phone;
                            if (response.m_istatus == NetCode.SUCCESS) {
                                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.verify_good);
                                if (mFromType == NEED_JUMP_TO_YOUNG_MODE) {//需要绑定成功后跳转到青少年模式
                                    YoungModePasswordActivity.startYoungPasswordActivity(
                                            PhoneVerifyActivity.this, false);
                                } else if (mFromType == FROM_FORGET_PASSWORD) {//忘记密码
                                    //清空密码
                                    SharedPreferenceHelper.setYoungPassword(getApplicationContext(), "");
                                } else {
                                    Intent intent = new Intent();
                                    intent.putExtra(Constant.PHONE_MODIFY, phone);
                                    setResult(RESULT_OK, intent);
                                }
                                if (isForce) {
                                    ChatUserInfo chatUserInfo = AppManager.getInstance().getUserInfo();
                                    chatUserInfo.t_phone_status = 1;
                                    SharedPreferenceHelper.saveAccountInfo(mContext, chatUserInfo);
                                }
                                finish();
                            } else {
                                String message = response.m_strMessage;
                                if (!TextUtils.isEmpty(message)) {
                                    ToastUtil.INSTANCE.showToast(getApplicationContext(), message);
                                } else {
                                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.verify_fail);
                                }
                            }
                        } else {
                            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.verify_fail);
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

        final Dialog mDialog = new Dialog(PhoneVerifyActivity.this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(PhoneVerifyActivity.this).inflate(R.layout.dialog_sms_verify_layout, null);
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
        cancel_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //图片
        final ImageView code_iv = view.findViewById(R.id.code_iv);
        final String url = ChatApi.GET_VERIFY() + phone;
        //加载
        Glide.with(PhoneVerifyActivity.this).load(url)
//                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(code_iv);
        //点击换一张
        TextView change_tv = view.findViewById(R.id.change_tv);
        change_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(PhoneVerifyActivity.this).load(url)
//                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(code_iv);
            }
        });
        //验证码
        final EditText code_et = view.findViewById(R.id.code_et);
        //确认
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = code_et.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_image_code);
                    return;
                }
                checkVerifyCode(code, phone);
                mDialog.dismiss();
            }
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

        String resType = "2";
        if (mFromType == FROM_FORGET_PASSWORD) {
            resType = "3";
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phone", phone);
        paramMap.put("resType", resType);
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
        mSendVerifyTv.setTextColor(getResources().getColor(R.color.white));
        mSendVerifyTv.setBackgroundResource(R.drawable.shape_send_verify_text_gray_background);
        mCountDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                String text = getResources().getString(R.string.re_send) + l / 1000 + getResources().getString(R.string.second);
                mSendVerifyTv.setText(text);
            }

            @Override
            public void onFinish() {
                mSendVerifyTv.setClickable(true);
                mSendVerifyTv.setTextColor(getResources().getColor(R.color.pink_main));
                mSendVerifyTv.setBackgroundColor(getResources().getColor(R.color.transparent));
                mSendVerifyTv.setText(R.string.get_code);
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                    mCountDownTimer = null;
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

}
