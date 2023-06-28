package com.lovechatapp.chat.activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.ActorEarnDetailRecyclerAdapter;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.ActorEarnDetailBean;
import com.lovechatapp.chat.bean.ActorEarnDetailListBean;
import com.lovechatapp.chat.bean.PageBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播贡献值详情
 * 作者：
 * 创建时间：2018/9/12
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActorEarnDetailActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.head_iv)
    ImageView mHeadIv;
    @BindView(R.id.nick_tv)
    TextView mNickTv;
    @BindView(R.id.total_tv)
    TextView mTotalTv;
    @BindView(R.id.today_tv)
    TextView mTodayTv;

    private int mCurrentPage = 1;
    private List<ActorEarnDetailListBean> mFocusBeans = new ArrayList<>();
    private ActorEarnDetailRecyclerAdapter mAdapter;
    private int mActorId;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_actor_earn_detail_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.actor_earn_detail_title);
        mActorId = getIntent().getIntExtra(Constant.ACTOR_ID, 0);
        initRecycler();
        if (mActorId > 0) {
            getActorInfo();
            getEarnDetailList(mRefreshLayout, true, 1);
        }
    }

    /**
     * 获取主播信息
     */
    private void getActorInfo() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("anchorId", String.valueOf(mActorId));
        OkHttpUtils.post().url(ChatApi.GET_ANTHOR_TOTAL())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ActorEarnDetailBean>>() {
            @Override
            public void onResponse(BaseResponse<ActorEarnDetailBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ActorEarnDetailBean detailBean = response.m_object;
                    if (detailBean != null) {
                        //头像
                        String handImg = detailBean.t_handImg;
                        if (!TextUtils.isEmpty(handImg)) {
                            int width = DevicesUtil.dp2px(mContext, 50);
                            int high = DevicesUtil.dp2px(mContext, 50);
                            ImageLoadHelper.glideShowCircleImageWithUrl(mContext, handImg, mHeadIv, width, high);
                        } else {
                            mHeadIv.setImageResource(R.drawable.default_head_img);
                        }
                        //昵称
                        String nick = detailBean.t_nickName;
                        if (!TextUtils.isEmpty(nick)) {
                            mNickTv.setText(nick);
                        }
                        //总共贡献值
                        String total = getResources().getString(R.string.earn_gold_des)
                                + detailBean.t_devote_value + getResources().getString(R.string.gold_des_one);
                        mTotalTv.setText(total);
                        //今日
                        String today = getResources().getString(R.string.today_earn_des)
                                + detailBean.toDay + getResources().getString(R.string.gold_des_one);
                        mTodayTv.setText(today);
                    }
                }
            }
        });
    }

    /**
     * 获取主播贡献明细列表
     */
    private void getEarnDetailList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("anchorId", String.valueOf(mActorId));
        paramMap.put("page", String.valueOf(page));
        OkHttpUtils.post().url(ChatApi.GET_CONTRIBUTION_DETAIL())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<ActorEarnDetailListBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<ActorEarnDetailListBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<ActorEarnDetailListBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<ActorEarnDetailListBean> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            int size = focusBeans.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mFocusBeans.clear();
                                mFocusBeans.addAll(focusBeans);
                                mAdapter.loadData(mFocusBeans);
                                if (mFocusBeans.size() > 0) {
                                    mRefreshLayout.setEnableRefresh(true);
                                } else {
                                    mRefreshLayout.setEnableRefresh(false);
                                }
                                refreshlayout.finishRefresh();
                                if (size >= 10) {//如果是刷新,且返回的数据大于等于10条,就可以load more
                                    refreshlayout.setEnableLoadMore(true);
                                }
                            } else {
                                mCurrentPage++;
                                mFocusBeans.addAll(focusBeans);
                                mAdapter.loadData(mFocusBeans);
                                if (size >= 10) {
                                    refreshlayout.finishLoadMore();
                                }
                            }
                            if (size < 10) {//如果数据返回少于10了,那么说明就没数据了
                                refreshlayout.finishLoadMoreWithNoMoreData();
                            }
                        }
                    } else {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
                        if (isRefresh) {
                            refreshlayout.finishRefresh();
                        } else {
                            refreshlayout.finishLoadMore();
                        }
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
                    if (isRefresh) {
                        refreshlayout.finishRefresh();
                    } else {
                        refreshlayout.finishLoadMore();
                    }
                }
            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecycler() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getEarnDetailList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getEarnDetailList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new ActorEarnDetailRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);
    }


}
