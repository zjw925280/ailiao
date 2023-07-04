package com.lovechatapp.chat.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.VideoPagerActivity;
import com.lovechatapp.chat.adapter.HomeVideoRecyclerAdapter;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.PageBean;
import com.lovechatapp.chat.bean.AlbumBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.view.recycle.RecycleGridDivider;
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
 * 功能描述：短视频页面Fragment
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class VideoFragment extends BaseFragment {

    private SmartRefreshLayout mRefreshLayout;
    private HomeVideoRecyclerAdapter mAdapter;
    private List<AlbumBean> mFocusBeans = new ArrayList<>();
    private int mCurrentPage = 1;

    //请求类型 -1：全部 0.免费  1.私密
    private int mQueryType = -1;

    private int[] ids = {R.id.video_all_btn, R.id.video_free_btn, R.id.video_pay_btn};

    @Override
    protected int initLayout() {
        return R.layout.fragment_video;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

        RecyclerView mVideoRv = view.findViewById(R.id.video_rv);
        mVideoRv.addItemDecoration(new RecycleGridDivider((int) getActivity().getResources().getDimension(R.dimen.item_space)));

        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(refreshLayout -> getVideoList(refreshLayout, true, 1));
        mRefreshLayout.setOnLoadMoreListener(refreshLayout -> getVideoList(refreshLayout, false, mCurrentPage + 1));

        //选项
        View.OnClickListener listener = v -> {
            if (v.isSelected())
                return;
            for (int id : ids) {
                findViewById(id).setSelected(v.getId() == id);
            }
            mQueryType = (int) v.getTag();
            mRefreshLayout.autoRefresh();
        };
        for (int i = 0; i < ids.length; i++) {
            View v = view.findViewById(ids[i]);
            v.setTag(i - 1);
            v.setOnClickListener(listener);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mVideoRv.setLayoutManager(gridLayoutManager);
        mAdapter = new HomeVideoRecyclerAdapter(mContext) {
            @Override
            public void itemClick(int position) {
                VideoPagerActivity.start(getActivity(), mFocusBeans, position, mCurrentPage, mQueryType);
            }
        };
        mVideoRv.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //默认选中全部
        findViewById(ids[0]).performClick();
    }

    /**
     * 获取主播视频照片
     */
    private void getVideoList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("queryType", String.valueOf(mQueryType));
        OkHttpUtils.post().url(ChatApi.GET_VIDEO_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<AlbumBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<AlbumBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<AlbumBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<AlbumBean> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            int size = focusBeans.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mFocusBeans.clear();
                                mFocusBeans.addAll(focusBeans);
                                mAdapter.loadData(mFocusBeans);
                                if (mFocusBeans.size() > 0) {
                                    mRefreshLayout.setEnableRefresh(true);
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

}