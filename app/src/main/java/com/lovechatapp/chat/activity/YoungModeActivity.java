package com.lovechatapp.chat.activity;

import android.text.TextUtils;
import android.view.View;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.UserCenterBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.OnClick;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：未成年模式引导页
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class YoungModeActivity extends BaseActivity {

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_young_mode_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.young_mode);
        mHeadLineV.setVisibility(View.INVISIBLE);
    }

    @OnClick({R.id.open_tv})
    public void onClick(View v) {
        //开启
        if (v.getId() == R.id.open_tv) {
            getInfo();
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
                        if (TextUtils.isEmpty(bean.t_phone)) {//如果没绑定
                            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.bind_phone);
                            int needJumpToYoungMode = 1;
                            PhoneVerifyActivity.startPhoneVerifyActivity(YoungModeActivity.this, needJumpToYoungMode);
                        } else {
                            YoungModePasswordActivity.startYoungPasswordActivity(
                                    YoungModeActivity.this, false);
                        }
                        finish();
                    } else {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

}
