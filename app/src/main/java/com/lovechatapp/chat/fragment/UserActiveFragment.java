package com.lovechatapp.chat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.PostActiveActivity;
import com.lovechatapp.chat.activity.UserSelfActiveActivity;
import com.lovechatapp.chat.adapter.UserActiveRecyclerAdapter;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.ActiveBean;
import com.lovechatapp.chat.bean.ActiveFileBean;
import com.lovechatapp.chat.bean.PageBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
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
 * 功能描述：个人中心动态Fragment
 * 作者：
 * 创建时间：2018/12/18
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UserActiveFragment extends BaseFragment implements View.OnClickListener {

    private UserActiveRecyclerAdapter mAdapter;
    private List<ActiveBean<ActiveFileBean>> mFocusBeans = new ArrayList<>();
    private TextView mMoreTv;

    @Override
    protected int initLayout() {
        return R.layout.fragment_user_active_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        TextView post_tv = view.findViewById(R.id.post_tv);
        post_tv.setOnClickListener(this);
        mMoreTv = view.findViewById(R.id.more_tv);
        mMoreTv.setOnClickListener(this);
        RecyclerView mContentRv = view.findViewById(R.id.content_rv);

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new UserActiveRecyclerAdapter(mContext);
        mContentRv.setAdapter(mAdapter);
        mContentRv.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onFirstVisible() {
        mContext.showLoadingDialog();
        getActiveList();
    }

    /**
     * 获取动态列表
     */
    private void getActiveList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", String.valueOf(1));
        OkHttpUtils.post().url(ChatApi.GET_OWN_DYNAMIC_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<ActiveBean<ActiveFileBean>>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<ActiveBean<ActiveFileBean>>> response, int id) {
                mContext.dismissLoadingDialog();
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<ActiveBean<ActiveFileBean>> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<ActiveBean<ActiveFileBean>> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            mFocusBeans.clear();
                            mFocusBeans.addAll(focusBeans);
                            mAdapter.loadData(mFocusBeans);
                            if (focusBeans.size() >= 10) {
                                mMoreTv.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(getContext(), R.string.system_error);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getContext(), R.string.system_error);
                mContext.dismissLoadingDialog();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_tv: {
                Intent intent = new Intent(getContext(), PostActiveActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.more_tv: {//更多
                Intent intent = new Intent(getContext(), UserSelfActiveActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
