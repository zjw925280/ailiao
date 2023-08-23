package com.lovechatapp.chat.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lovechatapp.chat.BuildConfig;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.DownloadBean;
import com.lovechatapp.chat.bean.UnReadBean;
import com.lovechatapp.chat.bean.UnReadMessageBean;
import com.lovechatapp.chat.bean.UpdateBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.dialog.CloseYoungModeDialog;
import com.lovechatapp.chat.dialog.FreeDialog;
import com.lovechatapp.chat.dialog.FreeImDialog;
import com.lovechatapp.chat.dialog.SetYoungModeDialog;
import com.lovechatapp.chat.fragment.FindFragment;
import com.lovechatapp.chat.fragment.HomeFragment;
import com.lovechatapp.chat.fragment.MessageFragment;
import com.lovechatapp.chat.fragment.MineFragment;
import com.lovechatapp.chat.fragment.RankGroupFragment;
import com.lovechatapp.chat.helper.IMFilterHelper;
import com.lovechatapp.chat.helper.IMHelper;
import com.lovechatapp.chat.helper.LocationHelper;
import com.lovechatapp.chat.helper.MessageHelper;
import com.lovechatapp.chat.helper.RingVibrateManager;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.BadgeNumberUtil;
import com.lovechatapp.chat.util.FinishActivityManager;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.util.permission.floating.IBgStartImpl;
import com.lovechatapp.chat.util.permission.floating.api.PermissionLisenter;
import com.lovechatapp.chat.view.tab.FragmentParamBuilder;
import com.lovechatapp.chat.view.tab.MainMineTabViewHolder;
import com.lovechatapp.chat.view.tab.MainMsgTabViewHolder;
import com.lovechatapp.chat.view.tab.MainTabViewHolder;
import com.lovechatapp.chat.view.tab.TabFragmentAdapter;
import com.lovechatapp.chat.view.tab.TabPagerLayout;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主页
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class MainActivity extends BaseActivity implements TIMMessageListener {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.content_vp)
    ViewPager mContentVp;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tabpager)
    TabPagerLayout tabPagerLayout;

//    @BindView(R.id.reward_view)
//    RewardView rewardView;

    MainMsgTabViewHolder mainMsgTabViewHolder;
    MainMineTabViewHolder mainMineTabViewHolder;

    private static final int REQUEST_CODE = 123;
    //系统未读消息数
    private int mSystemMessageCount;

    private UnReadBean<UnReadMessageBean> unReadBean;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_main);
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        FinishActivityManager.getManager().addActivity(this);
        needHeader(false);
        initIm();
        initViewPager();
        checkUpdate();



        LocationHelper.get().startLocation();
//        if (!LocationHelper.get().isHasPerMission()) {
//            showDialog(0,this,"这边需要获取您的位置信息，以便为您找到同城以及附近的好友");
//        }

        IMFilterHelper.getInstance().updateImFilterWord();
        AppManager.getInstance().startService();
        new FreeImDialog(this).show();
        new FreeDialog(this).show();
        if (!BuildConfig.DEBUG) {
            new IBgStartImpl().requestStartPermission(this, new PermissionLisenter() {

                @Override
                public void onGranted() {

                }

                @Override
                public void cancel() {

                }

                @Override
                public void onDenied() {
                    ToastUtil.INSTANCE.showToast("来电提醒需要打开悬浮窗权限哦");
                }
            });
        }
