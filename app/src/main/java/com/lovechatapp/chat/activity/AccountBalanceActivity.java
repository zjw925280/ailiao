package com.lovechatapp.chat.activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.AccountBalanceRecyclerAdapter;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.AccountBalanceBean;
import com.lovechatapp.chat.bean.InOutComeBean;
import com.lovechatapp.chat.bean.PageBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.dialog.YearMonthChooserDialog;
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
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：账户余额
 * 作者：
 * 创建时间：2018/10/24
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AccountBalanceActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.year_tv)
    TextView mYearTv;
    @BindView(R.id.month_tv)
    TextView mMonthTv;
    @BindView(R.id.left_number_tv)
    TextView mLeftNumberTv;
    @BindView(R.id.income_tv)
    TextView mIncomeTv;
    @BindView(R.id.out_come_tv)
    TextView mOutComeTv;

    private int mCurrentPage = 1;
    private List<AccountBalanceBean> mFocusBeans = new ArrayList<>();
    private AccountBalanceRecyclerAdapter mAdapter;

    private YearMonthChooserDialog yearMonthChooserDialog;

    private String mSelectYear, mSelectMonth;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_account_balance_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.account_left);
        initStart();
    }

    /**
     * 初始化
     */
    private void initStart() {

        yearMonthChooserDialog = new YearMonthChooserDialog(this) {
            @Override
            public void choose() {

                mSelectYear = getSelectYear();
                mSelectMonth = getSelectMonth();
                mYearTv.setText(mSelectYear + getResources().getString(R.string.year));
                mMonthTv.setText(mSelectMonth);

                //刷新数据
                getProfitAndPayTotal();
                getWalletDetail(mRefreshLayout, true, 1);
            }
        };

        //设置年月
        yearMonthChooserDialog.choose();

        mRefreshLayout.setOnRefreshListener(refreshlayout -> getWalletDetail(refreshlayout, true, 1));
        mRefreshLayout.setOnLoadMoreListener(refreshlayout -> getWalletDetail(refreshlayout, false, mCurrentPage + 1));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(linearLayoutManager);
        mAdapter = new AccountBalanceRecyclerAdapter(AccountBalanceActivity.this);
        mContentRv.setAdapter(mAdapter);

        getProfitAndPayTotal();
        getWalletDetail(mRefreshLayout, true, 1);
    }

    /**
     * 获取钱包消费或者提现明细
     */
    private void getWalletDetail(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("queryType", "-1");//查询类型 -1：全部 0.收入 1.支出
        paramMap.put("year", mSelectYear);
        paramMap.put("month", mSelectMonth);
        paramMap.put("page", String.valueOf(page));
        OkHttpUtils.post().url(ChatApi.GET_USER_GOLD_DETAILS())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<AccountBalanceBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<AccountBalanceBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<AccountBalanceBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<AccountBalanceBean> focusBeans = pageBean.data;
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
                        if (isRefresh) {
                            mFocusBeans.clear();
                            mAdapter.loadData(mFocusBeans);
                            refreshlayout.finishRefresh();
                        } else {
                            refreshlayout.finishLoadMore();
                        }
                    }
                } else {
                    try {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
                        if (isRefresh) {
                            mFocusBeans.clear();
                            mAdapter.loadData(mFocusBeans);
                            refreshlayout.finishRefresh();
                        } else {
                            refreshlayout.finishLoadMore();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                if (isRefresh) {
                    showLoadingDialog();
                }
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                if (isRefresh) {
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                try {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
                    if (isRefresh) {
                        mFocusBeans.clear();
                        mAdapter.loadData(mFocusBeans);
                        refreshlayout.finishRefresh();
                    } else {
                        refreshlayout.finishLoadMore();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    /**
     * 1.4版 钱包头部统计
     */
    private void getProfitAndPayTotal() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("year", mSelectYear);
        paramMap.put("month", mSelectMonth);
        OkHttpUtils.post().url(ChatApi.GET_PROFIT_AND_PAY_TOTAL())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<InOutComeBean>>() {
            @Override
            public void onResponse(BaseResponse<InOutComeBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    InOutComeBean bean = response.m_object;
                    if (bean != null) {
                        //收入
                        int income = bean.profit;
                        mIncomeTv.setText(String.valueOf(income));
                        //支出
                        int outCome = bean.pay;
                        mOutComeTv.setText(String.valueOf(outCome));
                        //剩余花瓣
                        int left = income - outCome;
                        mLeftNumberTv.setText(String.valueOf(left));
                        if (left <= 0) {
                            mLeftNumberTv.setTextColor(getResources().getColor(R.color.black_3f3b48));
                        } else {
                            mLeftNumberTv.setTextColor(getResources().getColor(R.color.red_fe2947));
                        }
                    }
                }
            }
        });
    }

    @OnClick({R.id.year_ll})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.year_ll: {
                yearMonthChooserDialog.show();
                break;
            }
        }
    }
}