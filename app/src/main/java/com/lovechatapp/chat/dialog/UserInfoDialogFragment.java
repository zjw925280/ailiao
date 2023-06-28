package com.lovechatapp.chat.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.ReportActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.BigUserBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：大房间头像信息弹窗
 * 作者：
 * 创建时间：2019/3/5
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UserInfoDialogFragment extends DialogFragment {

    public UserInfoDialogFragment() {

    }

    //头像
    private ImageView mHeadIv;
    //昵称
    private TextView mNickTv;
    //年龄
    private TextView mAgeTv;
    //职业
    private TextView mJobTv;
    //ID
    private TextView mIdCardTv;
    //城市
    private TextView mLocationTv;
    //签名
    private TextView mSignTv;
    //粉丝
    private LinearLayout mFanLl;
    private TextView mFanTv;
    //礼物
    private LinearLayout mGiftLl;
    private TextView mGiftTv;
    private TextView mGiftDesTv;

    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        View view = inflater.inflate(R.layout.dialog_user_info_layout, container, false);
        initView(view);
        return view;
    }

    /**
     * 初始化
     */
    private void initView(View view) {
        mHeadIv = view.findViewById(R.id.head_iv);
        mNickTv = view.findViewById(R.id.nick_tv);
        mAgeTv = view.findViewById(R.id.age_tv);
        mJobTv = view.findViewById(R.id.job_tv);
        mIdCardTv = view.findViewById(R.id.id_card_tv);
        mLocationTv = view.findViewById(R.id.location_tv);
        mSignTv = view.findViewById(R.id.sign_tv);
        mFanLl = view.findViewById(R.id.fan_ll);
        mFanTv = view.findViewById(R.id.fan_tv);
        mGiftLl = view.findViewById(R.id.gift_ll);
        mGiftTv = view.findViewById(R.id.gift_tv);
        mGiftDesTv = view.findViewById(R.id.gift_des_tv);
        TextView report_tv = view.findViewById(R.id.report_tv);

        Bundle bundle = getArguments();
        if (bundle != null) {
            final int mActorId = bundle.getInt(Constant.ACTOR_ID);
            if (mActorId > 0) {
                getUserInfo(mActorId);
                report_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ReportActivity.class);
                        intent.putExtra(Constant.ACTOR_ID, mActorId);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            // 一定要设置Background，如果不设置，window属性设置无效
            window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
            DisplayMetrics dm = new DisplayMetrics();
            if (getActivity() != null) {
                WindowManager windowManager = getActivity().getWindowManager();
                if (windowManager != null) {
                    windowManager.getDefaultDisplay().getMetrics(dm);
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.gravity = Gravity.BOTTOM;
                    // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    window.setAttributes(params);
                }
            }
        }
    }

    /**
     * 获取信息
     */
    private void getUserInfo(final int userId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", String.valueOf(userId));
        OkHttpUtils.post().url(ChatApi.GET_USER_INDEX_DATA())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<BigUserBean>>() {
            @Override
            public void onResponse(BaseResponse<BigUserBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    BigUserBean userBean = response.m_object;
                    if (userBean != null) {
                        //头像
                        String headImg = userBean.t_handImg;
                        if (!TextUtils.isEmpty(headImg)) {
                            ImageLoadHelper.glideShowCircleImageWithUrl(getActivity(), headImg, mHeadIv);
                        } else {
                            mHeadIv.setImageResource(R.drawable.default_head_img);
                        }
                        //昵称
                        mNickTv.setText(userBean.t_nickName);
                        //年龄
                        if (userBean.t_age > 0) {
                            mAgeTv.setText(String.valueOf(userBean.t_age));
                            mAgeTv.setVisibility(View.VISIBLE);
                        }
                        //职业
                        if (!TextUtils.isEmpty(userBean.t_vocation)) {
                            mJobTv.setText(userBean.t_vocation);
                            mJobTv.setVisibility(View.VISIBLE);
                        }
                        //ID
                        String content = mContext.getResources().getString(R.string.chat_number_one) + userBean.t_idcard;
                        mIdCardTv.setText(content);
                        //城市
                        mLocationTv.setText(userBean.t_city);
                        //个性签名
                        String sign = userBean.t_autograph;
                        if (!TextUtils.isEmpty(sign)) {
                            mSignTv.setText(sign);
                        } else {
                            mSignTv.setText(mContext.getResources().getString(R.string.lazy));
                        }
                        //关注数,就是粉丝
                        if (userBean.followCount >= 0) {
                            mFanTv.setText(String.valueOf(userBean.followCount));
                            mFanLl.setVisibility(View.VISIBLE);
                        }
                        //贡献值  粉丝  1 主播 0 用户
                        if (userBean.t_role == 1) {
                            mGiftDesTv.setText(getText(R.string.gift));
                        } else {
                            mGiftDesTv.setText(getText(R.string.send_gift));
                        }
                        mGiftTv.setText(String.valueOf(userBean.totalMoney));
                        mGiftLl.setVisibility(View.VISIBLE);

                    }
                }
            }
        });
    }


}
