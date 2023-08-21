package com.lovechatapp.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.ChatUserInfo;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
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
 * 功能描述：选择性别页面
 * 作者：
 * 创建时间：2018/6/22
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ChooseGenderActivity extends BaseActivity {

//    @BindView(R.id.code_ll)
//    View codeLl;

    @BindView(R.id.girl_iv)
    ImageView mGirlIv;

//    @BindView(R.id.code_et)
//    EditText codeEt;

    @BindView(R.id.boy_iv)
    ImageView mBoyIv;

    private final int BOY = 1;
    private final int GIRL = 0;
    private int mSelectGender = -1;

    private ChatUserInfo chatUserInfo;

    public static void start(Context context, ChatUserInfo chatUserInfo) {
        Intent starter = new Intent(context, ChooseGenderActivity.class);
        starter.putExtra("data", chatUserInfo);
        context.startActivity(starter);
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_choose_gender_layout);
    }

    @Override
    protected void onContentAdded() {

        chatUserInfo = (ChatUserInfo) getIntent().getSerializableExtra("data");

        setTitle(R.string.choose_gender);
        setRightText(R.string.confirm);
        setBackVisibility(View.GONE);
//        codeLl.setVisibility(View.GONE);
//        getReferee();
    }

    @OnClick({R.id.boy_iv, R.id.girl_iv, R.id.right_text})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.boy_iv: {//男
                setGenderSelect(BOY);
                break;
            }
            case R.id.girl_iv: {//女
                setGenderSelect(GIRL);
                break;
            }
            case R.id.right_text: {//确定
                chooseGender();
                break;
            }
        }
    }

    /**
     * 设置选中
     */
    private void setGenderSelect(int position) {
        if (position == BOY) {
            if (mBoyIv.isSelected()) {
                return;
            }
            mSelectGender = BOY;
            mBoyIv.setSelected(true);
            mBoyIv.setImageResource(R.drawable.boy);
            mGirlIv.setSelected(false);
            mGirlIv.setImageResource(R.drawable.gril_not);
        } else if (position == GIRL) {
            if (mGirlIv.isSelected()) {
                return;
            }
            mSelectGender = GIRL;
            mGirlIv.setSelected(true);
            mGirlIv.setImageResource(R.drawable.girl);
            mBoyIv.setSelected(false);
            mBoyIv.setImageResource(R.drawable.boy_not);
        }
    }

    /**
     * 获取用户是否绑定推荐人
     */
    private void getReferee() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.getReferee())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (!isFinishing() && response != null && response.m_istatus == -1) {
//                    codeLl.setVisibility(View.VISIBLE);
                }
            }

        });
    }

    /**
     * 选择性别
     */
    private void chooseGender() {
        if (mSelectGender == -1) {
            ToastUtil.INSTANCE.showToast("请选择性别");
            return;
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("sex", String.valueOf(mSelectGender));
//        if (!TextUtils.isEmpty(codeEt.getText().toString().trim())) {
//            paramMap.put("id_card", codeEt.getText().toString().trim());
//        }
        OkHttpUtils.post().url(ChatApi.UPDATE_USER_SEX())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
            @Override
            public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
                if (isFinishing()) {
                    return;
                }
                dismissLoadingDialog();
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.choose_success);
                        if (AppManager.getInstance().getUserInfo() != null) {
                            AppManager.getInstance().getUserInfo().t_sex = mSelectGender;
                        }
                        SharedPreferenceHelper.saveGenderInfo(getApplicationContext(), mSelectGender);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtil.INSTANCE.showToast(response.m_strMessage);
                    }
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (isFinishing()) {
                    return;
                }
                super.onError(call, e, id);
                dismissLoadingDialog();
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }
}