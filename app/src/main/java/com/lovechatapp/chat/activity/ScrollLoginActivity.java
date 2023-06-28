package com.lovechatapp.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
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
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.OnClick;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：背景长图滑动登录页面
 * 作者：
 * 创建时间：2018/6/22
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ScrollLoginActivity extends BaseActivity {

    private Tencent mTencent;
    private IWXAPI mWxApi;

    //接收关闭activity广播
    private MyLoginBroadcastReceiver mMyBroadcastReceiver;
    private Dialog mBeenCloseDialog;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_scroll_login_layout);
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        initStart();

        /*
         * 每次登录提示自动脚本发消息
         */
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit().putBoolean("AlertAutoMsg", true).apply();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            saveClipShareUserId();
        }
    }

    /**
     * 初始化QQ 微信
     */
    private void initStart() {
        mWxApi = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID, true);
        mWxApi.registerApp(Constant.WE_CHAT_APPID);
        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, getApplicationContext());

        mMyBroadcastReceiver = new MyLoginBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.FINISH_LOGIN_PAGE);
        filter.addAction(Constant.BEEN_CLOSE_LOGIN_PAGE);
        registerReceiver(mMyBroadcastReceiver, filter);

        boolean beenClose = getIntent().getBooleanExtra(Constant.BEEN_CLOSE, false);
        if (beenClose) {
            String beenCloseDes = getIntent().getStringExtra(Constant.BEEN_CLOSE_DES);
            if (!TextUtils.isEmpty(beenCloseDes)) {
                showBeenCloseDialog(beenCloseDes);
            } else {
                String des = getResources().getString(R.string.been_suspend);
                showBeenCloseDialog(des);
            }
        }
    }

    private boolean checkAgree() {
        CheckBox checkBox = findViewById(R.id.agree_cb);
        if (!checkBox.isChecked()) {
            ToastUtil.INSTANCE.showToast("请阅读并同意《用户协议》和《隐私政策》");
            return true;
        }
        return false;
    }

    @OnClick({R.id.phone_tv, R.id.agree_tv, R.id.qq_tv, R.id.we_chat_tv, R.id.private_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.agree_tv: {//用户协议
                Intent intent = new Intent(getApplicationContext(), CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, getString(R.string.agree_detail));
                intent.putExtra(Constant.URL, "file:///android_asset/agree.html");
                startActivity(intent);
                break;
            }
            case R.id.private_tv: {//隐私协议
                Intent intent = new Intent(getApplicationContext(), CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, getString(R.string.private_detail));
                intent.putExtra(Constant.URL, "file:///android_asset/private.html");
                startActivity(intent);
                break;
            }
            case R.id.phone_tv: {//手机号
                if (checkAgree()) {
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), PhoneLoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.qq_tv: {//QQ
                if (checkAgree()) {
                    return;
                }
                loginQQ();
                break;
            }
            case R.id.we_chat_tv: {
                if (checkAgree()) {
                    return;
                }
                loginToWeiXin();
                break;
            }
        }
    }

    /**
     * 调用自己的api 进行qq登录
     */
    private void loginQQWay(com.alibaba.fastjson.JSONObject jsonObject, String ip) {
        String openId = mTencent.getOpenId();
        if (TextUtils.isEmpty(openId)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.qq_fail);
            return;
        }
        showLoadingDialog();
        final String nickName = jsonObject.getString("nickname");
        String handImg = jsonObject.getString("figureurl_qq_2");
        if (TextUtils.isEmpty(handImg)) {
            handImg = jsonObject.getString("figureurl_2");
        }
        final String headImg = handImg;
        String city = jsonObject.getString("city");
        //用于师徒
        String t_system_version = "Android " + SystemUtil.getSystemVersion();
        String deviceNumber = SystemUtil.getOnlyOneId(getApplicationContext());

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("openId", TextUtils.isEmpty(openId) ? "" : openId);
        paramMap.put("nickName", TextUtils.isEmpty(nickName) ? "" : nickName);
        paramMap.put("handImg", TextUtils.isEmpty(handImg) ? "" : handImg);
        paramMap.put("city", TextUtils.isEmpty(city) ? "" : city);
        paramMap.put("t_phone_type", "Android");
        paramMap.put("t_system_version", TextUtils.isEmpty(t_system_version) ? "" : t_system_version);
        paramMap.put("deviceNumber", deviceNumber);
        paramMap.put("ip", ip);
        String channelId = AppManager.getInstance().getShareId();
        if (TextUtils.isEmpty(channelId)) {
            channelId = CodeUtil.getClipBoardContent(getApplicationContext());
        }
        paramMap.put("shareUserId", channelId);
        OkHttpUtils.post().url(ChatApi.QQ_LOGIN())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
            @Override
            public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
                dismissLoadingDialog();
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        CodeUtil.clearClipBoard(getApplicationContext());
                        ChatUserInfo userInfo = response.m_object;
                        if (userInfo != null) {
                            userInfo.t_nickName = nickName;
                            userInfo.headUrl = headImg;
                            AppManager.getInstance().setUserInfo(userInfo);
                            SharedPreferenceHelper.saveAccountInfo(getApplicationContext(), userInfo);
                            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_success);

                            if (userInfo.t_sex == 2) {//没有性别
                                Intent intent = new Intent(getApplicationContext(), ChooseGenderActivity.class);
                                intent.putExtra(Constant.NICK_NAME, nickName);
                                intent.putExtra(Constant.MINE_HEAD_URL, headImg);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
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
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dismissLoadingDialog();
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
            }
        });
    }

    class MyLoginBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equals(Constant.BEEN_CLOSE_LOGIN_PAGE)) {//被封号
                String message = intent.getStringExtra(Constant.BEEN_CLOSE);
                showBeenCloseDialog(message);
            } else {
                finish();
            }
        }
    }

    /**
     * 被封号
     */
    private void showBeenCloseDialog(String des) {
        mBeenCloseDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_been_close_layout, null);
        setDialogView(view, mBeenCloseDialog, des);
        mBeenCloseDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mBeenCloseDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mBeenCloseDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing() && !mBeenCloseDialog.isShowing()) {
            mBeenCloseDialog.show();
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

    //------------------------QQ第三方登录--------------------
    //第一步  QQ登录
    private void loginQQ() {
        if (!mTencent.isSessionValid()) {//判断是否登陆过
            mTencent.login(this, "all", new BaseUiListener());
        } else {//登陆过注销之后在登录
            mTencent.logout(this);
            mTencent.login(this, "all", new BaseUiListener());
        }
    }

    private class BaseUiListener implements IUiListener {
        public void onComplete(Object response) {
            //第二步 登陆成功后获取 token openid
            try {
                String openidString = ((JSONObject) response).getString("openid");
                mTencent.setOpenId(openidString);
                mTencent.setAccessToken(((JSONObject) response).getString("access_token"), ((JSONObject) response).getString("expires_in"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //第三步 获取用户信息
            QQToken qqToken = mTencent.getQQToken();
            UserInfo info = new UserInfo(getApplicationContext(), qqToken);
            info.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    try {
                        String json = o.toString();
                        if (!TextUtils.isEmpty(json)) {
                            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(o.toString());
                            getQQWayRealIp(jsonObject);
                        } else {
                            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                    }
                }

                @Override
                public void onError(UiError uiError) {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                }

                @Override
                public void onCancel() {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_cancel);
                }
            });

        }

        @Override
        public void onError(UiError uiError) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.qq_login_fail);
        }

        @Override
        public void onCancel() {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_cancel);
        }
    }

    /**
     * 获取QQ登录方式真实ip
     */
    private void getQQWayRealIp(final com.alibaba.fastjson.JSONObject object) {
        OkHttpUtils.get().url(ChatApi.GET_REAL_IP())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                loginQQWay(object, "0.0.0.0");
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response) && response.contains("{") && response.contains("}")) {
                    try {
                        int startIndex = response.indexOf("{");
                        int endIndex = response.indexOf("}");
                        String content = response.substring(startIndex, endIndex + 1);
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(content);
                        String cip = jsonObject.getString("cip");
                        if (!TextUtils.isEmpty(cip)) {
                            loginQQWay(object, cip);
                        } else {
                            loginQQWay(object, "0.0.0.0");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        loginQQWay(object, "0.0.0.0");
                    }
                } else {
                    loginQQWay(object, "0.0.0.0");
                }
            }
        });
    }

    //-----------------------QQ第三方登录结束------------------

    //------------------------微信第三方登录-------------------
    private void loginToWeiXin() {
        if (mWxApi != null && mWxApi.isWXAppInstalled()) {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_微信登录";
            boolean result = mWxApi.sendReq(req);
            if (result) {
                AppManager.getInstance().setIsWeChatBindAccount(false);
            }
        } else {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.not_install_we_chat);
        }
    }

    //------------------------微信第三方登录结束---------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, new BaseUiListener());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBeenCloseDialog != null) {
            mBeenCloseDialog.dismiss();
            mBeenCloseDialog = null;
        }
        if (mMyBroadcastReceiver != null) {
            unregisterReceiver(mMyBroadcastReceiver);
        }
    }

    /**
     * 获取粘贴板中分享userId
     * 如果有就存在本地
     * 登录的时候再取
     */
    private void saveClipShareUserId() {
        String clipUserId = CodeUtil.getClipBoardContentOne(getApplicationContext());
        LogUtil.d("==-", clipUserId);
        if (!TextUtils.isEmpty(clipUserId) && !clipUserId.equals("0")) {
            SharedPreferenceHelper.saveShareId(getApplicationContext(), clipUserId);
        }
    }
}