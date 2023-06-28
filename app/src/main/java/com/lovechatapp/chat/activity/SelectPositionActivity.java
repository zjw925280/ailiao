package com.lovechatapp.chat.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.PositionRecyclerAdapter;
import com.lovechatapp.chat.adapter.SearchPositionRecyclerAdapter;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.PoiBean;
import com.lovechatapp.chat.bean.SearchPoiBean;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：动态图片页面
 * 作者：
 * 创建时间：2018/1/5
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SelectPositionActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;

    private PositionRecyclerAdapter mPositionRecyclerAdapter;
    private String mCityName;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_select_position_layout);
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        initStart();
        startLocation();
    }

    /**
     * 初始化
     */
    private void initStart() {
        mPositionRecyclerAdapter = new PositionRecyclerAdapter(SelectPositionActivity.this);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getApplicationContext());
        mContentRv.setLayoutManager(gridLayoutManager);
        mContentRv.setAdapter(mPositionRecyclerAdapter);
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        //检查权限
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.need_position_permission);
            return;
        }

        showLoadingDialog();

        //声明AMapLocationClient类对象
        AMapLocationClient mLocationClient = new AMapLocationClient(getApplicationContext());
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
                        String mCityCode = aMapLocation.getCityCode();
                        mCityName = aMapLocation.getCity();
                        if (lat > 0 && lng > 0) {
                            startSearch(lat, lng, mCityCode);
                        } else {
                            String latSave = SharedPreferenceHelper.getCodeLat(getApplicationContext());
                            String lngSave = SharedPreferenceHelper.getCodeLng(getApplicationContext());
                            if (!TextUtils.isEmpty(latSave) && !TextUtils.isEmpty(lngSave)
                                    && Double.parseDouble(latSave) > 0 && Double.parseDouble(lngSave) > 0) {
                                startSearch(Double.parseDouble(latSave), Double.parseDouble(lngSave), mCityCode);
                            } else {
                                dismissLoadingDialog();
                            }
                        }
                    } else {//失败
                        LogUtil.i("定位失败 :" + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                        String latSave = SharedPreferenceHelper.getCodeLat(getApplicationContext());
                        String lngSave = SharedPreferenceHelper.getCodeLng(getApplicationContext());
                        if (!TextUtils.isEmpty(latSave) && !TextUtils.isEmpty(lngSave)
                                && Double.parseDouble(latSave) > 0 && Double.parseDouble(lngSave) > 0) {
                            startSearch(Double.parseDouble(latSave), Double.parseDouble(lngSave), "");
                        } else {
                            dismissLoadingDialog();
                        }
                    }
                } else {
                    dismissLoadingDialog();
                }
            }
        });
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    /**
     * 初始化
     */
    private void startSearch(double latitude, double longitude, String cityCode) {
        String mType = "餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|公司企业|道路附属设施|地名地址信息|公共设施";
        PoiSearch.Query mQuery = new PoiSearch.Query("", mType, cityCode);
        mQuery.setPageSize(20);
        mQuery.setPageNum(0);
        PoiSearch mPoiSearch = new PoiSearch(this, mQuery);
        mPoiSearch.setOnPoiSearchListener(this);
        mPoiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude, longitude), 1000, true));//
        mPoiSearch.searchPOIAsyn();// 异步搜索
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int rCode) {
        dismissLoadingDialog();
        if (rCode == 1000) {
            if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                if (poiItems != null && poiItems.size() > 0) {
                    List<PoiBean> poiBeans = new ArrayList<>();
                    String city = "";
                    for (PoiItem item : poiItems) {
                        PoiBean bean = new PoiBean();
                        bean.title = item.getTitle();
                        bean.detail = item.getSnippet();
                        bean.addCity = item.getCityName();
                        poiBeans.add(bean);
                        if (TextUtils.isEmpty(city)) {
                            city = item.getCityName();
                        }
                    }
                    //第一条不显示位置
                    PoiBean first = new PoiBean();
                    first.isSelected = true;
                    first.title = getResources().getString(R.string.not_show);
                    //第二条  显示城市
                    PoiBean second = new PoiBean();
                    second.title = city;
                    poiBeans.add(0, first);
                    poiBeans.add(1, second);
                    mPositionRecyclerAdapter.loadData(poiBeans);
                }
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @OnClick({R.id.cancel_tv, R.id.finish_tv, R.id.search_rl})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_tv: {//取消
                finish();
                break;
            }
            case R.id.finish_tv: {//完成
                String selectedPosition = mPositionRecyclerAdapter.getSelectData();
                if (TextUtils.isEmpty(selectedPosition)) {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.position_invalidate);
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(Constant.CHOOSED_POSITION, selectedPosition);
                setResult(RESULT_OK, intent);
                finish();
                break;
            }
            case R.id.search_rl: {//搜索
                showSearchDialog();
                break;
            }
        }
    }

    /**
     * 显示奖励规则
     */
    private void showSearchDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_search_position_layout, null);
        setDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            params.height = outSize.y;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置view
     */
    private void setDialogView(View view, final Dialog mDialog) {
        //取消
        final TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //列表
        final RecyclerView content_rv = view.findViewById(R.id.content_rv);
        final SearchPositionRecyclerAdapter mSearchAdapter = new SearchPositionRecyclerAdapter(SelectPositionActivity.this);
        content_rv.setLayoutManager(new LinearLayoutManager(SelectPositionActivity.this));
        content_rv.setAdapter(mSearchAdapter);
        //搜索
        EditText search_et = view.findViewById(R.id.search_et);
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && s.length() > 0) {
                    startSearch(s.toString(), mSearchAdapter, content_rv);
                }
            }
        });
        mSearchAdapter.setOnSearchItemClickListener(new SearchPositionRecyclerAdapter.OnSearchItemClickListener() {
            @Override
            public void onSearchItemClick(String address) {
                if (!TextUtils.isEmpty(address)) {
                    Intent intent = new Intent();
                    intent.putExtra(Constant.CHOOSED_POSITION, address);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.position_invalidate);
                }
            }
        });

        showSpan(search_et);
    }

    /**
     * 调用键盘
     */
    private void showSpan(final EditText search_et) {
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (search_et.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(search_et, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            }
        }, 400);
    }

    /**
     * 开始搜索
     */
    private void startSearch(final String searchText, final SearchPositionRecyclerAdapter adapter,
                             final RecyclerView content_rv) {
        //第二个参数传入null或者“”代表在全国进行检索，否则按照传入的city进行检索
        InputtipsQuery inputQuery = new InputtipsQuery(searchText, mCityName);
        inputQuery.setCityLimit(true);//限制在当前城市
        Inputtips inputTips = new Inputtips(SelectPositionActivity.this, inputQuery);
        inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
            @Override
            public void onGetInputtips(List<Tip> list, int i) {
                if (i == 1000 && list != null && list.size() > 0) {
                    List<SearchPoiBean> searchPoiBeans = new ArrayList<>();
                    for (Tip tip : list) {
                        SearchPoiBean bean = new SearchPoiBean();
                        bean.name = tip.getName();
                        bean.address = tip.getDistrict();
                        bean.searchText = searchText;
                        bean.addCity = mCityName;
                        searchPoiBeans.add(bean);
                    }
                    adapter.loadData(searchPoiBeans);
                    content_rv.setVisibility(View.VISIBLE);
                }
            }
        });
        inputTips.requestInputtipsAsyn();
    }

}
