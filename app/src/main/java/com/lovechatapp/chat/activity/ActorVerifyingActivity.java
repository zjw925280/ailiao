package com.lovechatapp.chat.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   主播审核中
 * 作者：
 * 创建时间：2018/11/2
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActorVerifyingActivity extends BaseActivity {

    @BindView(R.id.we_chat_tv)
    TextView mWeChatTv;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_actor_verifying_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.actor_verifying);
        initStart();
        getWeChatNumber();
    }

    /**
     * 初始化
     */
    private void initStart() {
        mWeChatTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //获取剪贴板管理器
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", mWeChatTv.getText().toString().trim());
                // 将ClipData内容放到系统剪贴板里。
                if (cm != null) {
                    cm.setPrimaryClip(mClipData);
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.copy_success);
                }
                return false;
            }
        });
    }

    /**
     * 获取微信号
     */
    private void getWeChatNumber() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_IDENTIFICATION_WEI_XIN())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<String>>() {
            @Override
            public void onResponse(BaseResponse<String> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String m_object = response.m_object;
                    if (!TextUtils.isEmpty(m_object)) {
                        mWeChatTv.setText(m_object);
                    }
                }
            }
        });
    }

    @OnClick({R.id.jump_now_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.jump_now_tv: {//跳转
                finish();
                break;
            }
        }
    }

}