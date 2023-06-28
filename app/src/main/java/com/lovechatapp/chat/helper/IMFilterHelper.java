package com.lovechatapp.chat.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.tencent.qcloud.tim.uikit.modules.message.ImFilter;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfoUtil;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;


/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述:  IM过滤词帮助类
 * 作者：
 * 创建时间：2019/5/31
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class IMFilterHelper implements ImFilter {

    @SuppressLint("StaticFieldLeak")
    private static IMFilterHelper mInstance;

    private String mFilterWord;

    /**
     * 单例类
     */
    public static IMFilterHelper getInstance() {
        if (mInstance == null) {
            synchronized (IMFilterHelper.class) {
                if (mInstance == null) {
                    mInstance = new IMFilterHelper();
                }
            }
        }
        return mInstance;
    }

    private IMFilterHelper() {
        MessageInfoUtil.setImFilter(this);
    }

    /**
     * 设置过滤池
     */
    public void setFilterWord(Context context, String filterWord) {
        mFilterWord = filterWord;
        SharedPreferenceHelper.setFilterWord(context, filterWord);
    }

    /**
     * 获取过滤池
     */
    public String getFilterWord(Context context) {
        //1.如果内存中有,则去内存中的
        if (!TextUtils.isEmpty(mFilterWord)) {
            return mFilterWord;
        }
        //2.内存中没有,则从缓存中取
        String saveFilter = SharedPreferenceHelper.getFilterWord(context);
        if (!TextUtils.isEmpty(saveFilter)) {
            //把取出来的赋值到内存中
            mFilterWord = saveFilter;
        }
        return saveFilter;
    }

    /**
     * 替换过滤词
     */
    public String switchFilterWord(Context context, String inputWord) {
        long last = System.currentTimeMillis();
        String saveFilterWord = getFilterWord(context);
        if (!TextUtils.isEmpty(inputWord) && !TextUtils.isEmpty(saveFilterWord)) {
            //将过滤词转为数组
            String[] filters = saveFilterWord.split("\\|");
            if (filters.length > 0) {
                for (String filterItem : filters) {
                    if (inputWord.contains(filterItem)) {
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < filterItem.length(); i++) {
                            builder.append("*");
                        }
                        inputWord = inputWord.replace(filterItem, builder.toString());
                    }
                }
            }
        }
        LogUtil.i("耗时: " + (System.currentTimeMillis() - last));
        return inputWord;
    }

    //------------------------------IM过滤词--------------------------
    public final void updateImFilterWord() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        OkHttpUtils.post().url(ChatApi.GET_IM_FILTER())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<String>>() {
            @Override
            public void onResponse(BaseResponse<String> response, int id) {
                LogUtil.i("IM过滤词: " + JSON.toJSONString(response));
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String filter = response.m_object;
                    if (!TextUtils.isEmpty(filter) && filter.contains("|")) {
                        IMFilterHelper.getInstance().setFilterWord(AppManager.getInstance(), filter);
                    }
                }
            }
        });
    }

    @Override
    public String switchFilterWord(String s) {
        return switchFilterWord(AppManager.getInstance(), s);
    }
}