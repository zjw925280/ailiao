package com.lovechatapp.chat.activity;

import android.app.Dialog;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.WithDrawDetailRecyclerAdapter;
import com.lovechatapp.chat.adapter.YearPickerRecyclerAdapter;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.PageBeanOne;
import com.lovechatapp.chat.bean.WithDrawDetailBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.layoutmanager.PickerLayoutManager;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.Calendar;
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
 * 功能描述：提现明细页面
 * 作者：
 * 创建时间：2018/6/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class WithDrawDetailActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.no_history_tv)
    TextView mNoHistoryTv;
    @BindView(R.id.year_tv)
    TextView mYearTv;
    @BindView(R.id.month_tv)
    TextView mMonthTv;
    @BindView(R.id.number_tv)
    TextView mNumberTv;

    private String mSelectYear = "";
    private boolean mYearHaveSelected = false;
    private String mSelectMonth = "";
    private boolean mMonthHaveSelected = false;

    private int mCurrentPage = 1;
    private List<WithDrawDetailBean> mFocusBeans = new ArrayList<>();
    private WithDrawDetailRecyclerAdapter mAdapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_with_draw_detail_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.with_draw_money);
        initRecycler();
        getWalletDetail(mRefreshLayout, true, 1);
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecycler() {
        //设置年月
        Calendar calendar = Calendar.getInstance();
        mSelectYear = String.valueOf(calendar.get(Calendar.YEAR));
        String year = mSelectYear + getResources().getString(R.string.year);
        mYearTv.setText(year);
        mSelectMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        mMonthTv.setText(mSelectMonth);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getWalletDetail(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getWalletDetail(refreshlayout, false, mCurrentPage + 1);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mContentRv.setLayoutManager(linearLayoutManager);
        mAdapter = new WithDrawDetailRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);
    }

    /**
     * 获取钱包消费或者提现明细
     */
    private void getWalletDetail(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("queyType", "4");
        paramMap.put("year", mSelectYear);
        paramMap.put("month", mSelectMonth);
        paramMap.put("state", "-1");//-1:全部 0.未审核 1.提现成功 2.提现失败
        paramMap.put("page", String.valueOf(page));
        OkHttpUtils.post().url(ChatApi.GET_WALLET_DETAIL())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBeanOne<WithDrawDetailBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBeanOne<WithDrawDetailBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBeanOne<WithDrawDetailBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        //月汇总
                        mNumberTv.setText(String.valueOf(pageBean.monthTotal));
                        List<WithDrawDetailBean> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            int size = focusBeans.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mFocusBeans.clear();
                                mFocusBeans.addAll(focusBeans);
                                mAdapter.loadData(mFocusBeans);
                                if (mFocusBeans.size() > 0) {
                                    mNoHistoryTv.setVisibility(View.GONE);
                                } else {
                                    mNoHistoryTv.setVisibility(View.VISIBLE);
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

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                dismissLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    @OnClick({R.id.year_ll})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.year_ll: {
                showCityPickerDialog();
                break;
            }
        }
    }

    /**
     * 显示年月Dialog
     */
    private void showCityPickerDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_year_month_picker_layout, null);
        setDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置city picker dialog view
     */
    private void setDialogView(View view, final Dialog mDialog) {
        final List<String> years = new ArrayList<>();
        for (int i = 2018; i < 2040; i++) {
            years.add(i + getResources().getString(R.string.year));
        }
        final List<String> month = new ArrayList<>();
        month.add("1月");
        month.add("2月");
        month.add("3月");
        month.add("4月");
        month.add("5月");
        month.add("6月");
        month.add("7月");
        month.add("8月");
        month.add("9月");
        month.add("10月");
        month.add("11月");
        month.add("12月");
        //左边
        YearPickerRecyclerAdapter leftAdapter = new YearPickerRecyclerAdapter(this);
        RecyclerView left_rv = view.findViewById(R.id.left_rv);
        PickerLayoutManager leftManager = new PickerLayoutManager(getApplicationContext(),
                left_rv, PickerLayoutManager.VERTICAL, false, 3, 0.3f, true);
        left_rv.setLayoutManager(leftManager);
        left_rv.setAdapter(leftAdapter);
        leftAdapter.loadData(years);

        //右边
        final YearPickerRecyclerAdapter rightAdapter = new YearPickerRecyclerAdapter(this);
        RecyclerView right_rv = view.findViewById(R.id.right_rv);
        PickerLayoutManager rightManager = new PickerLayoutManager(getApplicationContext(),
                right_rv, PickerLayoutManager.VERTICAL, false, 3, 0.3f, true);
        right_rv.setLayoutManager(rightManager);
        right_rv.setAdapter(rightAdapter);
        rightAdapter.loadData(month);

        ImageView close_iv = view.findViewById(R.id.close_iv);
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        leftManager.setOnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position) {
                String select = years.get(position);
                String[] result = select.split(getResources().getString(R.string.year));
                if (result.length > 0) {
                    mSelectYear = result[0];
                    mYearHaveSelected = true;
                }
            }
        });

        rightManager.setOnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position) {
                String select = month.get(position);
                String[] result = select.split(getResources().getString(R.string.month));
                if (result.length > 0) {
                    mSelectMonth = result[0];
                    mMonthHaveSelected = true;
                }
            }
        });

        //确定
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mYearHaveSelected) {
                    mSelectYear = "2018";
                }
                if (!mMonthHaveSelected) {
                    mSelectMonth = "1";
                }

                String content = mSelectYear + getResources().getString(R.string.year);
                mYearTv.setText(content);
                mMonthTv.setText(mSelectMonth);
                mYearHaveSelected = false;
                mMonthHaveSelected = false;
                mDialog.dismiss();
                //刷新数据
                getWalletDetail(mRefreshLayout, true, 1);
            }
        });
    }

}
