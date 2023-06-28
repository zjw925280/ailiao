package com.lovechatapp.chat.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.ActiveCommentRecyclerAdapter;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.ActiveCommentBean;
import com.lovechatapp.chat.bean.PageBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：动态评论页面
 * 作者：
 * 创建时间：2019/1/3
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActiveCommentActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.number_tv)
    TextView mNumberTv;
    @BindView(R.id.comment_et)
    EditText mCommentEt;

    private int mCurrentPage = 1;
    private List<ActiveCommentBean> mFocusBeans = new ArrayList<>();
    private ActiveCommentRecyclerAdapter mAdapter;

    private int mDynamicId;//动态编号
    private int mActorId;//主播id
    private int mCommentCount;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_active_comment_layout);
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        initStart();
        initRecycler();
    }

    private void initStart() {
        mDynamicId = getIntent().getIntExtra(Constant.ACTIVE_ID, 0);
        mActorId = getIntent().getIntExtra(Constant.ACTOR_ID, 0);
        //评论数量
        mCommentCount = getIntent().getIntExtra(Constant.COMMENT_NUMBER, 0);
        String content = getResources().getString(R.string.comment_number) + mCommentCount;
        mNumberTv.setText(content);
        if (mDynamicId > 0) {
            getCommentList(mRefreshLayout, true, 1);
        }
    }

    /**
     * 获取动态列表
     */
    private void getCommentList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("dynamicId", String.valueOf(mDynamicId));//0.公开动态，1.关注动态
        paramMap.put("page", String.valueOf(page));
        OkHttpUtils.post().url(ChatApi.GET_COMMENT_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<ActiveCommentBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<ActiveCommentBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<ActiveCommentBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<ActiveCommentBean> focusBeans = pageBean.data;
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

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
                if (isRefresh) {
                    refreshlayout.finishRefresh();
                } else {
                    refreshlayout.finishLoadMore();
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
                getCommentList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getCommentList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new ActiveCommentRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);
        mAdapter.setOnDeleteClickListener(new ActiveCommentRecyclerAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(String commentId) {
                deleteComment(commentId);
            }
        });
    }

    /**
     * 删除评论
     */
    private void deleteComment(String commentId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("commentId", commentId);
        OkHttpUtils.post().url(ChatApi.DEL_COMMENT())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.delete_success);
                    if (mCommentCount > 0) {
                        mCommentCount--;
                        String content = getResources().getString(R.string.comment_number) + mCommentCount;
                        mNumberTv.setText(content);
                    }
                    getCommentList(mRefreshLayout, true, 1);
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.delete_fail);
                }
            }
        });
    }

    @OnClick({R.id.top_v, R.id.send_iv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_v: {
                finish();
                break;
            }
            case R.id.send_iv: {//发布
                postComment();
                break;
            }
        }
    }

    /**
     * 发布
     */
    private void postComment() {
        String comment = mCommentEt.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_comment);
            return;
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverUserId", String.valueOf(mActorId));
        paramMap.put("comment", comment);
        paramMap.put("dynamicId", String.valueOf(mDynamicId));
        OkHttpUtils.post().url(ChatApi.DISCUSS_DYNAMIC())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.comment_success_one);
                    closeSoft(mCommentEt);
                    finish();
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.comment_fail_one);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.comment_fail_one);
            }

        });
    }

    /**
     * 关闭软件盘
     */
    private void closeSoft(EditText mChatInput) {
        try {
            if (mChatInput != null && mChatInput.hasFocus()) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && imm.isActive()) {
                    imm.hideSoftInputFromWindow(mChatInput.getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
