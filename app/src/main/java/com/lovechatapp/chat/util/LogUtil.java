package com.lovechatapp.chat.util;

import android.util.Log;

import com.lovechatapp.chat.BuildConfig;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：log工具类
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class LogUtil {

    private static final String TAG = "==--";

    public static void d(String tag, String msg) {
        if (BuildConfig.debug) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable t) {
        if (BuildConfig.debug) {
            Log.d(tag, msg, t);
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.debug) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Exception e) {
        if (BuildConfig.debug) {
            Log.w(tag, msg, e);
        }
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.debug) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Exception e) {
        if (BuildConfig.debug) {
            Log.w(tag, msg, e);
        }
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.debug) {
            Log.i(tag, msg);
        }
    }

    public static void i(String msg) {
        if (BuildConfig.debug) {
            Log.i(TAG, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.debug) {
            Log.v(tag, msg);
        }
    }

}
