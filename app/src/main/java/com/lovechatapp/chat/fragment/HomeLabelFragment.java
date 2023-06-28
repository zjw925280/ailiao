package com.lovechatapp.chat.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.RadioButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.dialog.CityPickerDialog;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.view.NestedRadioGroup;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 聊场（首页带banner带选项卡）
 */
public class HomeLabelFragment extends HomeDateBannerFragment implements NestedRadioGroup.OnCheckedChangeListener {

    /**
     * 默认选中
     */
    @BindView(R.id.home_type2)
    RadioButton defaultRb;

    @BindView(R.id.same_city_rb)
    RadioButton sameCityRb;

    @BindView(R.id.home_page_rg)
    NestedRadioGroup homePageRg;

    private AMapLocation location;

    private String selectedCity;

    @Override
    protected int initLayout() {
        return R.layout.fragment_home_label;
    }

    @Override
    protected void getData() {
        homePageRg.setOnCheckedChangeListener(this);
        defaultRb.setChecked(true);
        startLocation();
    }

    @Override
    public void onCheckedChanged(NestedRadioGroup group, int checkedId) {
        mRefreshLayout.finishLoadMore(0);
        mRefreshLayout.finishRefresh(0);
        String mCurrentQueryType = group.findViewById(checkedId).getTag().toString();
        requester.setParam("queryType", mCurrentQueryType);
        if (checkedId == sameCityRb.getId()) {
            if (!requester.getParamMap().containsKey("t_city")) {
                if (location != null) {
                    requester.setParam("t_city", location.getCity());
                } else {
                    startLocation();
                }
            }
            if (requester.getParamMap().containsKey("t_city")) {
                mRefreshLayout.autoRefresh();
                requester.cancel();
                requester.onRefresh();
            }
        } else {
            sameCityRb.setSelected(false);
            mRefreshLayout.autoRefresh();
            requester.cancel();
            requester.onRefresh();
        }
    }

    @OnClick({R.id.same_city_rb})
    public void onClick(View v) {
        if (sameCityRb.isChecked()) {
            if (sameCityRb.isSelected()) {
                showCityChooser();
            }
            sameCityRb.setSelected(true);
        }
    }

    private CityPickerDialog cityPickerDialog;

    private void showCityChooser() {
        if (cityPickerDialog == null) {
            cityPickerDialog = new CityPickerDialog(getActivity()) {
                @Override
                public void onSelected(String city, String city2) {
                    if (sameCityRb.isChecked()) {
                        requester.setParam("t_city", city2);
                        selectedCity = city2;
                        sameCityRb.setText(selectedCity);
                        mRefreshLayout.autoRefresh();
                    }
                }
            };
        }
        cityPickerDialog.show();
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
        mLocationClient.setLocationListener(aMapLocation -> {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {//成功
                    location = aMapLocation;
                    //保存在本地
                    double lat = aMapLocation.getLatitude();
                    double lng = aMapLocation.getLongitude();
                    SharedPreferenceHelper.saveCode(
                            mContext.getApplicationContext(),
                            String.valueOf(lat),
                            String.valueOf(lng));
                    SharedPreferenceHelper.saveCity(mContext, aMapLocation.getCity());
                    if (selectedCity == null) {
                        sameCityRb.setText(location.getCity());
                        requester.setParam("t_city", location.getCity());
                    }
                } else {//失败
                    LogUtil.i("Distance: 定位失败 :" + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        });
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

}