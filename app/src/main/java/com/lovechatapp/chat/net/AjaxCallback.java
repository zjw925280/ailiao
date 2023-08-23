package com.lovechatapp.chat.net;


import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.lovechatapp.chat.BuildConfig;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.UpdateBean;
import com.lovechatapp.chat.util.LogUtil;
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
                Log.e("http数据", "response.body()="+str);
            }

            BaseResponse baseResponse = JSON.parseObject(str, BaseResponse.class);

            if (baseResponse != null) {

                switch (baseResponse.m_istatus) {

                    case -1010: {
                        Log.e("code","1010="+response.isSuccessful());
                        AppManager.getInstance().exit(baseResponse.m_strMessage, true);
                        throw new IllegalAccessException("封号");
                    }

                    case -1020: {
                        Log.e("code","1020="+response.isSuccessful());
                        AppManager.getInstance().exit(AppManager.getInstance().getString(R.string.token_invalid), true);
                        throw new IllegalAccessException("登录失效");
                    }

                    case -1030: {
                        Log.e("code","1030="+response.isSuccessful());
                        AppManager.getInstance().updateApp(JSON.parseObject(baseResponse.m_object.toString(), UpdateBean.class));
                        throw new IllegalAccessException("更新版本");
                    }

                }

            }

            return JSON.parseObject(str, types[0]);
        }
        return null;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        if (call != null && e != null) {
            LogUtil.e("==--", call.request().url().toString() + ":" + e.getMessage());
        }
    }

}
