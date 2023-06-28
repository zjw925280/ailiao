package com.lovechatapp.chat.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;

import static android.content.Context.SENSOR_SERVICE;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：和设备相关的工具类
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class DevicesUtil {

    /**
     * dip 转px
     */
    public static int dp2px(Context context, float dip) {
        return (int) (context.getResources().getDisplayMetrics().density * dip + 0.5f);
    }

    /**
     * 获取当前屏幕宽度
     * <p/>
     * return 屏幕宽度
     */
    public static int getScreenW(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    /**
     * 获取当前屏幕高度
     * <p/>
     * return 屏幕高度
     */
    public static int getScreenH(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    /**
     * 判断是否存在光传感器来判断是否为模拟器
     * 部分真机也不存在温度和压力传感器。其余传感器模拟器也存在。
     *
     * @return true 为模拟器
     */
    public static Boolean notHasLightSensorManager(Context context) {
        try {
            SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
            if (sensorManager != null) {
                Sensor sensor8 = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); //光
                return null == sensor8;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


}
