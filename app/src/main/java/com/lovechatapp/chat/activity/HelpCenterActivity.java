package com.lovechatapp.chat.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.HelpCenterRecyclerAdapter;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseListResponse;
import com.lovechatapp.chat.bean.HelpCenterBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：帮助中心
 * 作者：
 * 创建时间：2018/10/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class HelpCenterActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;

    private HelpCenterRecyclerAdapter mAdapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_help_center_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.help_center);
        initRecycler();
        getHelpList();
    }

    /**
     * 初始化recyclerView
     */
    private void initRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(manager);
        mAdapter = new HelpCenterRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);
    }

    /**
     * 获取帮助列表
     */
    private void getHelpList() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_HELP_CONTRE())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<HelpCenterBean>>() {
            @Override
            public void onResponse(BaseListResponse<HelpCenterBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<HelpCenterBean> beans = response.m_object;
                    if (beans != null && beans.size() > 0) {
                        mAdapter.loadData(beans);
                    }
                }
            }
        });
    }

}
