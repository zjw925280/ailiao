package com.lovechatapp.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseListResponse;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.RankBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.dialog.RewardRuleDialog;
import com.lovechatapp.chat.fragment.RankFragment;
import com.lovechatapp.chat.glide.GlideCircleTransform;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;

/**
 * 榜单奖励
 */
public class RankRewardActivity extends BaseActivity {

    @BindView(R.id.title_tv)
    TextView titleTv;

    @BindView(R.id.type_iv)
    ImageView typeIv;

    @BindView(R.id.content_rv)
    RecyclerView contentRv;

    @BindView(R.id.my_reward_tv)
    TextView myRewardTv;

    @BindView(R.id.reward_gold_tv)
    TextView rewardGoldTv;

    @BindView(R.id.get_reward_tv)
    TextView getRewardTv;

    private AbsRecycleAdapter adapter;

    private RankFragment.RankData rankDate;

    private RankFragment.RankType rankType;

    public static void start(Context context, RankFragment.RankType rankType, RankFragment.RankData rankData) {
        Intent starter = new Intent(context, RankRewardActivity.class);
        starter.putExtra("rankType", rankType.name());
        starter.putExtra("dateType", rankData.name());
        context.startActivity(starter);
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_rank_reward);
    }

    @Override
    protected void onContentAdded() {

        needHeader(false);

        rankType = RankFragment.RankType.valueOf(getIntent().getStringExtra("rankType"));
        rankDate = RankFragment.RankData.valueOf(getIntent().getStringExtra("dateType"));
        typeIv.setImageResource(rankDate.rewardIcon);

        adapter = new AbsRecycleAdapter(new AbsRecycleAdapter.Type(R.layout.item_rank_reward, RankBean.class)) {

            DecimalFormat decimalFormat = new DecimalFormat("00");

            @Override
            public void convert(ViewHolder holder, Object t) {
                RankBean bean = (RankBean) t;

                //排名
                holder.<TextView>getView(R.id.number_tv).setText(decimalFormat.format(holder.getRealPosition() + 1));

                //头像
                Glide.with(mContext)
                        .load(bean.t_handImg)
                        .error(R.drawable.default_head_img)
                        .override(DevicesUtil.dp2px(mContext, 42))
                        .transform(new GlideCircleTransform(mContext))
                        .into(holder.<ImageView>getView(R.id.head_iv));

                //昵称
                holder.<TextView>getView(R.id.nick_tv) .setText(bean.t_nickName);
                if (bean.t_nickName != null && bean.t_nickName.length() > 0) {
                    holder.<TextView>getView(R.id.nick_tv).setText(bean.t_nickName.substring(0, 1));
                    holder.<TextView>getView(R.id.nick_tv).append("***");
                }

                //ID
//                holder.<TextView>getView(R.id.id_tv).setText(String.format("ID: %s", bean.t_idcard));

                //距离上一名花瓣数量
                String goldGapText = String.format(Locale.CHINA, mContext.getString(R.string.gold_gap), bean.off_gold + "");
                holder.<TextView>getView(R.id.gold_tv).setText(goldGapText);

                //奖励
                holder.<TextView>getView(R.id.reward_gold_tv).setText(String.format("奖励: %s金币", bean.t_rank_gold));

                //奖励领取状态
                holder.<TextView>getView(R.id.reward_state_tv).setText(bean.t_is_receive == 1 ? "已领取" : "未领取");
                holder.<TextView>getView(R.id.reward_state_tv).setBackgroundResource(bean.t_is_receive == 1 ?
                        R.drawable.corner_solid_graye7 : R.drawable.corner_purple);
                holder.<TextView>getView(R.id.reward_state_tv).setTextColor(bean.t_is_receive == 1 ? 0xff999999 : 0xffffffff);
            }
        };
        contentRv.setLayoutManager(new LinearLayoutManager(mContext));
        contentRv.setAdapter(adapter);

        getList();
        getTitleDesc();
    }

    /**
     * rankType	1:女神榜 2:邀请榜
     * queryType	5:昨日 6:上周 7:上月
     */
    private void getTitleDesc() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("rankType", rankType.rankType);
        paramMap.put("queryType", rankDate.rankType);
        OkHttpUtils.post().url(ChatApi.getRankConfig())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<JSONObject>>() {
            @Override
            public void onResponse(BaseResponse<JSONObject> response, int id) {
                if (isFinishing())
                    return;
                if (response != null && response.m_istatus == NetCode.SUCCESS && response.m_object != null) {
                    titleTv.setText(response.m_object.getString("rankDesc"));
                }
            }
        });
    }

    @OnClick({
            R.id.finish_btn,
            R.id.rule_btn
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.finish_btn:
                finish();
                break;

            case R.id.rule_btn:
                new RewardRuleDialog(mContext).show();
                break;
        }
    }

    private void getList() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("queryType", rankDate.rankType);
        OkHttpUtils.post().url(rankType.getMethod())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<RankBean>>() {
            @Override
            public void onResponse(BaseListResponse<RankBean> response, int id) {
                if (isFinishing())
                    return;
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<RankBean> rankBeans = response.m_object;
                    if (rankBeans != null) {
                        RankBean lastBean = null;
                        for (RankBean rankBean : rankBeans) {
                            if (lastBean != null) {
                                rankBean.off_gold = lastBean.gold - rankBean.gold;
                            }
                            lastBean = rankBean;
                        }
                        adapter.setData(rankBeans, true);
                        myRankReward(rankDate.rankType, rankBeans);
                    }
                }
            }
        });
    }

    /**
     * 我的奖励
     */
    private void myRankReward(int type, List<RankBean> rankBeans) {
        View view = findViewById(R.id.rank_reward_ll);

        int index = 1;
        RankBean bean = null;
        for (RankBean rankBean : rankBeans) {
            if (rankBean.t_id == AppManager.getInstance().getUserInfo().t_id) {
                bean = rankBean;
                break;
            }
            index++;
        }
        if (bean != null) {
            view.setVisibility(View.VISIBLE);

            //我的排名
            TextView myRankTv = findViewById(R.id.my_reward_tv);
            myRankTv.setText(String.format("我的排名: %s", index));

            //奖励金币数
            TextView rewardGoldTv = findViewById(R.id.reward_gold_tv);
            rewardGoldTv.setText(String.format("奖励: %s金币", bean.t_rank_gold));

            //奖励领取状态
            TextView rewardStateTv = findViewById(R.id.get_reward_tv);
            rewardStateTv.setText(bean.t_is_receive == 1 ? "已领取" : "领取");
            rewardStateTv.setBackgroundResource(bean.t_is_receive == 1 ?
                    R.drawable.corner_solid_graye7 : R.drawable.corner_purple_7948fb);
            rewardStateTv.setTextColor(bean.t_is_receive == 1 ? 0xff999999 : 0xffffffff);
            rewardStateTv.setOnClickListener(null);

            //领取奖励
            if (bean.t_is_receive != 1) {
                RankBean finalBean = bean;
                rewardStateTv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Map<String, Object> paramMap = new HashMap<>();
                        paramMap.put("userId", mContext.getUserId());
                        paramMap.put("rankType", rankType.rankType);
                        paramMap.put("rankRewardId", finalBean.rankRewardId);
                        paramMap.put("queryType", type);
                        OkHttpUtils.post().url(ChatApi.receiveRankGold())
                                .addParams("param", ParamUtil.getParam(paramMap))
                                .build().execute(new AjaxCallback<BaseListResponse<RankBean>>() {
                            @Override
                            public void onResponse(BaseListResponse<RankBean> response, int id) {
                                if (isFinishing())
                                    return;
                                if (response != null) {
                                    if (response.m_istatus == NetCode.SUCCESS) {
                                        ToastUtil.INSTANCE.showToast("领取成功");
                                        finalBean.t_is_receive = 1;
                                        adapter.notifyDataSetChanged();
                                        myRankReward(type, rankBeans);
                                    } else {
                                        ToastUtil.INSTANCE.showToast(response.m_strMessage);
                                    }
                                }
                            }

                            @Override
                            public void onBefore(Request request, int id) {
                                if (isFinishing())
                                    return;
                                showLoadingDialog();
                            }

                            @Override
                            public void onAfter(int id) {
                                if (isFinishing())
                                    return;
                                dismissLoadingDialog();
                            }

                            @Override
                            public void onError(Call call, Exception e, int id) {
                                ToastUtil.INSTANCE.showToast("领取失败");
                            }
                        });
                    }

                });
            }
        } else {
            view.setVisibility(View.GONE);
        }
    }
}