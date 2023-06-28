package com.lovechatapp.chat.net;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lovechatapp.chat.BuildConfig;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.UpdateBean;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public abstract class PageRequester<T> extends Callback<BaseResponse> {

    private int page = 0;

    private int size = 10;

    private String url;

    private Map<String, Object> paramMap = new HashMap<>();

    private boolean isRefresh;

    private Type type;

    private onPageDataListener onPageListener;

    private boolean requesting;

    private boolean noMore;

    public abstract void onSuccess(List<T> list, boolean isRefresh);

    public PageRequester() {
        type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public PageRequester(Type type) {
        this.type = type;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void onRefresh() {
        if (isRequesting()) {
            return;
        }
        page = 0;
        post();
    }

    public void onLoadMore() {
        post();
    }

    @Override
    public void onResponse(BaseResponse response, int id) {

    }

    @Override
    public BaseResponse parseNetworkResponse(Response response, int id) throws Exception {

        String str = response.body().string().replace("(null)", "");

        final BaseResponse<String> resultResponse = JSON.parseObject(str, new TypeReference<BaseResponse<String>>() {
        });

        if (resultResponse != null) {

            switch (resultResponse.m_istatus) {

                case -1010: {
                    AppManager.getInstance().exit(resultResponse.m_strMessage, true);
                    throw new IllegalAccessException("封号");
                }

                case -1020: {
                    AppManager.getInstance().exit(AppManager.getInstance().getString(R.string.token_invalid), true);
                    throw new IllegalAccessException("登录失效");
                }

                case -1030: {
                    AppManager.getInstance().updateApp(JSON.parseObject(resultResponse.m_object.toString(), UpdateBean.class));
                    throw new IllegalAccessException("更新版本");
                }

            }

        }

        if (BuildConfig.DEBUG) {
            LogUtil.i("==--http", response.request().url().toString() + ": " + str);
        }
        if (isDataError(resultResponse)) {
            OkHttpUtils.getInstance().getDelivery().execute(() -> {
                if (resultResponse != null) {
                    dataError(resultResponse.m_istatus, resultResponse.m_strMessage);
                } else {
                    dataError(0, "数据出错");
                }
            });
        } else {
            final List<T> list = new ArrayList<>();
            if (resultResponse.m_object.startsWith("[")) {
                JSONArray jsonArray = JSON.parseArray(resultResponse.m_object);
                for (Object data : jsonArray) {
                    T t = JSON.parseObject(data.toString(), type);
                    if (t != null)
                        list.add(t);
                }
            } else {
                JSONObject jsonData = JSON.parseObject(resultResponse.m_object);
                for (Object data : jsonData.getJSONArray("data")) {
                    T t = JSON.parseObject(data.toString(), type);
                    if (t != null)
                        list.add(t);
                }
            }
            if (list.size() > 0)
                page++;

            noMore = list.size() < size;

            OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
                @Override
                public void run() {
                    onSuccess(list, isRefresh);
                }
            });
        }
        return resultResponse;
    }

    @Override
    public void onAfter(int id) {
        if (onPageListener != null)
            onPageListener.onRefreshEnd();
        if (onPageListener != null)
            onPageListener.finishLoadMore(noMore);
        requesting = false;
    }

    private boolean isDataError(BaseResponse baseResponse) {
        return baseResponse == null || baseResponse.m_istatus != 1 || baseResponse.m_object == null;
    }

    @Override
    public void onError(Call call, final Exception e, int id) {
        e.printStackTrace();
        OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
            @Override
            public void run() {
                onFailure(e.getMessage());
            }
        });
    }

    public void onFailure(String message) {
    }

    public void dataError(int code, String message) {
        onFailure(message);
    }

    public PageRequester setApi(String api) {
        url = api;
        return this;
    }

    public PageRequester setParam(String key, Object value) {
        paramMap.put(key, value);
        return this;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    protected void post() {
        isRefresh = page == 0;
        if (TextUtils.isEmpty(url))
            throw new NullPointerException("api is null, please set api first!");
        requesting = true;
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("size", size);
        paramMap.put("page", page + 1);
        OkHttpUtils.post().url(url)
                .addParams("param", ParamUtil.getParam(paramMap))
                .tag(this)
                .build()
                .execute(this);
    }

    public void cancel() {
        OkHttpUtils.getInstance().cancelTag(this);
        requesting = false;
    }

    public boolean isRequesting() {
        return requesting;
    }

    public void setOnPageDataListener(onPageDataListener onPageListener) {
        this.onPageListener = onPageListener;
    }

    public interface onPageDataListener {

        void autoRefresh();

        void onRefreshEnd();

        void finishLoadMore(boolean noMore);
    }

    public static class SimplePageDataListener implements onPageDataListener {

        @Override
        public void autoRefresh() {

        }

        @Override
        public void onRefreshEnd() {

        }

        @Override
        public void finishLoadMore(boolean noMore) {

        }

    }
}