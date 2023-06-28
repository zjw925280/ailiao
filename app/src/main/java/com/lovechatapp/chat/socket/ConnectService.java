package com.lovechatapp.chat.socket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.helper.RecordUploader;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.ttt.QiNiuChecker;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：开启保活服务，不需要关闭
 * 作者：lyf
 * 创建时间：2017/5/20.
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ConnectService extends Service {

    private HandlerThread connectThread;
    private volatile ServiceHandler mServiceHandler;
    private final int CHECK = 0;
    private final long INTERVAL = 10 * 1000L;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = getPackageName() + ".channelid";
            String CHANNEL_NAME = getPackageName() + ".channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
            Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID).build();
            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //使用子线程开启连接
        if (connectThread == null) {
            connectThread = new HandlerThread("live");
            connectThread.start();
            mServiceHandler = new ServiceHandler(connectThread.getLooper());
            mServiceHandler.sendEmptyMessageDelayed(CHECK, INTERVAL);
            mServiceHandler.post(new Runnable() {
                @Override
                public void run() {
                    AppManager.getInstance().updateLoginTime();
                    getQQNumber();
                    RecordUploader.get().updateSaveRecord();
                    QiNiuChecker.get().checkEnable();
                }
            });
        }
        LogUtil.i(" -------------服务 onStartCommand");
        startConnect();
        return START_STICKY;
    }

    /**
     * 获取QQ客服号
     */
    private void getQQNumber() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        String encodeStr = ParamUtil.getParam(paramMap);
        OkHttpUtils.post().url(ChatApi.GET_SERVICE_QQ())
                .addParams("param", encodeStr)
                .build().execute(new AjaxCallback<BaseResponse<String>>() {
            @Override
            public void onResponse(BaseResponse<String> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    if (!TextUtils.isEmpty(response.m_object)) {
                        String mQQNumber = response.m_object;
                        LogUtil.i("QQ客服: " + mQQNumber);
                        String saveQQ = SharedPreferenceHelper.getQQ(getApplicationContext());
                        if (!TextUtils.isEmpty(mQQNumber) && !saveQQ.equals(mQQNumber)) {
                            SharedPreferenceHelper.saveQQ(getApplicationContext(), mQQNumber);
                        }
                    }
                }
            }
        });
    }

    private void startConnect() {
        ConnectHelper.get().start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (connectThread != null) {
            connectThread.quit();
            connectThread = null;
        }
    }

    class ServiceHandler extends Handler {

        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            mServiceHandler.sendEmptyMessageDelayed(CHECK, INTERVAL);
        }
    }
}