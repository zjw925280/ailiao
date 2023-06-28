package com.lovechatapp.chat.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.GiftPackRecyclerAdapter;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.GifResponseBean;
import com.lovechatapp.chat.bean.GiftPackBean;
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
 * 功能描述：主播礼物柜页面
 * 作者：
 * 创建时间：2018/10/26
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class GiftPackActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.gift_number_tv)
    TextView mGiftNumberTv;

    private GiftPackRecyclerAdapter mAdapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_gift_pack_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.gift_pack_title);
        initRecycler();
        int actorId = getIntent().getIntExtra(Constant.ACTOR_ID, 0);
        if (actorId > 0) {
            getAnthorGiftList(actorId);
        }
    }

    /**
     * 获取礼物柜
     */
    private void getAnthorGiftList(int actorId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", String.valueOf(actorId));
        OkHttpUtils.post().url(ChatApi.GET_ANTHOR_GIFT_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<GifResponseBean<GiftPackBean>>>() {
            @Override
            public void onResponse(BaseResponse<GifResponseBean<GiftPackBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    GifResponseBean<GiftPackBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        //数量
                        int number = pageBean.total;
                        if (number > 0) {
                            String content = number + getResources().getString(R.string.per);
                            mGiftNumberTv.setText(content);
                        }
                        //List
                        List<GiftPackBean> closeBeans = pageBean.data;
                        if (closeBeans != null && closeBeans.size() > 0) {
                            mAdapter.loadData(closeBeans);
                        }
                    }
                }
            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecycler() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new GiftPackRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);
    }

}
