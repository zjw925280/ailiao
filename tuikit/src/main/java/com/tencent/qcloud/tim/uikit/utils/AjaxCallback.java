package com.tencent.qcloud.tim.uikit.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.tencent.qcloud.tim.uikit.BuildConfig;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：返回数据解析封装
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public abstract class AjaxCallback<T> extends Callback<T> {

    private Type[] types;

    public AjaxCallback() {
        types = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {

        if (response.isSuccessful() && types != null && types.length > 0) {

            String str = response.body().string();

            if (BuildConfig.DEBUG) {
                Log.i("==--http", response.request().url().toString() + ": " + str);
            }

            BaseResponse baseResponse = new Gson().fromJson(str, BaseResponse.class);

            if (baseResponse != null) {

                return new Gson().fromJson(str, types[0]);
            }

            return null;
        }
        return null;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        if (call != null && e != null) {
            Log.e("==--", call.request().url().toString() + ":" + e.getMessage());
        }
    }
}
