package com.lovechatapp.chat.activity;

import android.Manifest;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pili.pldroid.player.widget.PLVideoView;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.AVChatBean;
import com.lovechatapp.chat.bean.AudioUserBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.dialog.VipDialog;
import com.lovechatapp.chat.helper.ChargeHelper;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.AudioVideoRequester;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.socket.SocketMessageManager;
import com.lovechatapp.chat.socket.domain.Mid;
import com.lovechatapp.chat.socket.domain.SocketResponse;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.SoundRing;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.util.permission.PermissionUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：等待接通页面
 * 作者：
 * 创建时间：2018/6/20
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class WaitActorActivity extends BaseActivity {

    @BindView(R.id.head_iv)
    ImageView mHeadIv;

    @BindView(R.id.name_tv)
    TextView mNameTv;

    @BindView(R.id.content_iv)
    ImageView mContentIv;

    @BindView(R.id.camera_ll)
    LinearLayout mCameraLl;

    @BindView(R.id.camera_iv)
    ImageView mCameraIv;

    @BindView(R.id.camera_tv)
    TextView mCameraTv;

    @BindView(R.id.video_view)
    PLVideoView mVideoView;

    //房间id
    private AVChatBean chatBean;

    private SoundRing soundRing;

    //30秒计时器 自动挂断
    private CountDownTimer mAutoHangUpTimer;

    //需要暂停
    private boolean mNeedPause = true;

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_wait_actor_layout);
    }

    @Override
    protected void onContentAdded() {

        //正在视频或者语音则不弹出来电call
        if (AppManager.getInstance().getActivityObserve().isChatting()) {
            finish();
            return;
        }
        chatBean = (AVChatBean) getIntent().getSerializableExtra("bean");

        SocketMessageManager.get().subscribe(subscription, Subscriptions);

        soundRing = new SoundRing();

        needHeader(false);

        initAutoCountTimer();

        playMusic();

        getPassUserInfo();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mNeedPause = true;
    }

    /**
     * 自动挂断
     */
    private void initAutoCountTimer() {
        if (AppManager.getInstance().getUserInfo().t_role != 1) {
            mCameraLl.setVisibility(View.VISIBLE);
        }
        if (mAutoHangUpTimer == null) {
            mAutoHangUpTimer = new CountDownTimer((long) (35 * 1000), 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    hangUp();
                }
            };
            mAutoHangUpTimer.start();
        }
    }

    /**
     * 取消timer
     */
    private void cancelAutoTimer() {
        if (mAutoHangUpTimer != null) {
            mAutoHangUpTimer.cancel();
            mAutoHangUpTimer = null;
        }
    }

    /**
     * 获取对方id的信息
     * ++++++++++++++++接口更改++++++++++++++
     */
    private void getPassUserInfo() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("coverUserId", chatBean.otherId);
        OkHttpUtils.post().url(ChatApi.getUserInfoById())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<AudioUserBean>>() {
            @Override
            public void onResponse(BaseResponse<AudioUserBean> response, int id) {

                if (isFinishing()) {
                    return;
                }

                if (response != null && response.m_istatus == NetCode.SUCCESS) {

                    AudioUserBean bean = response.m_object;

                    if (bean != null) {

                        //昵称
                        mNameTv.setText(bean.nickName);

                        //头像
                        String mHandImg = bean.handImg;
                        if (!TextUtils.isEmpty(mHandImg)) {
                            int width = DevicesUtil.dp2px(WaitActorActivity.this, 50);
                            int height = DevicesUtil.dp2px(WaitActorActivity.this, 50);
                            ImageLoadHelper.glideShowCornerImageWithUrl(WaitActorActivity.this,
                                    mHandImg, mHeadIv, 5, width, height);
                        } else {
                            mHeadIv.setImageResource(R.drawable.default_head_img);
                        }

                        //如果有公开视频,显示视频
                        String videoUrl = bean.t_addres_url;
                        if (!TextUtils.isEmpty(videoUrl)) {
                            //加载封面图
                            String coverUrl = bean.t_video_img;
                            if (!TextUtils.isEmpty(coverUrl)) {
                                int overWidth = DevicesUtil.getScreenW(mContext);
                                int overHeight = DevicesUtil.getScreenH(mContext);
                                if (overWidth > 800) {
                                    overWidth = (int) (overWidth * 0.7);
                                    overHeight = (int) (overHeight * 0.7);
                                }
                                ImageLoadHelper.glideShowImageWithUrl(mContext, coverUrl, mContentIv,
                                        overWidth, overHeight);
                            }
                            playVideoWithUrl(videoUrl);
                        } else {
                            //封面图
                            if (!TextUtils.isEmpty(bean.t_cover_img)) {
                                int overWidth = DevicesUtil.getScreenW(mContext);
                                int overHeight = DevicesUtil.getScreenH(mContext);
                                if (overWidth > 800) {
                                    overWidth = (int) (overWidth * 0.7);
                                    overHeight = (int) (overHeight * 0.7);
                                }
                                ImageLoadHelper.glideShowImageWithUrl(mContext, bean.t_cover_img, mContentIv,
                                        overWidth, overHeight);
                            }

                        }
                    }
                }
            }
        });
    }

    /**
     * 播放视频
     */
    private void playVideoWithUrl(String url) {
        if (mVideoView != null) {
            mContentIv.setVisibility(View.GONE);
            mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
            mVideoView.setVideoPath(url);
            mVideoView.setVolume(0, 0);
            mVideoView.setLooping(true);
            mVideoView.start();
        }
    }

    @OnClick({R.id.hang_up_tv, R.id.accept_tv, R.id.camera_ll})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hang_up_tv: {//挂断
                hangUp();
                break;
            }
            case R.id.accept_tv: {//接通

                PermissionUtil.requestPermissions(this, new PermissionUtil.OnPermissionListener() {

                            @Override
                            public void onPermissionGranted() {
                                AudioVideoRequester.getAgoraSign(chatBean.roomId, s -> {
                                    if (isFinishing()) {
                                        return;
                                    }
                                    dismissLoadingDialog();
                                    if (TextUtils.isEmpty(s)) {
                                        ToastUtil.INSTANCE.showToast("接听失败，请重试");
                                    } else {
                                        chatBean.sign = s;
                                        toVideoChat();
                                    }
                                });
                            }

                            @Override
                            public void onPermissionDenied() {
                                ToastUtil.INSTANCE.showToast("无麦克风或者摄像头权限，无法使用该功能");
                            }

                        },
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO);
                break;
            }
            case R.id.camera_ll: {//关闭摄像头
                mCameraIv.setSelected(!mCameraIv.isSelected());
                mCameraTv.setText(mCameraIv.isSelected() ? R.string.off_camera : R.string.open_camera);
                chatBean.closeVideo = mCameraIv.isSelected();
                break;
            }
        }
    }

    /**
     * 跳转到videoChat
     */
    private void toVideoChat() {

        //用户接听调用计费后进入同话界面
        if (!chatBean.isActor()) {
            /*
             * new MessageUtil(1, "开始计时");
             * new MessageUtil(-7, "音视频功能只有VIP可使用!");
             * new MessageUtil(-1, "余额不足!请充值.");
             * new MessageUtil(-5, "对方用户已下线");
             * new MessageUtil(-6, "当前用户正在计时中.");
             * new MessageUtil(-2, "主播资料未完善!");
             * new MessageUtil(-4, "啊！被别人抢走了,下次手速要快哦.");
             * new MessageUtil(-3, "对方挂断视频请求.");
             */
            showLoadingDialog();
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("userId", getUserId());
            paramMap.put("anthorId", chatBean.otherId);
            paramMap.put("roomId", chatBean.roomId);
            paramMap.put("chatType", AudioVideoRequester.CHAT_VIDEO);
            OkHttpUtils.post().url(ChatApi.VIDEO_CHAT_BIGIN_TIMING())
                    .addParams("param", ParamUtil.getParam(paramMap))
                    .build().execute(new AjaxCallback<BaseResponse<String>>() {
                @Override
                public void onResponse(BaseResponse response, int id) {
                    if (isFinishing()) {
                        return;
                    }
                    boolean ok = response != null && response.m_istatus == NetCode.SUCCESS;
                    if (!ok) {
                        boolean notVipAlert = false;
                        if (response != null) {
                            if (response.m_strMessage.contains("余额不足")) {
                                Intent intent = new Intent(WaitActorActivity.this, ChargeActivity.class);
                                startActivity(intent);
                            } else {
                                if (response.m_istatus == -7) {
                                    new VipDialog(WaitActorActivity.this).show();
                                    notVipAlert = true;
                                } else if (response.m_istatus == -1) {
                                    ChargeHelper.showSetCoverDialog(WaitActorActivity.this);
                                } else {
                                    if (response.m_istatus==-5){
                                        ToastUtil.INSTANCE.showToast("余额不足!请充值");
                                        Intent intent = new Intent(WaitActorActivity.this, ChargeActivity.class);
                                        startActivity(intent);
                                    }else {
                                        if (!TextUtils.isEmpty(response.m_strMessage)) {
                                            ToastUtil.INSTANCE.showToast(response.m_strMessage);
                                        }
                                    }
                                }
                            }
                        } else {
                            ToastUtil.INSTANCE.showToast(R.string.please_retry);
                        }
                        if (!notVipAlert) {
                            cancelAutoTimer();
                            hangUp();
                        }
                    } else {
                        LogUtil.i("开始计费成功");
                        VideoChatActivity.start(mContext, chatBean);
                        cancelAutoTimer();
                        finish();
                    }
                }

                @Override
                public void onAfter(int id) {
                    if (!isFinishing()) {
                        dismissLoadingDialog();
                    }
                }
            });
        } else {
            //主播接听直接进入通话界面
            VideoChatActivity.start(mContext, chatBean);
            cancelAutoTimer();
            finish();
        }
    }

    /**
     * 用户或者主播挂断链接
     */
    private void hangUp() {
        cancelAutoTimer();
        finish();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("roomId", chatBean.roomId);
        OkHttpUtils.post().url(ChatApi.BREAK_LINK())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
            }
        });
    }

    /**
     * 播放音频
     */
    private void playMusic() {
        soundRing.start();
    }

    protected void onHangUp(int roomId, int breakId) {
        if (roomId != chatBean.roomId)
            return;
        try {
            if (!isFinishing()) {
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.have_hang_up);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null && mNeedPause) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    protected void onDestroy() {

        SocketMessageManager.get().unsubscribe(subscription);

        super.onDestroy();
        if (soundRing != null) {
            soundRing.stop();
        }
        if (mVideoView != null) {
            mVideoView.stopPlayback();
            mVideoView = null;
        }
        cancelAutoTimer();
    }

    /**
     * 订阅Socket Message
     */
    int[] Subscriptions = {
            Mid.HAVE_HANG_UP
    };

    OnCommonListener<SocketResponse> subscription = new OnCommonListener<SocketResponse>() {

        @Override
        public void execute(SocketResponse response) {
            switch (response.mid) {
                case Mid.HAVE_HANG_UP:
                    onHangUp(response.roomId, response.breakUserId);
                    break;
            }
        }

    };
}