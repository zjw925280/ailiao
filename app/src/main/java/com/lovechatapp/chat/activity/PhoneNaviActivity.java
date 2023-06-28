package com.lovechatapp.chat.activity;

import android.view.View;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;

import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：手机指南页面
 * 作者：
 * 创建时间：2018/11/13
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PhoneNaviActivity extends BaseActivity {

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_phone_navi_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.phone_navi);

    }

    @OnClick({
            R.id.phone_vivo,
            R.id.phone_huawei,
            R.id.phone_xiaomi,
            R.id.phone_oppo
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone_vivo: {//vivo
                CommonWebViewActivity.start(mContext, "VIVO手机设置指南", "file:///android_asset/guide/vivo_setting.png");
                break;
            }
            case R.id.phone_huawei: {//华为
                CommonWebViewActivity.start(mContext, "华为手机设置指南", "file:///android_asset/guide/huawei_setting.png");
                break;
            }
            case R.id.phone_xiaomi: {//小米
                CommonWebViewActivity.start(mContext, "小米手机设置指南", "file:///android_asset/guide/xiaomi_setting.png");
                break;
            }
            case R.id.phone_oppo: {//oppo
                CommonWebViewActivity.start(mContext, "OPPO手机设置指南", "file:///android_asset/guide/oppo_setting.png");
                break;
            }
        }
    }

}