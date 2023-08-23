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
import com.lovechatapp.chat.dialog.AgreementDialog;
import com.lovechatapp.chat.helper.IMHelper;
import com.lovechatapp.chat.helper.RingVibrateManager;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.util.FinishActivityManager;
import com.tencent.mm.opensdk.utils.Log;

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



//        OkHttpUtils.post()
//                .url(ChatApi.getProtectAppVersion())
//                .build()
//                .execute(new AjaxCallback<BaseResponse<UrlResponse>>() {
//
//                    @Override
//                    public void onResponse(BaseResponse<UrlResponse> response, int id) {
//                        if (response != null && response.m_object != null) {
//                            UrlResponse urlResponse = response.m_object;
//                            if (urlResponse.request_status == 1) {
//                                setUrl(urlResponse);
//                                saveUrl(urlResponse);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        String localUrlData = PreferenceManager.getDefaultSharedPreferences(AppManager.getInstance())
//                                .getString("local_url_data", null);
//                        if (!TextUtils.isEmpty(localUrlData)) {
//                            try {
//                                UrlResponse urlResponse = JSON.parseObject(localUrlData, UrlResponse.class);
//                                setUrl(urlResponse);
//                            } catch (Exception e2) {
//                                e2.printStackTrace();
//                            }
//                        }
//                    }
//
//                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FinishActivityManager.getManager().addActivity(this);
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

    private void toIntent() {
        boolean finish = true;
        if (chatUserInfo.t_id > 0) {
            if (chatUserInfo.t_sex == 2) {//还没有选择性别
                startActivity(new Intent(getApplicationContext(), ChooseGenderActivity.class));
            } else {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        } else {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            if (sp.getBoolean("agree", false)) {
                startActivity(new Intent(getApplicationContext(), ScrollLoginActivity.class));
            } else {
                finish = false;
                new AgreementDialog(this).show();
            }
        }
        if (finish) {
            finish();
        }
    }

}