package com.lovechatapp.chat.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.BuildConfig;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.UpdateBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.RingVibrateManager;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.FileUtil;
import com.lovechatapp.chat.util.FinishActivityManager;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.MyDataCleanManager;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：设置页面
 * 作者：
 * 创建时间：2018/6/22
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.check_tv)
    TextView mCheckTv;

    @BindView(R.id.cache_number_tv)
    TextView mCacheNumberTv;

    @BindView(R.id.sound_iv)
    ImageView mSoundIv;

    @BindView(R.id.sound_group_iv)
    ImageView mSoundGroupIv;

    @BindView(R.id.vibrate_iv)
    ImageView mVibrateIv;

    @BindView(R.id.vibrate_group_iv)
    ImageView mVibrateGroupIv;

    @BindView(R.id.vibrate_group)
    ImageView vibrate_group;

    @BindView(R.id.zhuxiao_tv)
    TextView zhuxiao_tv;


    private MyHandler mHandler = new MyHandler(SettingActivity.this);
    private final int DONE = 1;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_setting_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.setting_center);
        initView();
    }

    /**
     * 初始化版本
     */
    private void initView() {

        //消息提示音
        boolean sound = SharedPreferenceHelper.getTipSound(getApplicationContext());
        mSoundIv.setSelected(sound);

        //个性化推荐
        boolean vibr= SharedPreferenceHelper.getTipSound5(getApplicationContext());
        vibrate_group.setSelected(vibr);

        //群消息提示音
        boolean sound2 = SharedPreferenceHelper.getTipSound2(getApplicationContext());
        mSoundGroupIv.setSelected(sound2);

        //消息震动
        boolean vibrate = SharedPreferenceHelper.getTipVibrate(getApplicationContext());
        mVibrateIv.setSelected(vibrate);

        //群消息震动
        boolean vibrate2 = SharedPreferenceHelper.getTipVibrate2(getApplicationContext());
        mVibrateGroupIv.setSelected(vibrate2);

        //版本
        String originalVersionName = BuildConfig.VERSION_NAME;//现在版本
        if (!TextUtils.isEmpty(originalVersionName)) {
            mCheckTv.setText(originalVersionName);
        }

        //缓存
        try {
            String dataSize = MyDataCleanManager.getTotalCacheSize(getApplicationContext());
            mCacheNumberTv.setText(dataSize);
            mCacheNumberTv.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick({
            R.id.vibrate_group,
            R.id.opinion_rl,
            R.id.exit_tv,
            R.id.check_rl,
            R.id.clear_cache_tv,
            R.id.sound_iv,
            R.id.sound_group_iv,
            R.id.vibrate_iv,
            R.id.vibrate_group_iv,
            R.id.young_tv,
            R.id.help_tv,
            R.id.agreement_tv,
            R.id.private_tv,
            R.id.black_tv,
            R.id.zhuxiao_tv
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vibrate_group: {//个性化推荐
                vibrate_group.setSelected(!vibrate_group.isSelected());
                SharedPreferenceHelper.saveTipSound5(getApplicationContext(), vibrate_group.isSelected());
                RingVibrateManager.syncSwitch();
                break;
            }
            case R.id.zhuxiao_tv: {//注销账号

                showDialogaa(SettingActivity.this);
                break;
            }
            case R.id.opinion_rl: {//意见反馈
                Intent intent = new Intent(getApplicationContext(), OpinionActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.exit_tv: {//退出
                AppManager.getInstance().exit(null, false);
                break;
            }
            case R.id.check_rl: {//检查版本
                showLoadingDialog();
                checkUpdate();
                break;
            }
            case R.id.clear_cache_tv: {//清除缓存
                showLoadingDialog();
                clearAppCache();
                break;
            }

            //消息提示音
            case R.id.sound_iv: {
                mSoundIv.setSelected(!mSoundIv.isSelected());
                SharedPreferenceHelper.saveTipSound(getApplicationContext(), mSoundIv.isSelected());
                RingVibrateManager.syncSwitch();
                break;
            }

            //群消息提示音
            case R.id.sound_group_iv: {
                mSoundGroupIv.setSelected(!mSoundGroupIv.isSelected());
                SharedPreferenceHelper.saveTipSound2(getApplicationContext(), mSoundGroupIv.isSelected());
                RingVibrateManager.syncSwitch();
                break;
            }

            case R.id.vibrate_iv: {//消息震动
                mVibrateIv.setSelected(!mVibrateIv.isSelected());
                SharedPreferenceHelper.saveTipVibrate(getApplicationContext(), mVibrateIv.isSelected());
                RingVibrateManager.syncSwitch();
                break;
            }

            //群震动提示音
            case R.id.vibrate_group_iv: {
                mVibrateGroupIv.setSelected(!mVibrateGroupIv.isSelected());
                SharedPreferenceHelper.saveTipVibrate2(getApplicationContext(), mVibrateGroupIv.isSelected());
                RingVibrateManager.syncSwitch();
                break;
            }

            case R.id.black_tv: {
                startActivity(new Intent(SettingActivity.this, BlackListActivity.class));
                break;
            }
            case R.id.young_tv: {
                startActivity(new Intent(SettingActivity.this, YoungModeActivity.class));
                break;
            }
            case R.id.help_tv: {
                startActivity(new Intent(SettingActivity.this, HelpCenterActivity.class));
                break;
            }
            case R.id.agreement_tv: {
                Intent intent = new Intent(getApplicationContext(), CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, getResources().getString(R.string.agree_detail));
                intent.putExtra(Constant.URL, "http://api.zhongzhiqian.cn:8080/tmApp/file/agreement.txt");
                startActivity(intent);
                break;
            }
            case R.id.private_tv: {//隐私协议
                Intent intent = new Intent(getApplicationContext(), CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, getResources().getString(R.string.private_detail));
                intent.putExtra(Constant.URL, "http://api.lnqianlian.top:8080/tmApp/file/privacy.txt");
                startActivity(intent);
                break;
            }
        }
    }
    public void showDialogaa( Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // 设置对话框标题和消息
        alertDialogBuilder.setTitle("提示");
        alertDialogBuilder.setMessage("确定要注销账号吗？");

        // 设置关闭按钮
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // 设置确认按钮
        alertDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FinishActivityManager.getManager().finishActivity(MainActivity.class);
                finish();
            }
        });
        // 创建并显示对话框
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    /**
     * 清除app缓存
     */
    public void clearAppCache() {
        new Thread() {
            @Override
            public void run() {
                try {
                    MyDataCleanManager.clearAllCache(getApplicationContext());
                    mHandler.sendEmptyMessage(DONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_NEW_VERSION())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<UpdateBean>>() {
            @Override
            public void onResponse(BaseResponse<UpdateBean> response, int id) {
                dismissLoadingDialog();
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    UpdateBean bean = response.m_object;
                    if (bean != null) {
                        String t_version = bean.t_version;//接口版本
                        if (!TextUtils.isEmpty(t_version)) {
                            String originalVersionName = BuildConfig.VERSION_NAME;//现在版本
                            if (!TextUtils.isEmpty(originalVersionName)) {
                                if (!originalVersionName.equals(t_version)) {
                                    showUpdateDialog(bean);
                                } else {
                                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.already_the_latest);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 显示更新dialog
     */
    private void showUpdateDialog(UpdateBean bean) {
        final Dialog mDialog = new Dialog(SettingActivity.this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.dialog_update_layout, null);
        setUpdateDialogView(view, mDialog, bean);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    private void setUpdateDialogView(View view, final Dialog mDialog, final UpdateBean bean) {
        //描述
        TextView des_tv = view.findViewById(R.id.des_tv);
        String des = bean.t_version_depict;
        if (!TextUtils.isEmpty(des)) {
            des_tv.setText(des);
        }
        //更新
        final TextView update_tv = view.findViewById(R.id.update_tv);
        update_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.setCancelable(false);
                downloadApkFile(bean, mDialog, update_tv);
            }
        });
    }

    /**
     * 下载apk文件
     */
    private void downloadApkFile(UpdateBean bean, final Dialog updateDialog, final TextView updateTv) {
        String downloadUrl = bean.t_download_url;
        if (TextUtils.isEmpty(downloadUrl)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.update_fail);
            return;
        }
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            boolean res = pFile.mkdir();
            if (!res) {
                return;
            }
        }
        File file = new File(Constant.UPDATE_DIR);
        if (!file.exists()) {
            boolean res = file.mkdir();
            if (!res) {
                return;
            }
        } else {
            FileUtil.deleteFiles(file.getPath());
        }
        OkHttpUtils.get().url(downloadUrl).build().execute(new FileCallBack(Constant.UPDATE_DIR, Constant.UPDATE_APK_NAME) {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                int res = (int) (progress * 100);
                String content = res + getResources().getString(R.string.percent);
                updateTv.setText(content);
            }

            @Override
            public void onResponse(File response, int id) {
                try {
                    updateDialog.dismiss();
                    if (response != null && response.exists() && response.isFile()) {
                        // 下载成功后，检查8.0安装权限,安装apk
                        checkIsAndroidO(response);
                    } else {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.update_fail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    updateDialog.dismiss();
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.update_fail);
                }
            }
        });
    }

    /**
     * 判断是否是8.0,8.0需要处理未知应用来源权限问题,否则直接安装
     */
    private void checkIsAndroidO(File response) {
        if (Build.VERSION.SDK_INT >= 26) {
            boolean b = getPackageManager().canRequestPackageInstalls();
            LogUtil.i("=====未知来源安装权限: " + b);
            if (b) {
                installApk(response);//安装应用的逻辑(写自己的就可以)
            } else {
                showUnkownPermissionDialog();
            }
        } else {
            installApk(response);
        }
    }

    /**
     * 被封号
     */
    private void showUnkownPermissionDialog() {
        final Dialog mDialog = new Dialog(SettingActivity.this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.dialog_set_unkown_permission_layout, null);
        setUnkownDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置查看微信号提醒view
     */
    private void setUnkownDialogView(View view, final Dialog mDialog) {
        //取消
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //设置
        TextView confirm_tv = view.findViewById(R.id.set_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 26) {
                    //请求安装未知应用来源的权限
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    startActivityForResult(intent, 10086);
                }
                mDialog.dismiss();
            }
        });
    }

    //普通安装
    private void installApk(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //版本在7.0以上是不能直接通过uri访问的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileProvider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile),
                    "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    private static class MyHandler extends Handler {
        private WeakReference<SettingActivity> mSettingActivityWeakReference;

        MyHandler(SettingActivity settingActivity) {
            mSettingActivityWeakReference = new WeakReference<>(settingActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SettingActivity settingActivity = mSettingActivityWeakReference.get();
            if (settingActivity != null) {
                settingActivity.clearDone();
                settingActivity.dismissLoadingDialog();
            }
        }
    }

    /**
     * 清理完成
     */
    private void clearDone() {
        mCacheNumberTv.setText(getResources().getString(R.string.cache_des));
        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.clear_done);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10086) {//未知来源权限
            if (Build.VERSION.SDK_INT >= 26) {
                boolean b = getPackageManager().canRequestPackageInstalls();
                File apk = new File(Constant.UPDATE_DIR, Constant.UPDATE_APK_NAME);
                if (apk.exists() && b) {
                    installApk(apk);
                }
            }
        }
    }
}