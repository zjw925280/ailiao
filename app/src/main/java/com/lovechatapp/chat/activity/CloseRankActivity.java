package com.lovechatapp.chat.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.CloseRankRecyclerAdapter;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.CloseBean;
import com.lovechatapp.chat.bean.PageBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
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
 * 功能描述：亲密榜页面
 * 作者：
 * 创建时间：2018/10/25
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CloseRankActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;

    private CloseRankRecyclerAdapter mCloseRankRecyclerAdapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_close_rank_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.close_rank);
        initStart();
        int actorId = getIntent().getIntExtra(Constant.ACTOR_ID, 0);
        if (actorId > 0) {
            getAnthorIntimateList(actorId);
        }
    }

    /**
     * 初始化
     */
    private void initStart() {
        mContentRv.setNestedScrollingEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(manager);
        mCloseRankRecyclerAdapter = new CloseRankRecyclerAdapter(this);
        mContentRv.setAdapter(mCloseRankRecyclerAdapter);
    }

    /**
     * 1.4版 获取主播亲密度列表
     */
    private void getAnthorIntimateList(int actorId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", String.valueOf(actorId));
        paramMap.put("page", "1");
        OkHttpUtils.post().url(ChatApi.GET_ANTHOR_INTIMATE_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<CloseBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<CloseBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<CloseBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<CloseBean> closeBeans = pageBean.data;
                        if (closeBeans != null && closeBeans.size() > 0) {
                            mCloseRankRecyclerAdapter.loadData(closeBeans);
                        }
                    }
                }
            }
        });


    }

}