//        rewardView.setActivity(mContext);
    }


    @Override
    protected void onStart() {
        super.onStart();
//        rewardView.getData();
        //极光
        if (JPushInterface.isPushStopped(getApplicationContext())) {
            JPushInterface.resumePush(getApplicationContext());
        }
    }

    //-------------------------------------定位部分----------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LocationHelper.get().onRequestPermissionsResult();
    }

    private void initIm() {

        //注册新消息通知
        TIMManager.getInstance().addMessageListener(this);

        //设置极光推送别名
        String saveAlisa = SharedPreferenceHelper.getJPushAlias(mContext);
        if (TextUtils.isEmpty(saveAlisa) || !saveAlisa.equals(getUserId())) {
            JPushInterface.setAlias(mContext, 1, getUserId());
        }
    }

    /**
     * 初始化viewPager
     */
    private void initViewPager() {
        View topBg = mBaseLayout.findViewById(R.id.base_top_bg);
        TabFragmentAdapter adapter = new TabFragmentAdapter(getSupportFragmentManager(), mContentVp);

        adapter.init(
                FragmentParamBuilder.create()
                        .withClazz(HomeFragment.class)
                        .withName(getString(R.string.home))
                        .withViewHolder(new MainTabViewHolder(tabPagerLayout, R.drawable.selector_navigation_home_background))
                        .build(),

                FragmentParamBuilder.create()
                        .withClazz(FindFragment.class)
                        .withName(getString(R.string.dynamic))
                        .withViewHolder(new MainTabViewHolder(tabPagerLayout, R.drawable.selector_navigation_live))
                        .build(),

                FragmentParamBuilder.create()
                        .withClazz(RankGroupFragment.class)
                        .withName(getString(R.string.main_rank))
                        .withViewHolder(new MainTabViewHolder(tabPagerLayout, R.drawable.selector_navigation_home_top))
                        .build(),

                FragmentParamBuilder.create()
                        .withClazz(MessageFragment.class)
                        .withName(getString(R.string.main_message))
                        .withViewHolder(mainMsgTabViewHolder = new MainMsgTabViewHolder(tabPagerLayout))
                        .build(),

                FragmentParamBuilder.create()
                        .withClazz(MineFragment.class)
                        .withName(getString(R.string.main_mine))
                        .withViewHolder(mainMineTabViewHolder = new MainMineTabViewHolder(tabPagerLayout))
                        .build()
        );

        tabPagerLayout.init(mContentVp);
        mContentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                if (adapter.getFragment(position) instanceof HomeFragment && isHomeFragmentImgShow) {
                    mBaseLayout.setBackgroundColor(Color.parseColor("#F7F7F7"));
                    topBg.setVisibility(View.VISIBLE);
                } else {
                    mBaseLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                    topBg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        HomeFragment fragment = (HomeFragment) ((TabFragmentAdapter) Objects.requireNonNull(mContentVp.getAdapter())).getFragment(HomeFragment.class);
        fragment.setPageChangeListener(new HomeFragment.OnPageChangeListener() {
            @Override
            public void currentPosition(int position) {
                if (position == 0) {
                    isHomeFragmentImgShow = true;
                    mBaseLayout.setBackgroundColor(Color.parseColor("#F7F7F7"));
                    topBg.setVisibility(View.VISIBLE);
                } else {
                    isHomeFragmentImgShow = false;
                    mBaseLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                    topBg.setVisibility(View.GONE);
                }
            }
        });


    }

    private boolean isHomeFragmentImgShow = false;

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //---------------------------------更新部分----------------------------

    /**
     * 检查更新
     */
    private void checkUpdate() {
        AppManager.getInstance().refreshMyInfo(userCenterBean -> {
            if (isFinishing() || userCenterBean == null) {
                return;
            }
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("userId", getUserId());
            OkHttpUtils.post().url(ChatApi.GET_NEW_VERSION())
                    .addParams("param", ParamUtil.getParam(paramMap))
                    .build().execute(new AjaxCallback<BaseResponse<UpdateBean>>() {
                        @Override
                        public void onResponse(BaseResponse<UpdateBean> response, int id) {
                            if (isFinishing()) {
                                return;
                            }
                            if (response != null && response.m_istatus == NetCode.SUCCESS && response.m_object != null) {
                                UpdateBean bean = response.m_object;
                                String t_version = bean.t_version;//接口版本
                                if (!TextUtils.isEmpty(t_version)) {
                                    String originalVersionName = BuildConfig.VERSION_NAME;//现在版本
                                    if (!TextUtils.isEmpty(originalVersionName) && !originalVersionName.equals(t_version)) {
                                        showUpdateDialog(bean);
                                    }
                                }
                            }
                        }
                    });
        });

    }

    /**
     * 显示更新dialog
     */
    private void showUpdateDialog(UpdateBean bean) {
        final Dialog mDialog = new Dialog(MainActivity.this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_update_layout, null);
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
        if (!BuildConfig.DEBUG)
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
        //版本
        TextView title_tv = view.findViewById(R.id.title_tv);
        String version = bean.t_version;
        String content;
        if (!TextUtils.isEmpty(version)) {
            content = getResources().getString(R.string.new_version_des_one) + version;
        } else {
            content = getString(R.string.new_version_des);
        }
        title_tv.setText(content);
        //更新
        final TextView update_tv = view.findViewById(R.id.update_tv);
        update_tv.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(bean.t_download_url)) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(bean.t_download_url);
                intent.setData(content_url);
                startActivity(intent);
            } else {
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.get_download_url_fail_one);
            }
        });
        //不能正常升级
        TextView click_tv = view.findViewById(R.id.click_tv);
        click_tv.setOnClickListener(v -> getDownloadUrl());
    }

    /**
     * 获取下载链接
     */
    private void getDownloadUrl() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_DOLOAD_URL())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<DownloadBean>>() {
                    @Override
                    public void onResponse(BaseResponse<DownloadBean> response, int id) {
                        if (response != null && response.m_istatus == NetCode.SUCCESS) {
                            DownloadBean bean = response.m_object;
                            if (bean != null && !TextUtils.isEmpty(bean.t_android_download)) {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(bean.t_android_download);
                                intent.setData(content_url);
                                startActivity(intent);
                            } else {
                                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.get_download_url_fail);
                            }
                        } else {
                            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.get_download_url_fail);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.get_download_url_fail);
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

    //-----------------------消息部分-------------------------
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable messageRun = new Runnable() {
        @Override
        public void run() {
            dealMessageCount(mSystemMessageCount);
        }
    };

    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        LogUtil.i("主页面TIM 新消息");

        handler.removeCallbacks(messageRun);
        handler.postDelayed(messageRun, 500);

        for (TIMMessage timMessage : list) {

            TIMConversation conversation = timMessage.getConversation();

            if (conversation == null || TextUtils.isEmpty(conversation.getPeer())) {
                continue;
            }

            //过滤个人消息
            if (timMessage.isSelf()) {
                continue;
            }

            //群会话同步
            if (IMHelper.isGroupDismiss(timMessage)) {
                continue;
            }

            //过滤同性单聊消息
            if (IMHelper.filterC2CSex(conversation)) {
                continue;
            }

            RingVibrateManager.receiveMessage(timMessage);

        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IMHelper.checkLogin();

        try {
            showSetYoungMode();
            dealUnReadCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //注销消息接收
        TIMManager.getInstance().removeMessageListener(this);

        unReadBeanOnCommonListener = null;
    }

    private OnCommonListener<UnReadBean<UnReadMessageBean>> unReadBeanOnCommonListener;

    public void setUnReadBeanListener(OnCommonListener<UnReadBean<UnReadMessageBean>> listener) {
        this.unReadBeanOnCommonListener = listener;
        listener.execute(unReadBean);
    }

    /**
     * 处理未读消息
     */
    public final void dealUnReadCount() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_UN_READ_MESSAGE())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<UnReadBean<UnReadMessageBean>>>() {
                    @Override
                    public void onResponse(BaseResponse<UnReadBean<UnReadMessageBean>> response, int id) {
                        if (isFinishing())
                            return;
                        if (response != null && response.m_istatus == NetCode.SUCCESS && response.m_object != null) {
                            unReadBean = response.m_object;
                            mSystemMessageCount = unReadBean.totalCount;
                            dealMessageCount(mSystemMessageCount);
                        }
                    }
                });
    }

    /**
     * 处理消息总数
     */
    private void dealMessageCount(int systemCount) {
        int[] count = getTIMUnReadCount();
        int all = count[0] + count[1] + systemCount;
        mainMsgTabViewHolder.setRedCount(all);
        BadgeNumberUtil.setCount(all);
        if (unReadBean != null) {
            unReadBean.groupCount = count[1];
        }
        if (unReadBeanOnCommonListener != null) {
            unReadBeanOnCommonListener.execute(unReadBean);
        }
    }

    /**
     * 清空消息
     */
    public final void resetRedPot() {
        MessageHelper.execute(o -> {
            if (isFinishing())
                return;
            dealUnReadCount();
        });
    }

    /**
     * 获取IM未读消息数
     */
    private int[] getTIMUnReadCount() {
        int count = 0;
        int count2 = 0;
        try {
            List<TIMConversation> conversationList = TIMManager.getInstance().getConversationList();
            if (conversationList != null && conversationList.size() > 0) {
                for (TIMConversation timConversation : conversationList) {
                    if (TextUtils.isEmpty(timConversation.getPeer())) {
                        continue;
                    }
                    if (timConversation.getType() == TIMConversationType.C2C) {
                        if (!IMHelper.filterC2CSex(timConversation)) {
                            count += timConversation.getUnreadMessageNum();
                        }
                    } else if (timConversation.getType() == TIMConversationType.Group
                            && IMHelper.isPublicGroup(timConversation.getPeer())) {
                        count2 += timConversation.getUnreadMessageNum();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[]{count, count2};
    }

    //---------------------------------未成年模式--------------------------

    /**
     * 显示设置未成年模式
     */
    private void showSetYoungMode() {
        FragmentManager manager = getSupportFragmentManager();
        boolean haveShow = SharedPreferenceHelper.getHaveShow(getApplicationContext());
        if (!haveShow) {//如果没显示过
            if (manager != null) {
                //如果显示了
                SetYoungModeDialog dialog = (SetYoungModeDialog) manager.findFragmentByTag("SetYoungModeDialog");
                if (dialog != null && dialog.isVisible()) {
                    return;
                }
            }
            SharedPreferenceHelper.setHaveShow(getApplicationContext());
            SetYoungModeDialog dialog = new SetYoungModeDialog();
            dialog.show(getSupportFragmentManager(), "SetYoungModeDialog");
        } else {//如果显示过了, 检查是否需要弹出关闭未成年模式弹窗
            String youngModePassword = SharedPreferenceHelper.getYoungPassword(getApplicationContext());
            if (!TextUtils.isEmpty(youngModePassword)) {//如果密码不为空, 弹出关闭弹窗
                if (manager != null) {
                    //如果显示了
                    CloseYoungModeDialog dialog = (CloseYoungModeDialog) manager.findFragmentByTag("CloseYoungModeDialog");
                    if (dialog != null && dialog.isVisible()) {
                        return;
                    }
                }
                CloseYoungModeDialog dialog = new CloseYoungModeDialog();
                dialog.show(getSupportFragmentManager(), "CloseYoungModeDialog");
            } else {//如果密码为空了, 说明已经关闭了
                if (manager != null) {
                    //如果是在预览界面
                    CloseYoungModeDialog dialog = (CloseYoungModeDialog) manager.findFragmentByTag("CloseYoungModeDialog");
                    if (dialog != null && dialog.isVisible()) {
                        dialog.dismiss();
                    }
                }
            }
        }
    }
}