package com.lovechatapp.chat.helper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseBean;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 位置帮助类
 */
public class LocationHelper implements PoiSearch.OnPoiSearchListener {

    private static LocationHelper locationHelper;

    private boolean hasPerMission;

    private String latitude;

    private String longitude;

    private AMapLocationClient mLocationClient;

    private List<OnCommonListener<Boolean>> listeners;

    private Handler handler = new Handler(Looper.getMainLooper());

    private AMapLocation mapLocation;

    private LocationHelper() {

        listeners = new ArrayList<>();

        hasPerMission = checkPermission();

        latitude = SharedPreferenceHelper.getCodeLat(AppManager.getInstance());
        longitude = SharedPreferenceHelper.getCodeLng(AppManager.getInstance());

        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);

        mLocationClient = new AMapLocationClient(AppManager.getInstance());
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        mapLocation = aMapLocation;
                        double lat = aMapLocation.getLatitude();
                        double lng = aMapLocation.getLongitude();
                        latitude = String.valueOf(lat);
                        longitude = String.valueOf(lng);
                        SharedPreferenceHelper.saveCode(AppManager.getInstance(), latitude, longitude);
                        uploadCode(lat, lng);
//                        LocationHelper.get().search(lat, lng);
                        notifyLocation(true);
                        LogUtil.i("定位成功");
                    } else {
                        notifyLocation(false);
                        LogUtil.i("定位失败 :" + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo());
                    }
                }
            }
        });
    }

    public AMapLocation getLocation() {
        return mapLocation;
    }

    public static LocationHelper get() {
        if (locationHelper == null) {
            synchronized (LocationHelper.class) {
                if (locationHelper == null) {
                    locationHelper = new LocationHelper();
                }
            }
        }
        return locationHelper;
    }

    /**
     * 回调定位结果
     */
    private void notifyLocation(final boolean ok) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (OnCommonListener<Boolean> listener : listeners) {
                    listener.execute(ok);
                }
            }
        });
    }

    public final String getLatitude() {
        return latitude;
    }

    public final String getLongitude() {
        return longitude;
    }

    /**
     * 获取经纬度
     */
    public final void getLocation(OnCommonListener<Boolean> listener) {
        if (listener != null) {
            if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
                listener.execute(true);
            } else {
                if (!listeners.contains(listener)) {
                    listeners.add(listener);
                }
            }
        }
    }

    /**
     * 取消定位回调
     */
    public final void cancelGetLocation(OnCommonListener<Boolean> listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    public final boolean isHasPerMission() {
        return hasPerMission;
    }

    /**
     * 检查权限
     */
    public final boolean checkPermission() {
        return ActivityCompat
                .checkSelfPermission(
                        AppManager.getInstance(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat
                .checkSelfPermission(
                        AppManager.getInstance(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 权限回调
     */
    public final void onRequestPermissionsResult() {
        hasPerMission = checkPermission();
        if (hasPerMission) {
            startLocation();
        } else {
            notifyLocation(false);
        }
    }

    /**
     * 开始定位
     */
    public final void startLocation() {
        if (hasPerMission) {
            mLocationClient.startLocation();
        } else {
            for (OnCommonListener<Boolean> listener : listeners) {
                listener.execute(false);
            }
            listeners.clear();
        }
    }

    /**
     * 上传坐标
     */
    private void uploadCode(double lat, double lng) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("lat", lat);
        paramMap.put("lng", lng);
        OkHttpUtils.post().url(ChatApi.UPLOAD_COORDINATE())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    LogUtil.i("上传坐标成功");
                }
            }
        });
    }

    /**
     * 搜索附近2km内的小区、酒店
     * 非vip男用户
     */
    public final void search(double latitude, double longitude) {

        if (AppManager.getInstance().getUserInfo().t_sex == 0 ||
                AppManager.getInstance().getUserInfo().t_is_vip == 0)
            return;

        PoiSearch.Query query = new PoiSearch.Query(null, "宾馆酒店|住宅小区");
        query.setPageSize(10);
        PoiSearch.SearchBound searchBound =
                new PoiSearch.SearchBound(new LatLonPoint(latitude, longitude), 2000);
        PoiSearch poiSearch = new PoiSearch(AppManager.getInstance(), query);
        poiSearch.setBound(searchBound);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (poiResult.getPois().size() > 0) {
            List<PositionBean> list = new ArrayList<>();
            String city = null;
            for (PoiItem poiItem : poiResult.getPois()) {
                PositionBean positionBean = new PositionBean();
                positionBean.title = poiItem.getTitle();
                positionBean.latitude = poiItem.getLatLonPoint().getLatitude();
                positionBean.longitude = poiItem.getLatLonPoint().getLongitude();
                positionBean.address = poiItem.getProvinceName() +
                        poiItem.getCityName() +
                        poiItem.getAdName() +
                        poiItem.getSnippet();
                if (city == null) {
                    city = poiItem.getCityName();
                    if (TextUtils.isEmpty(city)) {
                        city = poiItem.getProvinceName();
                    }
                }
                list.add(positionBean);
            }
//            uploadPositions(list, city);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

//    /**
//     * 上传周围小区、酒店
//     */
//    private void uploadPositions(List<PositionBean> mapInfo, String city) {
//        Map<String, Object> paramMap = new HashMap<>();
//        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
//        paramMap.put("city", city);
//        paramMap.put("mapInfo", mapInfo);
//        OkHttpUtils.post().url(ChatApi.getUserNearbyInfo)
//                .addParams("param", ParamUtil.getParam(paramMap))
//                .build().execute(new AjaxCallback<BaseResponse<String>>() {
//            @Override
//            public void onResponse(BaseResponse<String> response, int id) {
//            }
//        });
//    }

    public static class PositionBean extends BaseBean {
        public double longitude;
        public double latitude;
        public String title;
        public String address;
    }
}