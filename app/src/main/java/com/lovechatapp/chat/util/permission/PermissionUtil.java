package com.lovechatapp.chat.util.permission;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.util.LogUtil;

import java.util.List;

public class PermissionUtil {

    /**
     * 48小时（毫秒）
     */
    private static final long hours48 = 48 * 60 * 60 * 1000;

    private static SharedPreferences.Editor editor;
    private static SharedPreferences sharedPreferences;

    /**
     * 定位权限
     */
    public static String[] locationPermission = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    /**
     * 文件读写权限
     */
    public final static String[] filePermission = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    /**
     * 请求权限
     */
    public static void requestPermissions(Context context, OnPermissionListener listener, String... permissions) {
        if (context == null || listener == null) {
            return;
        }
        if (Build.VERSION.SDK_INT <= 22) {
            listener.onPermissionGranted();
        } else {
            List<String> deniedPermissions = CheckPermissionActivity.getDeniedPermissions(context, permissions);
            if (deniedPermissions.size() == 0) {
                listener.onPermissionGranted();
            } else {
                if (checkRequestTime(permissions)) {
                    CheckPermissionActivity.start(context, permissions, listener);
                } else {
                    listener.onPermissionDenied();
                }
            }
        }
    }

    /**
     * 检查该权限的请求时长是否超过48小时，应用宝要求
     */
    private static boolean checkRequestTime(String... permissions) {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AppManager.getInstance());
        }
        if (editor == null) {
            editor = sharedPreferences.edit();
        }
        long currentTime = System.currentTimeMillis();
        for (String permission : permissions) {
            if (currentTime - sharedPreferences.getLong(permission, 0) < hours48) {
                LogUtil.e("==--", "权限申请间隔未超过48小时");
                return false;
            }
        }
        for (String permission : permissions) {
            editor.putLong(permission, currentTime);
        }
        editor.apply();
        return true;
    }

    public interface OnPermissionListener {

        void onPermissionGranted();//授权

        void onPermissionDenied();//拒绝
    }
}