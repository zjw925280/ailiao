package com.lovechatapp.chat.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.SendRecyclerAdapter;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.PageBean;
import com.lovechatapp.chat.bean.SendBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：大房间贡献值Fragment
 * 作者：
 * 创建时间：2019/3/5
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class TotalSendFragment extends BaseFragment {

    public TotalSendFragment() {

    }

    private SmartRefreshLayout mRefreshLayout;
    private SendRecyclerAdapter mAdapter;
    private List<SendBean> mFocusBeans = new ArrayList<>();
    private int mCurrentPage = 1;
    private int mActorId;

    @Override
    protected int initLayout() {
        return R.layout.fragment_user_recycler_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        RecyclerView mContentRv = view.findViewById(R.id.content_rv);
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getUserList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getUserList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new SendRecyclerAdapter(mContext);
        mContentRv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new SendRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SendBean bean) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(bean);
                }
            }
        });
    }

    @Override
    protected void onFirstVisible() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mActorId = bundle.getInt(Constant.ACTOR_ID);
        }
        getUserList(mRefreshLayout, true, 1);
    }

    /**
     * 获取我的徒弟 徒孙
     */
    private void getUserList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", String.valueOf(mActorId));
        paramMap.put("page", String.valueOf(page));
        OkHttpUtils.post().url(ChatApi.GET_BIG_CONTRIBUTION_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<SendBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<SendBean>> response, int id) {
                if (mContext.isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<SendBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<SendBean> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            int size = focusBeans.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mFocusBeans.clear();
                                mFocusBeans.addAll(focusBeans);
                                mAdapter.loadData(mFocusBeans);
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
                        if (isRefresh) {
                            refreshlayout.finishRefresh();
                        } else {
                            refreshlayout.finishLoadMore();
                        }
                    }
                } else {
                    if (isRefresh) {
                        refreshlayout.finishRefresh();
                    } else {
                        refreshlayout.finishLoadMore();
                    }
                }
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(SendBean bean);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}
