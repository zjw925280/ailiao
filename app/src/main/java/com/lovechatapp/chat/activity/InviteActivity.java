package com.lovechatapp.chat.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.InviteBean;
import com.lovechatapp.chat.bean.InviteRewardBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.dialog.ShareDialog;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.share.ShareCopyUrl;
import com.lovechatapp.chat.util.share.ShareWechatCircle;
import com.lovechatapp.chat.util.share.ShareWechatGraphic;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Copyright (C)
 * 版权所有
 *
 * 功能描述：邀请好友新页面
 * 作者：
 * 创建时间：
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class InviteActivity extends BaseActivity {

    @BindView(R.id.rule_des_tv)
    TextView mRuleDesTv;

    @BindView(R.id.retain_gold_tv)
    TextView mRetainGoldTv;

    @BindView(R.id.retain_friend_tv)
    TextView mRetainFriendTv;

    @BindView(R.id.ivReceive)
    ImageView ivReceive;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_invite_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle("邀请好友");
        getShareInfo();
        getSpreadAward();
//        startComboAnim(ivReceive);
    }

    /**
     * 获取推广赚钱信息
     */
    private void getShareInfo() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_SHARE_TOTAL())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<InviteBean>>() {
            @Override
            public void onResponse(BaseResponse<InviteBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    InviteBean shareBean = response.m_object;
                    if (shareBean != null) {

                        //总收益
                        mRetainGoldTv.setText(String.valueOf(shareBean.profitTotal));

                        //总人数
                        int total = shareBean.oneSpreadCount + shareBean.twoSpreadCount;
                        mRetainFriendTv.setText(String.valueOf(total));
                    }
                }
            }
        });
    }

    /**
     * 获取推广奖励规则
     */
    private void getSpreadAward() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post()
                .url(ChatApi.GET_SPREAD_AWARD())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build()
                .execute(new AjaxCallback<BaseResponse<InviteRewardBean>>() {
                    @Override
                    public void onResponse(BaseResponse<InviteRewardBean> response, int id) {
                        if (isFinishing()) {
                            return;
                        }
                        if (response != null && response.m_istatus == NetCode.SUCCESS) {
                            InviteRewardBean rewardBean = response.m_object;
                            if (rewardBean != null) {
                                mRuleDesTv.setText(rewardBean.t_award_rules);
                            }
                        }
                    }
                });
    }

    @OnClick({R.id.invite_tv,
            R.id.invite_ll,
            R.id.invite_detail_tv,
            R.id.ivReceive,
            R.id.go_btn})
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.invite_detail_tv:
            case R.id.invite_ll: {
                Intent intent = new Intent(InviteActivity.this, MyInviteActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.invite_tv: {
                new ShareDialog(mContext, Arrays.asList(
                        new ShareDialog.ShareInfo(R.drawable.share_we_chat, "微信好友", new ShareWechatGraphic()),
                        new ShareDialog.ShareInfo(R.drawable.share_we_circle, "微信朋友圈", new ShareWechatCircle()),
                       // new ShareDialog.ShareInfo(R.drawable.share_qq, "QQ", new ShareQQ()),
                        //new ShareDialog.ShareInfo(R.drawable.share_qq_zone, "QQ空间", new ShareQZone()),
                       // new ShareDialog.ShareInfo(R.drawable.share_poster, "分享海报", new SharePoster()),
                        new ShareDialog.ShareInfo(R.drawable.share_copy, "复制链接", new ShareCopyUrl())
                )).show();
                break;
            }

            case R.id.ivReceive:
            case R.id.go_btn: {
                Intent intent = new Intent(this, InviteRewardActivity.class);
                startActivity(intent);
            }

        }

    }

    private void startComboAnim(final ImageView giftNumView) {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(giftNumView, "scaleX", 1f, 1.3f);
        anim1.setRepeatCount(ValueAnimator.INFINITE);
        anim1.setRepeatMode(ValueAnimator.REVERSE);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(giftNumView, "scaleY", 1f, 1.3f);
        anim2.setRepeatCount(ValueAnimator.INFINITE);
        anim2.setRepeatMode(ValueAnimator.REVERSE);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(1200);
        animSet.playTogether(anim1, anim2);
        animSet.start();
    }

}