package com.lovechatapp.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.gyf.barlibrary.ImmersionBar;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.bean.ChatUserInfo;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.dialog.AgreementDialog;
import com.lovechatapp.chat.helper.IMHelper;
import com.lovechatapp.chat.helper.RingVibrateManager;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.util.FinishActivityManager;
import com.lovechatapp.chat.util.LogUtil;
import com.tencent.mm.opensdk.utils.Log;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：启动页面
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SplashActivity extends Activity {

    private ChatUserInfo chatUserInfo;
    private AgreementDialog agreementDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppManager.getInstance().getActivityObserve().isActive()) {
            finish();
            return;
        }

        chatUserInfo = SharedPreferenceHelper.getAccountInfo(AppManager.getInstance());

        ImmersionBar.with(this).statusBarDarkFont(true).navigationBarColor(R.color.black).init();

        setContentView(R.layout.activity_splash_layout);

        getRealIp();
    }
    /**
     * 获取真实ip
     */
    private void getRealIp() {
        OkHttpUtils.get().url(ChatApi.GET_REAL_IP())
                .build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("这是cityWX真实IP: " + response);
                        String replace = response.replace("ipCallback({ip:\"", "");
                        String cip= replace.replace("\"})", "");
                        getCity(cip);
                    }
                });
    }

    /**
     * 获取真实ip
     */
    private void getCity(String string) {
        OkHttpUtils.get().url(ChatApi.GET_CITY(string))
                .build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        SharedPreferenceHelper.saveCity(AppManager.getInstance(), "");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("这是city城市json: " + response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("result");
                            JSONObject jsonObject1 = new JSONObject(result);
                            String ad_info = jsonObject1.getString("ad_info");
                            JSONObject jsonObject2 = new JSONObject(ad_info);
                            String city = jsonObject2.getString("city");
                            LogUtil.i("这是city城市信息: " + city+" ip="+string);
                            SharedPreferenceHelper.saveCity(AppManager.getInstance(), city);
                        } catch (JSONException e) {
                            SharedPreferenceHelper.saveCity(AppManager.getInstance(), "");
                            throw new RuntimeException(e);
                        }

                    }
                });

        new Handler().postDelayed(() -> {
            if (isFinishing()) {
                return;
            }
            toIntent();
        }, 2000);
        if (chatUserInfo.t_id > 0) {
            IMHelper.checkLogin();
            AppManager.getInstance().refreshMyInfo();
        }
        RingVibrateManager.syncSwitch();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    private void toIntent() {
        boolean finish = true;
        if (chatUserInfo.t_id > 0) {
            if (chatUserInfo.t_sex < 2) {//还没有选择性别
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                startActivity(new Intent(getApplicationContext(), ChooseGenderActivity.class));
            }
        } else {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            if (sp.getBoolean("agree", false)) {
                startActivity(new Intent(getApplicationContext(), ScrollLoginActivity.class));
            } else {
                finish = false;
                agreementDialog = new AgreementDialog(this);
                agreementDialog.show();
            }
        }
        if (finish) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}