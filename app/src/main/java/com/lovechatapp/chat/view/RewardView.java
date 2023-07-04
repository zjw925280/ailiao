package com.lovechatapp.chat.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseBean;
import com.lovechatapp.chat.base.BaseListResponse;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.RankBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 排行榜奖励
 */
public class RewardView extends FrameLayout {

    private TextView myRankTv;
    private TextView rewardGoldTv;
    private TextView rewardStateTv;
    private BaseActivity baseActivity;
    private boolean isRequesting;

    public RewardView(@NonNull Context context) {
        super(context);
        init();
    }

    public RewardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RewardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.reward_layout, this);
        myRankTv = findViewById(R.id.my_rank_tv);
        rewardGoldTv = findViewById(R.id.reward_gold_tv);
        rewardStateTv = findViewById(R.id.get_reward_tv);
        setVisibility(View.GONE);
    }

    public void setActivity(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    public final void getData() {
        if (isRequesting) {
            return;
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        OkHttpUtils.post().url(ChatApi.getUserRankInfo())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<List<RewardInfo>>>() {
            @Override
            public void onResponse(BaseResponse<List<RewardInfo>> response, int id) {
                if (baseActivity == null || baseActivity.isFinishing())
                    return;
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    myRankReward(response.m_object);
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                isRequesting = true;
            }

            @Override
            public void onAfter(int id) {
                isRequesting = false;
            }

        });
    }

    /**
     * 我的奖励
     */
    private void myRankReward(List<RewardInfo> infoList) {

        if (infoList != null && infoList.size() > 0) {

            RewardInfo bean = infoList.get(0);

            setVisibility(View.VISIBLE);

            //我的排名
            myRankTv.setText(bean.t_title);
            myRankTv.append(String.format("排名: %s", bean.t_rank_sort));

            //奖励约豆数
            rewardGoldTv.setText(String.format("奖励: %s约豆", bean.t_rank_gold));

            //奖励领取状态
            rewardStateTv.setText("领取");
            rewardStateTv.setBackgroundResource(R.drawable.corner_purple_7948fb);
            rewardStateTv.setTextColor(0xffffffff);

            //领取奖励
            rewardStateTv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
                    paramMap.put("rankType", bean.t_rank_reward_type);
                    paramMap.put("queryType", bean.t_rank_time_type);
                    paramMap.put("rankRewardId", bean.rankRewardId);
                    OkHttpUtils.post().url(ChatApi.receiveRankGold())
                            .addParams("param", ParamUtil.getParam(paramMap))
                            .build().execute(new AjaxCallback<BaseListResponse<RankBean>>() {
                        @Override
                        public void onResponse(BaseListResponse<RankBean> response, int id) {
                            if (baseActivity == null || baseActivity.isFinishing())
                                return;
                            if (response != null) {
                                if (response.m_istatus == NetCode.SUCCESS) {
                                    ToastUtil.INSTANCE.showToast("领取成功");
                                    infoList.remove(bean);
                                    myRankReward(infoList);
                                } else {
                                    ToastUtil.INSTANCE.showToast(response.m_strMessage);
                                }
                            }
                        }

                        @Override
                        public void onBefore(Request request, int id) {
                            if (baseActivity == null || baseActivity.isFinishing())
                                return;
                            baseActivity.showLoadingDialog();
                        }

                        @Override
                        public void onAfter(int id) {
                            if (baseActivity == null || baseActivity.isFinishing())
                                return;
                            baseActivity.dismissLoadingDialog();
                        }

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ToastUtil.INSTANCE.showToast("领取失败");
                        }
                    });
                }

            });

        } else {
            setVisibility(View.GONE);
        }
    }

    /**
     * t_rank_reward_type 1:魅力 2:邀请
     * t_rank_time_type 1.日榜  2.周榜  3.月榜  4.总榜 5.昨日 6.上周 7.上月
     */
    private static class RewardInfo extends BaseBean {

        public int t_rank_reward_type;

        public int t_rank_time_type;

        public int rankRewardId;

        public int t_rank_sort_gold;

        public int t_rank_sort;

        public int t_rank_gold;

        public String t_title;

    }

}