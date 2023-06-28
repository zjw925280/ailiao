package com.lovechatapp.chat.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.SearchRecyclerAdapter;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.PageBean;
import com.lovechatapp.chat.bean.SearchBean;
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

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主页
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SearchActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.search_et)
    EditText mSearchEt;
    @BindView(R.id.search_tv)
    TextView mSearchTv;

    private int mCurrentPage = 1;
    private List<SearchBean> mFocusBeans = new ArrayList<>();
    private SearchRecyclerAdapter mAdapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_search_layout);
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        initRecycler();
        showSpan();
    }

    /**
     * 获取个人浏览记录
     */
    private void getSearchResult(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        //搜索条件
        String condition = mSearchEt.getText().toString().trim();
        if (TextUtils.isEmpty(condition)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.search_hint);
            refreshlayout.finishRefresh();
            return;
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("condition", condition);
        OkHttpUtils.post().url(ChatApi.GET_SEARCH_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<SearchBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<SearchBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<SearchBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<SearchBean> focusBeans = pageBean.data;
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
                                } else if (size <= 0) {
                                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.no_one);
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
                getSearchResult(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getSearchResult(refreshlayout, false, mCurrentPage + 1);
            }
        });
        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && s.length() > 0) {
                    mSearchTv.setText(getResources().getString(R.string.search));
                } else {
                    mSearchTv.setText(getResources().getString(R.string.cancel));
                }
            }
        });
        mSearchEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String condition = mSearchEt.getText().toString().trim();
                    if (TextUtils.isEmpty(condition)) {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.search_hint);
                        return true;
                    }
                    closeSoft();
                    mRefreshLayout.autoRefresh();
                    return true;
                }
                return false;
            }
        });

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new SearchRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);
    }

    @OnClick({R.id.back_fl, R.id.search_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_fl: {
                closeSoft();
                finish();
                break;
            }
            case R.id.search_tv: {
                String des = mSearchTv.getText().toString().trim();
                if (des.equals(getResources().getString(R.string.search))) {
                    String condition = mSearchEt.getText().toString().trim();
                    if (TextUtils.isEmpty(condition)) {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.search_hint);
                        return;
                    }
                    closeSoft();
                    mRefreshLayout.autoRefresh();
                } else {
                    closeSoft();
                    finish();
                }
                break;
            }
        }
    }

    /**
     * 调用键盘
     */
    private void showSpan() {
        if (mSearchEt != null) {
            mSearchEt.requestFocus();
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mSearchEt != null && mSearchEt.requestFocus()) {
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.showSoftInput(mSearchEt, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                }
            }, 400);
        }
    }

    /**
     * 关闭软件盘
     */
    private void closeSoft() {
        try {
            if (mSearchEt != null && mSearchEt.hasFocus()) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && imm.isActive()) {
                    imm.hideSoftInputFromWindow(mSearchEt.getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
