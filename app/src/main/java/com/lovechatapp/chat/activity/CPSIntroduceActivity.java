package com.lovechatapp.chat.activity;

import android.content.Intent;
import android.view.View;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;

import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：CPS介绍页面
 * 作者：
 * 创建时间：2018/9/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CPSIntroduceActivity extends BaseActivity {

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_cps_introduce_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.cps_intro);
    }

    @OnClick({R.id.apply_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apply_tv: {
                Intent intent = new Intent(getApplicationContext(), ApplyCPSActivity.class);
                startActivity(intent);
                finish();
                break;
            }
        }
    }

}