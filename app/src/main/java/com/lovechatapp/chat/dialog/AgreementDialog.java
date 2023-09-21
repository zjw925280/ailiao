package com.lovechatapp.chat.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lovechatapp.chat.BuildConfig;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.CommonWebViewActivity;
import com.lovechatapp.chat.activity.ScrollLoginActivity;
import com.lovechatapp.chat.base.BasicParamsInterceptor;
import com.lovechatapp.chat.camera.beauty.BeautyManager;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.IMHelper;
import com.lovechatapp.chat.im.ConfigHelper;
import com.lovechatapp.chat.rtc.RtcManager;
import com.lovechatapp.chat.socket.SocketMessageManager;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.mm.opensdk.utils.Log;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;


/**
 * 首冲弹窗
 */
public class AgreementDialog extends Dialog implements View.OnClickListener {
    public static final String APP_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "loveChat" + File.separator;

    private Activity activity;
    private static final int REQUEST_CODE = 123;
    public AgreementDialog(@NonNull Activity context) {
        super(context);
        activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_agreement);

        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.setAttributes(lp);

        setCancelable(false);

        findViewById(R.id.confirm_tv).setOnClickListener(this);
        findViewById(R.id.cancel_tv).setOnClickListener(this);

        String content = activity.getString(R.string.alert_agreement_des);
        int index1 = content.indexOf("《用户协议》");
        int index2 = content.indexOf("《隐私政策》");
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.main)),
                index1,
                index1 + 6,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
                                    @Override
                                    public void onClick(@NonNull View widget) {
                                        //用户协议
                                        Intent intent = new Intent(activity, CommonWebViewActivity.class);
                                        intent.putExtra(Constant.TITLE, activity.getString(R.string.agree_detail));
                                        intent.putExtra(Constant.URL, "http://api.zhongzhiqian.cn:8080/tmApp/file/agreement.txt");
                                        activity.startActivity(intent);
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        ds.setUnderlineText(false);
                                    }
                                },
                index1,
                index1 + 6,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.main)),
                index2,
                index2 + 6,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {

                                    @Override
                                    public void onClick(@NonNull View widget) {
                                        //隐私协议
                                        Intent intent = new Intent(activity, CommonWebViewActivity.class);
                                        intent.putExtra(Constant.TITLE, activity.getString(R.string.private_detail));
//                                        intent.putExtra(Constant.URL, "file:///android_asset/private.html");
                                        intent.putExtra(Constant.URL, "http://api.lnqianlian.top:8080/tmApp/file/privacy.txt");
                                        activity.startActivity(intent);
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        ds.setUnderlineText(false);
                                    }
                                },
                index2,
                index2 + 6,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView desTv = findViewById(R.id.des_tv);
        desTv.setMovementMethod(LinkMovementMethod.getInstance());
        desTv.setText(spannableString);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.confirm_tv) {

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
//            if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//                    || activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
//            } else {
//                // 已经有权限，可以执行操作SD卡的代码
//            }
            //极光
            JPushInterface.setDebugMode(BuildConfig.DEBUG);
            JPushInterface.init(activity);

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

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
            sp.edit().putBoolean("agree", true).apply();
            dismiss();
            activity.startActivity(new Intent(activity, ScrollLoginActivity.class));
        } else {
            dismiss();
            activity.finish();
        }

    }

    public void uncaughtException1(Thread thread, Throwable throwable) {
        try {
            // 创建一个文件来保存异常信息
            File logFile = new File(activity.getExternalFilesDir(null), "语聊Log.txt");

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

    /**
     * 初始化腾讯IM
     */
    private void initTIM() {

        //ImUi
        TUIKit.init(getContext(), Constant.TIM_APP_ID, new ConfigHelper().getConfigs());

        //初始化 SDK 基本配置
        TIMSdkConfig config = new TIMSdkConfig(Constant.TIM_APP_ID);

        // 是否在控制台打印Log?
        config.enableLogPrint(false);

        //初始化 SDK
        TIMManager.getInstance().init(activity, config);

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
            File cacheDir = new File(activity.getCacheDir(), "http");
            HttpResponseCache.install(cacheDir, 1024 * 1024 * 128);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}