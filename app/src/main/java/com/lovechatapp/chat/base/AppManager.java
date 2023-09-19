package com.lovechatapp.chat.base;

import android.app.Activity;
import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.lovechatapp.chat.bean.DateBean;
import com.lovechatapp.chat.dialog.AgreementDialog;
import com.mcxiaoke.packer.helper.PackerNg;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.SplashActivity;
import com.lovechatapp.chat.activity.UpdateActivity;
import com.lovechatapp.chat.bean.UpdateBean;
import com.lovechatapp.chat.rtc.RtcManager;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.mm.opensdk.utils.Log;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.lovechatapp.chat.BuildConfig;
import com.lovechatapp.chat.activity.ScrollLoginActivity;
import com.lovechatapp.chat.bean.ChatUserInfo;
import com.lovechatapp.chat.bean.UserCenterBean;
import com.lovechatapp.chat.bean.VipInfoBean;
import com.lovechatapp.chat.camera.beauty.BeautyManager;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.ActivityRegister;
import com.lovechatapp.chat.helper.IMHelper;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.im.ConfigHelper;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.socket.ConnectHelper;
import com.lovechatapp.chat.socket.ConnectService;
import com.lovechatapp.chat.socket.SocketMessageManager;
import com.lovechatapp.chat.socket.WakeupService;
import com.lovechatapp.chat.util.FloatingManagerOne;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.OkHttpClient;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：自定义Application,用于初始化,储存一些全局变量,如UserInfo
 * 作者：
 * 创建时间：2018/6/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AppManager extends Application {

    public static final String APP_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "loveChat" + File.separator;

    private ChatUserInfo mUserInfo;

    private static AppManager mInstance;

    //判断微信支付是充值VIP 还是充值约豆
    private boolean mIsWeChatForVip = false;

    //判断微信登录是绑定提现账号,还是登录页面的微信登录
    private boolean mIsWeChatBindAccount = false;

    //判断是首页微信分享还是
    private boolean mIsMainPageShareQun = false;

    //用于视频接通后提示用户
    public String mVideoStartHint = "";

    private ActivityObserve activityObserve;

    private final HashMap<Integer, String> dateStatusMap = new HashMap<>();

    /**
     * http访问令牌
     */
    private String token;

    /**
     * 限制截图开关
     */
    public static final boolean SecureEnable = true;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        dateStatusMap.put(DateBean.INVITE_TYPE_NEW, "邀请中");
        dateStatusMap.put(DateBean.INVITE_TYPE_ACCEPTED, "已同意");
        dateStatusMap.put(DateBean.INVITE_TYPE_REFUSED, "已拒绝");
        dateStatusMap.put(DateBean.INVITE_TYPE_CODE_VERIFY, "已赴约");
        dateStatusMap.put(DateBean.INVITE_TYPE_AUTO_VERIFY, "已赴约");
        dateStatusMap.put(DateBean.INVITE_TYPE_CANCELED, "已取消");
        dateStatusMap.put(DateBean.INVITE_TYPE_INACTIVE, "已失效");
        //监听应用前后台状态
        ActivityRegister.get();

        //监听activity生命周期
        registerActivityLifecycleCallbacks(activityObserve = new ActivityObserve());

        //crash
//        CrashHandler.getInstance().init(this);
//        CaocConfig.Builder.create()
//                //程序在后台时，发生崩溃的三种处理方式
//                //BackgroundMode.BACKGROUND_MODE_SHOW_CUSTOM: //当应用程序处于后台时崩溃，也会启动错误页面，
//                //BackgroundMode.BACKGROUND_MODE_CRASH:      //当应用程序处于后台崩溃时显示默认系统错误（一个系统提示的错误对话框），
//                //BackgroundMode.BACKGROUND_MODE_SILENT:     //当应用程序处于后台时崩溃，默默地关闭程序！
//                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
//                .enabled(!BuildConfig.DEBUG)     //false表示对崩溃的拦截阻止。用它来禁用customactivityoncrash框架
//                .showErrorDetails(true) //这将隐藏错误活动中的“错误详细信息”按钮，从而隐藏堆栈跟踪,—》针对框架自带程序崩溃后显示的页面有用(DefaultErrorActivity)。。
//                .showRestartButton(false)    //是否可以重启页面,针对框架自带程序崩溃后显示的页面有用(DefaultErrorActivity)。
//                .trackActivities(true)     //错误页面中显示错误详细信息；针对框架自带程序崩溃后显示的页面有用(DefaultErrorActivity)。
//                .minTimeBetweenCrashesMs(2000)      //定义应用程序崩溃之间的最短时间，以确定我们不在崩溃循环中。比如：在规定的时间内再次崩溃，框架将不处理，让系统处理！
//                .errorDrawable(R.mipmap.ic_launcher)     //崩溃页面显示的图标
//                .restartActivity(SplashActivity.class)      //重新启动后的页面
////                    .errorActivity(ErrorActivity.class) //程序崩溃后显示的页面
////                    .eventListener(new CustomEventListener())//设置监听
//                .apply();



        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean agree = sp.getBoolean("agree", false);
        Log.e("agree","agree="+agree);
   if (agree){

       Log.e("那個先","AppManager");
       File folder = new File(APP_CACHE_PATH);
       if (!folder.exists()) {
           folder.mkdirs();
       }

       Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
           @Override
           public void uncaughtException(Thread thread, Throwable throwable) {
               // 在这里处理异常，可以写入文件或执行其他操作
               // 请确保处理异常的操作是快速且可靠的，因为应用即将崩溃
               uncaughtException1(thread,throwable);
           }
       });
       initsdk();

   }

        token = SharedPreferenceHelper.getToken();
    }
    public void uncaughtException1(Thread thread, Throwable throwable) {
        try {
            // 创建一个文件来保存异常信息
            File logFile = new File(getExternalFilesDir(null), "语聊Log.txt");

            // 将异常信息写入文件
            PrintWriter writer = new PrintWriter(new FileWriter(logFile, true));
            throwable.printStackTrace(writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 终止应用程序
        System.exit(1);
    }
    public void initsdk(){
        //极光
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        JPushInterface.init(this);

        //IM初始化
        initTIM();

        initHttp();

        initHttpCache();

        //悬浮消息
        //FloatingMessageManager.init();

        //美颜
        BeautyManager.get().init();

        //rtc
        RtcManager.get();

        //消息分发初始化
        SocketMessageManager.get();
    }

    public HashMap<Integer, String> getDateStatusMap() {
        return dateStatusMap;
    }

    public final String getShareId() {
        String channel = PackerNg.getChannel(this);
        if (TextUtils.isEmpty(channel))
            channel = "";
        return channel;
    }

    /**
     * 初始化腾讯IM
     */
    private void initTIM() {

        //ImUi
        TUIKit.init(this, Constant.TIM_APP_ID, new ConfigHelper().getConfigs());

        //初始化 SDK 基本配置
        TIMSdkConfig config = new TIMSdkConfig(Constant.TIM_APP_ID);

        // 是否在控制台打印Log?
        config.enableLogPrint(false);

        //初始化 SDK
        TIMManager.getInstance().init(getApplicationContext(), config);

        IMHelper.init();
    }

    private void initHttp() {
        BasicParamsInterceptor basicParamsInterceptor = new BasicParamsInterceptor.Builder()
                .addHeaderParam("appSystem", String.valueOf(0))
                .addHeaderParam("appVersionCode", BuildConfig.VERSION_NAME)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(basicParamsInterceptor)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    /**
     * 初始化httpCache
     */
    private void initHttpCache() {
        try {
            File cacheDir = new File(getCacheDir(), "http");
            HttpResponseCache.install(cacheDir, 1024 * 1024 * 128);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatUserInfo getUserInfo() {
        if (mUserInfo != null) {
            return mUserInfo;
        } else {
            return mUserInfo = SharedPreferenceHelper.getAccountInfo(getApplicationContext());
        }
    }

    public boolean isFirstInUserInfo(){
        return SharedPreferenceHelper.getIsFirstInUserInfo(this);
    }

    public void setUserInfoGet(){
        SharedPreferenceHelper.setUserInfoGet(this);
    }

    public final String getToken() {
        return token;
    }

    public final void setToken(String token) {
        this.token = token;
    }

    public void setUserInfo(ChatUserInfo userInfo) {
        this.mUserInfo = userInfo;

    }

    public static AppManager getInstance() {
        return mInstance;
    }

    public void setIsWeChatForVip(boolean isWeChatForVip) {
        mIsWeChatForVip = isWeChatForVip;
    }

    public boolean getIsWeChatForVip() {
        return mIsWeChatForVip;
    }

    public void setIsWeChatBindAccount(boolean isWeChatBindAccount) {
        mIsWeChatBindAccount = isWeChatBindAccount;
    }

    public boolean getIsWeChatBindAccount() {
        return mIsWeChatBindAccount;
    }

    public boolean getIsMainPageShareQun() {
        return mIsMainPageShareQun;
    }

    public void setIsMainPageShareQun(boolean mIsMainPageShareQun) {
        this.mIsMainPageShareQun = mIsMainPageShareQun;
    }

    public final ActivityObserve getActivityObserve() {
        return activityObserve;
    }

    public final void updateApp(UpdateBean updateBean) {
        UpdateActivity.start(updateBean);
    }

    public synchronized final void exit(String beenCloseDes, boolean been_close) {
        if (AppManager.getInstance().getUserInfo().t_id == 0)
            return;
        try {
            activityObserve.finishAllExcludeLogin();
            //断开socket
            ConnectHelper.get().onDestroy();
            FloatingManagerOne.clear();
            //调用接口退出
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("userId", getUserInfo().t_id);
            OkHttpUtils.post().url(ChatApi.LOGOUT())
                    .addParams("param", ParamUtil.getParam(paramMap))
                    .build().execute(new AjaxCallback<BaseResponse>() {
                        @Override
                        public void onResponse(BaseResponse response, int id) {
                            if (response != null && response.m_istatus == NetCode.SUCCESS) {
                                LogUtil.i("登出服务器成功");
                            }
                        }
                    });

            AppManager.getInstance().setUserInfo(null);
            ChatUserInfo chatUserInfo = new ChatUserInfo();
            chatUserInfo.t_sex = 2;
            chatUserInfo.t_id = 0;
            SharedPreferenceHelper.saveAccountInfo(getApplicationContext(), chatUserInfo);

            //TIM登出
            TIMManager.getInstance().logout(new TIMCallBack() {

                @Override
                public void onError(int code, String desc) {
                    LogUtil.i("TIM logout failed. code: " + code + " errmsg: " + desc);
                }

                @Override
                public void onSuccess() {
                    LogUtil.i("TIM 登出成功");
                }
            });

            //极光
            JPushInterface.stopPush(this);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Intent intent = new Intent(getApplicationContext(), ScrollLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Constant.BEEN_CLOSE, been_close);
            if (!TextUtils.isEmpty(beenCloseDes))
                intent.putExtra(Constant.BEEN_CLOSE_DES, beenCloseDes);
            startActivity(intent);
        }
    }

    /**
     * 更新个人信息
     */
    public final void refreshMyInfo() {
        refreshMyInfo(new OnCommonListener<UserCenterBean>() {
            @Override
            public void execute(UserCenterBean bean) {
                if (bean != null) {
                    SharedPreferenceHelper.saveCity(AppManager.getInstance(),SharedPreferenceHelper.getCity(mInstance));
                    AppManager.getInstance().getUserInfo().t_nickName = bean.nickName;
                    IMHelper.checkTIMInfo(bean.nickName, bean.handImg);
                }
            }
        });
    }

    /**
     * 更新vip状态
     */
    public final void refreshVip(final OnCommonListener<VipInfoBean> listener) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserInfo().t_id);
        OkHttpUtils.post().url(ChatApi.getUserVipInfo())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<VipInfoBean>>() {
                    @Override
                    public void onResponse(BaseResponse<VipInfoBean> response, int id) {
                        if (response != null && response.m_istatus == NetCode.SUCCESS && response.m_object != null) {
                            /*
                             * 更新vip状态
                             */
                            SharedPreferenceHelper.saveUserVip(getInstance(), response.m_object.t_is_vip);
                            listener.execute(response.m_object);
                        }
                    }
                });
    }

    public final void refreshMyInfo(final OnCommonListener<UserCenterBean> listener) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserInfo().t_id);
        OkHttpUtils.post()
                .url(ChatApi.INDEX())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build()
                .execute(new AjaxCallback<BaseResponse<UserCenterBean>>() {
                    @Override
                    public void onResponse(BaseResponse<UserCenterBean> response, int id) {
                        UserCenterBean bean = null;
                        if (response != null && response.m_istatus == NetCode.SUCCESS) {

                            bean = response.m_object;

                            /*
                             * 更新vip状态
                             */
                            SharedPreferenceHelper.saveUserVip(getInstance(), bean.t_is_vip);
                        }
                        notify(bean);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        notify(null);
                    }

                    private void notify(UserCenterBean bean) {
                        if (listener != null)
                            listener.execute(bean);
                    }
                });
    }

    /**
     * 更新登录时间
     */
    public final void updateLoginTime() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserInfo().t_id);
        OkHttpUtils.post().url(ChatApi.UP_LOGIN_TIME())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response, int id) {
                        if (response != null && response.m_istatus == NetCode.SUCCESS) {
                            LogUtil.i("更新登录时间成功");
                        }
                    }
                });
    }

    /**
     * 开启保活服务，不需要关闭
     */
    public final void startService() {
        Intent intent = new Intent(getApplicationContext(), ConnectService.class);
        startService(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            JobInfo jobInfo = new JobInfo.Builder(1, new ComponentName(getPackageName(),
                    WakeupService.class.getName()))
                    .setPeriodic(5 * 60 * 1000L)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            if (jobScheduler != null) {
                jobScheduler.schedule(jobInfo);
            }
        }
    }

}