package com.lovechatapp.chat.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.FansRecyclerAdapter;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.FansBean;
import com.lovechatapp.chat.bean.PageBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.dialog.HelloDialog;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：男用户页面Fragment
 * 作者：
 * 创建时间：2018/8/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class FansFragment extends BaseFragment {

    private SmartRefreshLayout mRefreshLayout;
    private FansRecyclerAdapter mAdapter;
    private List<FansBean> mFocusBeans = new ArrayList<>();
    private int mCurrentPage = 1;
    private View greetBtn;

    @Override
    protected int initLayout() {
        return R.layout.fragment_fans_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        RecyclerView mContentRv = view.findViewById(R.id.content_rv);
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(refreshlayout -> getFansList(refreshlayout, true, 1));
        mRefreshLayout.setOnLoadMoreListener(refreshlayout -> getFansList(refreshlayout, false, mCurrentPage + 1));

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new FansRecyclerAdapter(mContext);
        mContentRv.setAdapter(mAdapter);

        //打招呼
        greetBtn = view.findViewById(R.id.greet_iv);
        greetBtn.setOnClickListener(v -> new HelloDialog(mContext).show());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getFansList(mRefreshLayout, true, 1);
    }

    /**
     * 获取关注列表
     */
    private void getFansList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("searchType", String.valueOf(1));
        OkHttpUtils.post().url(ChatApi.GET_ONLINE_USER_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<FansBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<FansBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<FansBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        int pageCount = pageBean.pageCount;
                        List<FansBean> focusBeans = pageBean.data;
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
                                } else {
                                    refreshlayout.finishLoadMoreWithNoMoreData();
                                }
                            } else {
                                mCurrentPage++;
                                mFocusBeans.addAll(focusBeans);
                                mAdapter.loadData(mFocusBeans);
                                if (size < 10 || mCurrentPage == pageCount) {
                                    refreshlayout.finishLoadMoreWithNoMoreData();
                                } else {
                                    refreshlayout.finishLoadMore();
                                }
                            }
                        }
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(getContext(), R.string.system_error);
                    if (isRefresh) {
                        refreshlayout.finishRefresh();
                    } else {
                        refreshlayout.finishLoadMore();
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getContext(), R.string.system_error);
                if (isRefresh) {
                    refreshlayout.finishRefresh();
                } else {
                    refreshlayout.finishLoadMore();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isActor = AppManager.getInstance().getUserInfo().t_role == 1;
        greetBtn.setVisibility(isActor ? View.VISIBLE : View.GONE);
        if (isActor) {
            startAnim(greetBtn);
        }
    }
    private void startAnim(View view) {
        AnimatorSet animSet = (AnimatorSet) view.getTag();
        if (animSet == null) {
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.3f);
            anim1.setRepeatCount(ValueAnimator.INFINITE);
            anim1.setRepeatMode(ValueAnimator.REVERSE);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.3f);
            anim2.setRepeatCount(ValueAnimator.INFINITE);
            anim2.setRepeatMode(ValueAnimator.REVERSE);
            animSet = new AnimatorSet();
            animSet.setDuration(400);
            animSet.playTogether(anim1, anim2);
            view.setTag(animSet);
            animSet.start();
        }
    }
}