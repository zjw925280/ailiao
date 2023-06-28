package com.lovechatapp.chat.fragment.invite;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.InviteRecyclerAdapter;
import com.lovechatapp.chat.base.BaseCompactFragment;
import com.lovechatapp.chat.base.BaseListResponse;
import com.lovechatapp.chat.bean.RewardBean;
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

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：奖励排行
 * 作者：
 * 创建时间：2018/10/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class RewardFragment extends BaseCompactFragment implements View.OnClickListener {

    public RewardFragment() {

    }

    private InviteRecyclerAdapter mAdapter;
    //榜单
    private int[] ids = {R.id.day_tv, R.id.week_tv, R.id.month_tv, R.id.total_tv};

    @Override
    protected int initLayout() {
        return R.layout.fragment_reward_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        RecyclerView mContentRv = view.findViewById(R.id.content_rv);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new InviteRecyclerAdapter(mContext);
        mContentRv.setAdapter(mAdapter);
        //榜单
        for (int id : ids) {
            view.findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    protected void onFirstVisible() {
        //默认选中总榜
        if (getView() != null) {
            onClick(getView().findViewById(ids[3]));
        }
    }

    /**
     * 获取推荐贡献排行榜
     */
    private void getRankList(int queryType) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("queryType", String.valueOf(queryType));
        OkHttpUtils.post().url(ChatApi.GET_SPREAD_BONUSES())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<RewardBean>>() {
            @Override
            public void onResponse(BaseListResponse<RewardBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<RewardBean> focusBeans = response.m_object;
                    if (focusBeans != null) {
                        mAdapter.loadData(focusBeans);
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(getContext(), R.string.system_error);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getContext(), R.string.system_error);
            }

        });
    }

    @Override
    public void onClick(View v) {
        if (v.isSelected()) {
            return;
        }
        for (int id : ids) {
            if (getView() != null) {
                getView().findViewById(id).setSelected(v.getId() == id);
            }
        }
        switchRank(v.getId());
    }

    /**
     * 切换榜单
     */
    private void switchRank(int id) {
        if (id == R.id.day_tv) {
            getRankList(1);
        } else if (id == R.id.week_tv) {
            getRankList(2);
        } else if (id == R.id.month_tv) {
            getRankList(3);
        } else if (id == R.id.total_tv) {
            getRankList(4);
        }
    }


}
