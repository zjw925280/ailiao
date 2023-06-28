package com.lovechatapp.chat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.PersonInfoActivity;
import com.lovechatapp.chat.activity.RankRewardActivity;
import com.lovechatapp.chat.adapter.RankAdapter;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.base.BaseListResponse;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.RankBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.dialog.MysteryDialog;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.listener.OnGetListener;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.view.recycle.OnItemClickListener;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;

/**
 * 榜单
 */
public class RankFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView mContentRv;

    /**
     * 上周、上月奖励按钮
     */
    private ImageView reward_iv;

    /**
     * 是否展示按钮、展示列表奖励
     */
    private JSONObject config;

    /**
     * 排行榜类型
     */
    private RankType rankType;

    /**
     * 排行榜列表adapter
     */
    private RankAdapter mAdapter;

    /**
     * 1.日榜  2.周榜  3.月榜  4.总榜 5.昨日 6.上周 7.上月
     */
    private final int[] ids = {R.id.day_tv, R.id.week_tv, R.id.month_tv};
    private final int[] idValues = {1, 2, 3};

    /**
     * 默认选中日榜
     */
    final int defaultSelected = 1;

    /**
     * 前三名VH
     */
    private final List<RankVH> rankVHS = new ArrayList<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rankType = getArguments() != null ?
                RankType.valueOf(getArguments().getString("type")) : RankType.Goddess;

        if (getView() != null) {

            rankVHS.add(new RankVH(rankType, getView(), R.id.first_btn, R.id.one_head_iv,
                    R.id.one_nick_tv, R.id.one_chat_number_tv, R.id.one_gold_tv, R.id.one_reward_tv));

            rankVHS.add(new RankVH(rankType, getView(), R.id.second_btn, R.id.two_head_iv,
                    R.id.two_nick_tv, R.id.two_chat_number_tv, R.id.two_gold_tv, R.id.two_reward_tv));

            rankVHS.add(new RankVH(rankType, getView(), R.id.three_btn, R.id.three_head_iv,
                    R.id.three_nick_tv, R.id.three_chat_number_tv, R.id.three_gold_tv, R.id.three_reward_tv));
        }

        for (int i = 0; i < ids.length; i++) {
            int id = ids[i];
            View v = findViewById(id);
            v.setOnClickListener(this);
            v.setTag(idValues[i]);
        }

        mAdapter = new RankAdapter(mContext, rankType);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object obj, int position) {
                toPersonData((RankBean) obj);
            }
        });
        mContentRv = findViewById(R.id.content_rv);
        mContentRv.setNestedScrollingEnabled(false);
        mContentRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mContentRv.setAdapter(mAdapter);

        reward_iv = findViewById(R.id.reward_iv);
        reward_iv.setVisibility(View.GONE);
        reward_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //跳转上周上月排行榜奖励
                if (v.getTag() != null) {
                    RankData rankData = (RankData) v.getTag();
                    RankRewardActivity.start(getActivity(), rankType, rankData);
                }

            }
        });

        onClick(findViewById(ids[defaultSelected]));
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_beauty_rank_layout;
    }

    /**
     * 获取榜单 1.日榜  2.周榜  3.月榜  4.总榜 5.昨日 6.上周 7.上月
     */
    private void getList(int queryType) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("queryType", queryType);
        OkHttpUtils.post().url(rankType.getMethod())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<RankBean>>() {
            @Override
            public void onResponse(BaseListResponse<RankBean> response, int id) {
                if (getActivity() == null || getActivity().isFinishing())
                    return;
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    dealBean(rankType, queryType, response.m_object);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (getActivity() == null || getActivity().isFinishing())
                    return;
                dealBean(rankType, queryType, null);
            }
        });
    }

    /**
     * 主播/用户资料页跳转
     */
    private void toPersonData(RankBean rankBean) {

        //神秘人不跳转
        if (rankBean.t_rank_switch == 1) {
            new MysteryDialog(mContext).show();
            return;
        }

        //邀请榜不跳转
        if (rankType == RankType.Invitation) {
            return;
        }

        PersonInfoActivity.start(mContext, rankBean.t_id);
    }

    /**
     * 设置1/2/3名
     * type: 1.日榜  2.周榜  3.月榜  4.总榜 5.昨日 6.上周 7.上月
     */
    private void dealBean(RankType rankType, int type, List<RankBean> rankBeans) {

        if (rankBeans == null) {
            rankBeans = new ArrayList<>();
        }

        //计算距离上一名的金币数
        RankBean lastBean = null;
        for (RankBean rankBean : rankBeans) {
            rankBean.off_gold = lastBean != null ? lastBean.gold - rankBean.gold : 0;
            lastBean = rankBean;
        }

        //是否奖励模式
//        boolean rewardMode = rewardMode(rankType, type, config);
        boolean rewardMode = false;

        for (int i = 0; i < rankVHS.size(); i++) {

            RankVH rankVH = rankVHS.get(i);
            rankVH.reset();

            if (rankBeans.size() > i) {

                final RankBean rankBean = rankBeans.get(i);

                //昵称
                rankVH.nameTv.setText(rankBean.t_nickName);

                //id
                rankVH.idTv.setText(String.format("ID: %s", rankBean.t_idcard));
                if (rankBean.t_rank_switch == 1) {
                    rankVH.idTv.setText(null);
                }

                //邀请榜隐藏Id、名称*号处理
                if (rankType == RankType.Invitation) {
                    rankVH.idTv.setText(null);
                    if (rankBean.t_nickName != null && rankBean.t_nickName.length() > 0) {
                        rankVH.nameTv.setText(rankBean.t_nickName.substring(0, 1));
                        rankVH.nameTv.append("***");
                    }
                }
                if (rewardMode) {
                    rankVH.idTv.setText(String.format("奖励: %s", rankBean.t_rank_gold));
                }

                //设置头像
                Glide.with(mContext)
                        .load(rankBean.t_handImg)
                        .error(R.drawable.default_head)
                        .transform(new CircleCrop())
                        .into(rankVH.headIv);

                //设置金币
                rankVH.goldTv.setText(String.format(Locale.CHINA, mContext.getString(R.string.gold_gap), rankBean.getOffGold()));

                //奖励领取状态
                rankVH.rewardTv.setVisibility(rewardMode ? View.VISIBLE : View.INVISIBLE);
                rankVH.rewardTv.setText(rankBean.t_is_receive == 1 ? "已领取" : "未领取");
                rankVH.rewardTv.setBackgroundResource(rankBean.t_is_receive == 1 ?
                        R.drawable.corner_solid_graye7 : R.drawable.corner_purple);
                rankVH.rewardTv.setTextColor(rankBean.t_is_receive == 1 ? 0xff999999 : 0xffffffff);

                //跳转资料页
                rankVH.setOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toPersonData(rankBean);
                    }
                });
            }
        }

        mAdapter.setRewardMode(rewardMode);
        mContentRv.scrollToPosition(0);

        //处理第三名以后的数据
        if (rankBeans.size() > 3) {
            mAdapter.loadData(rankBeans.subList(3, rankBeans.size()));
        } else {
            mAdapter.loadData(new ArrayList<>());
        }
    }

    /**
     * 是否有周榜/月榜/昨日/奖励
     *
     * @param type 1.日榜  2.周榜  3.月榜  4.总榜  5.昨日  6.上周  7.上月
     */
    public boolean rewardMode(RankType rankType, int type, JSONObject config) {

        boolean rewardMode = false;
        reward_iv.setVisibility(View.GONE);

        switch (type) {

            //展示周榜奖励按钮
            case 2:
                if (config.getIntValue(rankType.week) == 1) {
                    reward_iv.setImageResource(R.drawable.last_week_reward);
                    reward_iv.setVisibility(View.VISIBLE);
                    reward_iv.setTag(RankData.LastWeek);
                }
                break;

            //展示月榜奖励按钮
            case 3:
                if (config.getIntValue(rankType.month) == 1) {
                    reward_iv.setImageResource(R.drawable.last_month_reward);
                    reward_iv.setVisibility(View.VISIBLE);
                    reward_iv.setTag(RankData.LastMonth);
                }
                break;

            //展示昨日榜列表奖励
            case 5:
                rewardMode = config.getIntValue(rankType.day) == 1;
                break;

        }
        return rewardMode;
    }

    /**
     * 切换榜单类型
     */
    @Override
    public void onClick(View v) {
        if (v.isSelected()) {
            return;
        }
        for (int id : ids) {
            findViewById(id).setSelected(v.getId() == id);
        }
        int type = (int) v.getTag();
        if (config == null) {
            getConfig(new OnCommonListener<Boolean>() {
                @Override
                public void execute(Boolean aBoolean) {
                    if (config != null) {
                        getList(type);
                    }
                }
            });
            return;
        }
        getList(type);
    }

    private void getConfig(OnCommonListener<Boolean> listener) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        OkHttpUtils.post().url(ChatApi.getSystemConfig())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<JSONObject>>() {
            @Override
            public void onResponse(BaseResponse<JSONObject> response, int id) {
                if (getActivity() == null || getActivity().isFinishing())
                    return;
                if (response != null && response.m_istatus == NetCode.SUCCESS && response.m_object != null) {
                    config = response.m_object;
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.INSTANCE.showToast("获取失败");
            }

            @Override
            public void onAfter(int id) {
                if (getActivity() == null || getActivity().isFinishing())
                    return;
                listener.execute(null);
            }
        });
    }

    /**
     * 1:女神榜 2:邀请榜
     */
    public enum RankType {

        Goddess(1, "invite_day", "invite_week", "inviter_month", ChatApi::GET_GLAMOUR_LIST),

        Invitation(2, "glamour_day", "glamour_week", "glamour_month", ChatApi::getSpreadUser),

        Consumption(3, "wealth_day", "wealth_week", "wealth_month", ChatApi::GET_CONSUME_LIST),

        Guard(4, "guard_day", "guard_week", "guard_month", ChatApi::getUserGuardList);

        public int rankType;
        public String day;
        public String week;
        public String month;
        private final OnGetListener<String> onGetListener;

        RankType(int rankType, String day, String week, String month, OnGetListener<String> onGetListener) {
            this.rankType = rankType;
            this.day = day;
            this.week = week;
            this.month = month;
            this.onGetListener = onGetListener;
        }

        public String getMethod() {
            return onGetListener.get();
        }
    }

    /**
     * 1.日榜  2.周榜  3.月榜  4.总榜 5.昨日 6.上周 7.上月
     */
    public enum RankData {

        LastWeek(6, R.drawable.reward_week_text),
        LastMonth(7, R.drawable.reward_month_text);

        public int rankType;
        public int rewardIcon;

        RankData(int rankType, int rewardIcon) {
            this.rankType = rankType;
            this.rewardIcon = rewardIcon;
        }
    }

    /**
     * 前三名VH
     */
    static class RankVH {

        //点击事件
        View clickView;

        //头像
        ImageView headIv;

        //昵称
        TextView nameTv;

        //Id
        TextView idTv;

        //金币
        TextView goldTv;

        //奖励领取状态
        TextView rewardTv;

        public RankVH(RankType rankType, View view, int clickId, int imageId, int nameId, int textId, int goldId, int rewardId) {

            this.clickView = view.findViewById(clickId);
            this.headIv = view.findViewById(imageId);
            this.nameTv = view.findViewById(nameId);
            this.idTv = view.findViewById(textId);
            this.goldTv = view.findViewById(goldId);
            this.rewardTv = view.findViewById(rewardId);

            boolean mFromCost = rankType == RankFragment.RankType.Consumption;
            this.goldTv.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    mFromCost ? R.drawable.rank_cost : R.drawable.rank_hot, 0, 0, 0);
        }

        private void setOnClick(View.OnClickListener onClickListener) {
            clickView.setOnClickListener(onClickListener);
        }

        private void reset() {
            setOnClick(null);
            headIv.setImageResource(0);
            nameTv.setText(null);
            idTv.setText(null);
            goldTv.setText(null);
            rewardTv.setVisibility(View.INVISIBLE);
        }

    }

}