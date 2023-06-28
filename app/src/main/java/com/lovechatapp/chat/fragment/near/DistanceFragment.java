package com.lovechatapp.chat.fragment.near;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.DistanceRecyclerAdapter;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.NearBean;
import com.lovechatapp.chat.bean.PageBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.LogUtil;
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
 * 功能描述：距离的Fragment
 * 作者：
 * 创建时间：2018/11/16
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class DistanceFragment extends BaseFragment {

    private SmartRefreshLayout mRefreshLayout;
    private DistanceRecyclerAdapter mAdapter;
    private List<NearBean> mFocusBeans = new ArrayList<>();
    private int mCurrentPage = 1;

    @Override
    protected int initLayout() {
        return R.layout.fragment_distance_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        RecyclerView mVideoRv = view.findViewById(R.id.video_rv);
        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getVideoList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getVideoList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
        mVideoRv.setLayoutManager(gridLayoutManager);
        mAdapter = new DistanceRecyclerAdapter(mContext);
        mVideoRv.setAdapter(mAdapter);
    }

    @Override
    protected void onFirstVisible() {
        String lat = SharedPreferenceHelper.getCodeLat(getContext());
        String lng = SharedPreferenceHelper.getCodeLng(getContext());
        if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng)) {
            getVideoList(mRefreshLayout, true, 1);
        } else {
            startLocation();
        }
    }


    /**
     * 获取主播或者用户列表
     */
    private void getVideoList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        String lat = SharedPreferenceHelper.getCodeLat(getContext());
        String lng = SharedPreferenceHelper.getCodeLng(getContext());
        if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lat)) {
            mRefreshLayout.finishRefresh(1000);
            startLocation();
            return;
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("lat", lat);
        paramMap.put("lng", lng);
        OkHttpUtils.post().url(ChatApi.GET_ANTHOR_DISTANCE_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<NearBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<NearBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<NearBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<NearBean> focusBeans = pageBean.data;
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

    private void startLocation() {
        //检查权限
        if (ActivityCompat.checkSelfPermission(mContext.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext.getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LogUtil.i("distance没有权限:");
            return;
        }

        //声明AMapLocationClient类对象
        AMapLocationClient mLocationClient = new AMapLocationClient(mContext.getApplicationContext());
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {//成功
                        //保存在本地
                        double lat = aMapLocation.getLatitude();
                        double lng = aMapLocation.getLongitude();
                        if (lat > 0 && lng > 0) {
                            SharedPreferenceHelper.saveCode(mContext.getApplicationContext(), String.valueOf(lat),
                                    String.valueOf(lng));
                            getVideoList(mRefreshLayout, true, 1);
                        }
                    } else {//失败
                        LogUtil.i("Distance: 定位失败 :" + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        });
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

}
