package com.lovechatapp.chat.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.cjt2325.cameralibrary.util.ScreenUtils;
import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.VideoChatTextRecyclerAdapter;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.AVChatBean;
import com.lovechatapp.chat.bean.AudioUserBean;
import com.lovechatapp.chat.bean.ChatMessageBean;
import com.lovechatapp.chat.bean.CustomMessageBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.dialog.GiftDialog;
import com.lovechatapp.chat.dialog.InputDialogFragment;
import com.lovechatapp.chat.dialog.InvalidChatDialog;
import com.lovechatapp.chat.helper.IMFilterHelper;
import com.lovechatapp.chat.helper.IMHelper;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.AudioVideoRequester;
import com.lovechatapp.chat.net.FocusRequester;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.rtc.RtcEngineEventHandler;
import com.lovechatapp.chat.rtc.RtcManager;
import com.lovechatapp.chat.rtc.RtcVideoConsumer;
import com.lovechatapp.chat.socket.SocketMessageManager;
import com.lovechatapp.chat.socket.domain.Mid;
import com.lovechatapp.chat.socket.domain.SocketResponse;
import com.lovechatapp.chat.ttt.QiNiuChecker;
import com.lovechatapp.chat.util.DensityUtil;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.SoundRing;
import com.lovechatapp.chat.util.TimeUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.ImCustomMessage;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

/**
 * 视频聊天
 */
public class VideoChatActivity extends BaseActivity implements TIMMessageListener {

    //---------------呼叫中布局---------------//

    @BindView(R.id.calling_fl)
    FrameLayout callingLayout;

    @BindView(R.id.close_camera_tv)
    TextView mCloseCameraTv;

    @BindView(R.id.other_name_tv)
    TextView mOtherNameTv;

    @BindView(R.id.other_head_iv)
    ImageView mOtherHeadIv;

    @BindView(R.id.cover_iv)
    ImageView mCoverIv;

    //---------------呼叫中布局---------------//

    //正在聊天布局
    @BindView(R.id.chat_fl)
    View chatLayout;

    //对方摄像头关闭
    @BindView(R.id.other_camera_disable)
    View otherCameraDisable;

    //计时
    @BindView(R.id.des_tv)
    TextView mDesTv;

    //禁用麦克风
    @BindView(R.id.close_micro_iv)
    ImageView mCloseMicroIv;

    //礼物相关
    @BindView(R.id.gift_ll)
    LinearLayout mGiftLl;

    @BindView(R.id.gift_head_iv)
    ImageView mGiftHeadIv;

    @BindView(R.id.gift_des_tv)
    TextView mGiftDesTv;

    @BindView(R.id.gift_iv)
    ImageView mGiftIv;

    @BindView(R.id.gift_number_tv)
    TextView mGiftNumberTv;

    //关闭摄像头
    @BindView(R.id.close_camera_iv)
    ImageView mCloseVideoIv;

    @BindView(R.id.text_list_rv)
    RecyclerView mTextListRv;

    //控制布局
    @BindView(R.id.control_ll)
    LinearLayout mControlLl;

    //对方信息
    @BindView(R.id.info_ll)
    LinearLayout mInfoLl;

    //头像
    @BindView(R.id.info_head_iv)
    ImageView mInfoHeadIv;

    //昵称
    @BindView(R.id.info_nick_tv)
    TextView mInfoNickTv;

    //年龄
    @BindView(R.id.info_age_tv)
    TextView mInfoAgeTv;

    //关注
    @BindView(R.id.follow_btn)
    TextView mFollowTv;

    //城市
    @BindView(R.id.city_tv)
    TextView mCityTv;

    //剩余约豆
    @BindView(R.id.left_gold_tv)
    TextView mLeftGoldTv;

    //gif动图礼物
    @BindView(R.id.gif_sv)
    SVGAImageView mGifSv;

    @BindView(R.id.video_cover)
    View remoteCoverView;

    @BindView(R.id.illegal_tv)
    TextView illegalTv;

    @BindView(R.id.close_remote_iv)
    ImageView remoteCloseBtn;

    /**
     * 本地视图
     */
    @BindView(R.id.local_view)
    FrameLayout localView;

    @BindView(R.id.local_video_view)
    TextureView localVideoView;

    @BindView(R.id.local_cover_view)
    View localCoverView;

    @BindView(R.id.local_illegal_view)
    View localIllegalView;

    /**
     * 远端视图
     */
    @BindView(R.id.remote_view)
    FrameLayout remoteView;

    protected SoundRing soundRing;

    //计时器
    private Handler mHandler = new Handler(Looper.getMainLooper());

    //当前毫秒数
    private long mCurrentSecond = 0;

    //记录单次动画时间内用户赠送的礼物
    private int mSingleTimeSendGiftCount = 0;

    //30秒计时器 自动挂断
    private CountDownTimer mAutoHangUpTimer;

    //视频聊天相关
    protected RtcManager rtcManager;

    protected boolean mHaveUserJoin = false;

    //文字聊天
    private VideoChatTextRecyclerAdapter mTextRecyclerAdapter;

    //会话
    protected TIMConversation mConversation;

    public static boolean isChatting;

    private boolean isDisable;

    private AVChatBean chatBean;

    private Dialog illegalAlert;

    /**
     * 非法视频弹窗展示时间15s
     */
    final int illegalTime = 15000;

    /**
     * 系统挂断
     */
    protected final int HANGUP_SYSTEM = 100;

    /**
     * 页面销毁挂断
     */
    protected final int HANGUP_DESTROY = 101;

    /**
     * 超时挂断
     */
    protected final int HANGUP_TIME_OUT = 102;

    /**
     * RTC回调挂断
     */
    protected final int HANGUP_RTC = 103;

    /**
     * 轮询挂断
     */
    protected final int HANGUP_LOOP_BREAK = 104;

    /**
     * 计费失败挂断
     */
    protected final int HANGUP_BILLING = 105;

    /**
     * 点击挂断
     */
    protected final int HANGUP_CLICK_BREAK = 106;

    /**
     * 通信已挂断
     */
    protected final int HANGUP_BREAK = Mid.HAVE_HANG_UP;

    /**
     * 被封号
     */
    protected final int HANGUP_BEAN_SUSPEND = Mid.BEAN_SUSPEND;

    /**
     * 用户拒接挂断
     */
    protected final int HANGUP_USER_BREAK = Mid.brokenVIPLineRes;

    /**
     * 主播拒接挂断
     */
    protected final int HANGUP_ACTOR_BREAK = Mid.brokenUserLineRes;

    public static void start(Context context, AVChatBean bean) {
        Intent starter = new Intent(context, VideoChatActivity.class);
        starter.putExtra("bean", bean);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        SocketMessageManager.get().subscribe(subscription, Subscriptions);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    public void finish() {
        destroy();
        super.finish();
    }

    private void destroy() {

        if (rtcManager == null) {
            return;
        }

        if (illegalAlert != null) {
            illegalAlert.dismiss();
            illegalAlert = null;
        }

        QiNiuChecker.get().setAlertListener(null);

        destroyRtc();

        SocketMessageManager.get().unsubscribe(subscription);

        hangUp(HANGUP_DESTROY, AppManager.getInstance().getUserInfo().t_id);

        stopPlay();

        cancelAutoTimer();

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (mGifSv != null) {
            mGifSv.pauseAnimation();
            mGifSv = null;
        }

        IMHelper.setLoginResult(null);

        TIMManager.getInstance().removeMessageListener(this);

        requestBreak(chatBean.roomId, null);

        toComment();

        isChatting = false;
    }

    @Override
    protected void onDestroy() {
        destroy();
        super.onDestroy();
    }

    private void destroyRtc() {
        if (rtcManager != null) {
            rtcManager.stopCamera();
            rtcManager.removeRtcHandler(rtcEngineEventHandler);
            rtcManager.rtcEngine().leaveChannel();
            rtcManager = null;
        }
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_video_chat);
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return true;
    }

    @Override
    protected void onContentAdded() {

        isChatting = true;

        soundRing = getSoundRing();

        QiNiuChecker.get().checkEnable();
        QiNiuChecker.get().setAlertListener(new OnCommonListener<Boolean>() {
            @Override
            public void execute(Boolean aBoolean) {
                if (isFinishing()) {
                    QiNiuChecker.get().setAlertListener(null);
                    return;
                }
                if (aBoolean) {
                    sendMessage(ImCustomMessage.buildIllegalVideo());
                    IllegalVideo(true);
                    if (illegalAlert == null) {
                        illegalAlert = new InvalidChatDialog(mContext, getString(R.string.illegal_info_self));
                        illegalAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                illegalAlert = null;
                            }
                        });
                    }
                    illegalAlert.show();
                }
            }
        });

        checkPermission();

        needHeader(false);

        initTextChat();

        initData(getIntent());

        TIMManager.getInstance().addMessageListener(this);
    }

    protected SoundRing getSoundRing() {
        return new SoundRing();
    }

    protected void initData(Intent intent) {

        chatBean = (AVChatBean) intent.getSerializableExtra("bean");

        if (chatBean == null)
            return;

        //初始化UI
        initViewShow();

        //获取对方信息
        getOtherData(chatBean.otherId);

        //初始化Im
        initIm();

        initVideo();
    }

    private void startAnim() {

        if (isFinishing() || TextUtils.isEmpty(QiNiuChecker.get().getVideoAlert())) {
            return;
        }

        final TextView animView = findViewById(R.id.alert_tv);

        animView.setText(QiNiuChecker.get().getVideoAlert());

        //启动动画
        int screenWidth = ScreenUtils.getScreenWidth(AppManager.getInstance());

        ObjectAnimator enter = ObjectAnimator.ofFloat(animView, "translationX", screenWidth, 0);
        enter.setDuration(1000);

        ObjectAnimator stay = ObjectAnimator.ofFloat(animView, "translationX", 0, 0);
        stay.setDuration(10000);

        ObjectAnimator exit = ObjectAnimator.ofFloat(animView, "translationX", 0, -screenWidth);
        exit.setDuration(1000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(enter, stay, exit);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isFinishing()) {
                    animView.setText(null);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    /**
     * 用户进入
     */
    protected void userJoined() {

        //计费开始
        if (chatBean.countdown) {
            startCountTime();
        }

        //呼叫结束
        callingState(false);

        //取消30秒
        cancelAutoTimer();

        //关闭头像闪烁
        stopPlay();

        //排版双方画面
        toggleBigAndSmall();
    }

    /**
     * 初始化界面显示
     */
    private void initViewShow() {

        boolean isActor = chatBean.isActor();
        mCloseVideoIv.setVisibility(isActor ? View.GONE : View.VISIBLE);
        mCloseCameraTv.setVisibility(isActor ? View.GONE : View.VISIBLE);
        remoteCloseBtn.setVisibility(!isActor ? View.GONE : View.VISIBLE);

        boolean isRequest = chatBean.isRequest;
        mCoverIv.setVisibility(isRequest && !chatBean.isActor() ? View.VISIBLE : View.GONE);
        callingState(isRequest);
    }

    /**
     * 更新布局calling状态
     */
    private void callingState(boolean calling) {
        if (calling) {
            playMusic();
        } else {
            if (callingLayout != null) {
                ((ViewGroup) callingLayout.getParent()).removeView(callingLayout);
                callingLayout = null;
            }
        }
        chatLayout.setVisibility(calling ? View.GONE : View.VISIBLE);
        initAutoCountTimer();
    }

    /**
     * 获取单聊会话
     */
    private void initIm() {
        if (isFinishing())
            return;
        String peer = String.valueOf(10000 + chatBean.otherId);
        mConversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, peer);
        if (mConversation != null) {
            TIMManager.getInstance().addMessageListener(this);
        }
    }

    /**
     * 初始化文字聊天
     */
    private void initTextChat() {
        mTextRecyclerAdapter = new VideoChatTextRecyclerAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mTextListRv.setLayoutManager(manager);
        mTextListRv.setAdapter(mTextRecyclerAdapter);

        float scale = ScreenUtil.getWHScale(mContext);
        int width = DensityUtil.dip2px(mContext, 92);
        int height = (int) (width * scale);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        layoutParams.topMargin = DensityUtil.dip2px(mContext, 30);
        layoutParams.rightMargin = DensityUtil.dip2px(mContext, 10);
        layoutParams.gravity = Gravity.END;
        remoteView.setLayoutParams(layoutParams);
    }

    /**
     * 自动挂断
     */
    private void initAutoCountTimer() {
        cancelAutoTimer();
        if (mAutoHangUpTimer == null) {
            mAutoHangUpTimer = new CountDownTimer((long) (30 * 1000), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    countDownFinish();
                }
            };
            mAutoHangUpTimer.start();
        }
    }

    protected void countDownFinish() {
        ToastUtil.INSTANCE.showToast(R.string.no_response);
        hangUp(HANGUP_TIME_OUT, AppManager.getInstance().getUserInfo().t_id);
    }

    /**
     * 取消timer
     */
    protected void cancelAutoTimer() {
        if (mAutoHangUpTimer != null) {
            mAutoHangUpTimer.cancel();
            mAutoHangUpTimer = null;
        }
    }

    /**
     * 播放音频
     */
    protected void playMusic() {
        if (soundRing != null) {
            soundRing.start();
        }
    }

    protected void stopPlay() {
        if (soundRing != null) {
            soundRing.stop();
        }
    }

    RtcEngineEventHandler rtcEngineEventHandler = new RtcEngineEventHandler() {

        private void onError() {
            new AlertDialog.Builder(mContext)
                    .setMessage("进入房间失败")
                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create().show();
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            Log.d("EngineEvent", "onJoinChannelSuccess: " + channel);
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            Log.d("EngineEvent", "onUserOffline: " + uid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.INSTANCE.showToast("对方已退出");
                    hangUp(HANGUP_RTC, AppManager.getInstance().getUserInfo().t_id);
                }
            });
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            Log.d("EngineEvent", "onUserJoined: " + uid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mHaveUserJoin) {
                        mHaveUserJoin = true;
                        mHandler.postDelayed(mTimeRunnable, 1000);
                        setRemoteVideoView(uid);
                        userJoined();
                    }
                }
            });
        }

        @Override
        public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
            Log.d("EngineEvent", state + "onRemoteVideoStateChanged: " + reason);
        }

        @Override
        public void onUserMuteVideo(int uid, boolean muted) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    otherCameraDisable.setVisibility(muted ? View.VISIBLE : View.GONE);
                }
            });
        }

    };

    private void setRemoteVideoView(int uid) {
        mControlLl.setVisibility(View.VISIBLE);
        mCoverIv.setVisibility(View.GONE);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(this);
        rtcManager.rtcEngine().setupRemoteVideo(
                new VideoCanvas(
                        surfaceView,
                        VideoCanvas.RENDER_MODE_HIDDEN,
                        chatBean.roomId + "",
                        uid, Constants.VIDEO_MIRROR_MODE_ENABLED));
        remoteView.addView(surfaceView, 0, new FrameLayout.LayoutParams(-1, -1));
    }

    /**
     * 初始化video
     */
    protected void initVideo() {

        VideoEncoderConfiguration configuration = new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT);

        rtcManager = RtcManager.get();
        rtcManager.addRtcHandler(rtcEngineEventHandler);
        rtcManager.rtcEngine().setVideoEncoderConfiguration(configuration);
        rtcManager.rtcEngine().setVideoSource(new RtcVideoConsumer());
        rtcManager.rtcEngine().enableVideo();
        rtcManager.getCameraManager().setLocalPreview(localVideoView);
        rtcManager.startCamera();

        rtcManager.rtcEngine().enableLocalAudio(true);
        rtcManager.rtcEngine().enableLocalVideo(true);
        rtcManager.rtcEngine().setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        rtcManager.rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        rtcManager.rtcEngine().joinChannel(
                chatBean.sign,
                chatBean.roomId + "",
                null,
                AppManager.getInstance().getUserInfo().t_id);
        if (chatBean.closeVideo) {
            switchCamera(true);
        }
    }

    protected void brokenVIPLineRes(int roomId, int breakId, int code) {
        if (roomId != chatBean.roomId)
            return;
        ToastUtil.INSTANCE.showToast(R.string.have_hang_up_one);
        hangUp(code, AppManager.getInstance().getUserInfo().t_id);
    }

    /**
     * 切换大小视图
     */
    private void toggleBigAndSmall() {
        final ViewGroup.LayoutParams localParams = localView.getLayoutParams();
        final ViewGroup.LayoutParams remoteParams = remoteView.getLayoutParams();
        if (localParams.width > remoteParams.width) {
            remoteView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    remoteView.bringToFront();
                }
            }, 200);
        } else {
            localView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    localView.bringToFront();
                }
            }, 200);
        }
        remoteView.setLayoutParams(localParams);
        localView.setLayoutParams(remoteParams);
    }

    /**
     * 开始本地计时
     */
    private Runnable mTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isFinishing()) {

                mCurrentSecond = mCurrentSecond + 1000;
                mDesTv.setText(TimeUtil.getFormatHMS(mCurrentSecond));

                //递归调用本run able对象，实现每隔一秒一次执行任务
                mHandler.postDelayed(this, 1000);

                if (mCurrentSecond / 1000 % 30 == 0) {
                    getChatState();
                }

                int second = (int) mCurrentSecond / 1000;
                QiNiuChecker.get().checkTime(second, chatBean.getUserId(), chatBean.getActorId(), chatBean.roomId);
                if (QiNiuChecker.get().checkAnim(second)) {
                    startAnim();
                }
            }
        }
    };

    /**
     * 开始计时
     */
    private void startCountTime() {

        //用户端加入房间前已经成功开始了计费，主播端不操作计费
        if (!chatBean.countdown) {
            return;
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("anthorId", chatBean.otherId);
        paramMap.put("userId", getUserId());
        paramMap.put("chatType", AudioVideoRequester.CHAT_VIDEO);
        paramMap.put("roomId", chatBean.roomId);

        OkHttpUtils.post().url(ChatApi.VIDEO_CHAT_BIGIN_TIMING())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<String>>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                boolean ok = response != null && response.m_istatus == NetCode.SUCCESS;
                if (!ok) {
                    if (response != null) {
                        if (response.m_istatus == -7) {
                            VipAlertActivity.start(VideoChatActivity.this);
                        } else {
                            if (!TextUtils.isEmpty(response.m_strMessage)) {
                                ToastUtil.INSTANCE.showToast(getApplicationContext(), response.m_strMessage);
                            }
                        }
                    } else {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_retry);
                    }
                    cancelAutoTimer();
                    hangUp(HANGUP_BILLING, AppManager.getInstance().getUserInfo().t_id);
                } else {
                    LogUtil.i("开始计时成功");
                }
            }
        });
    }

    /**
     * 获取对方信息
     */
    private void getOtherData(int actorId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("coverUserId", actorId);
        OkHttpUtils.post().url(ChatApi.getUserInfoById())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<AudioUserBean>>() {
            @Override
            public void onResponse(BaseResponse<AudioUserBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS && response.m_object != null && !isFinishing()) {
                    updateUser(response.m_object);
                }
            }
        });
    }

    protected void updateUser(AudioUserBean bean) {

        //设置呼叫时,对方昵称
        mOtherNameTv.setText(bean.nickName);

        //左上角信息
        mInfoNickTv.setText(bean.nickName);

        //年龄
        mInfoAgeTv.setText(String.format("%s岁", bean.t_age));

        //头像
        String handImg = bean.handImg;
        if (!TextUtils.isEmpty(handImg)) {
            int width = DevicesUtil.dp2px(VideoChatActivity.this, 50);
            int height = DevicesUtil.dp2px(VideoChatActivity.this, 50);
            ImageLoadHelper.glideShowCornerImageWithUrl(this, handImg, mOtherHeadIv, 5, width, height);
            ImageLoadHelper.glideShowCircleImageWithUrl(VideoChatActivity.this,
                    handImg, mGiftHeadIv);
            ImageLoadHelper.glideShowCircleImageWithUrl(VideoChatActivity.this,
                    handImg, mInfoHeadIv);
        } else {
            mOtherHeadIv.setImageResource(R.drawable.default_head_img);
            mGiftHeadIv.setImageResource(R.drawable.default_head_img);
            mInfoHeadIv.setImageResource(R.drawable.default_head_img);
        }

        //余额
        if (chatBean.isActor()) {
            if (!TextUtils.isEmpty(bean.balance)) {
                String left = getString(R.string.left_gold_one) + bean.balance;
                mLeftGoldTv.setText(left);
                mLeftGoldTv.setVisibility(View.VISIBLE);
            }
        }

        if (chatBean.isRequest) {
            Glide.with(mContext)
                    .load(bean.t_cover_img)
                    .transform(new CenterCrop())
                    .into(mCoverIv);
        }

        mCityTv.setText(bean.t_city);

        setFollow(bean.isFollow == 1);
    }

    private void setFollow(boolean isFollow) {
        mFollowTv.setSelected(isFollow);
        mFollowTv.setText(isFollow ? "已关注" : "关注");
    }

    @OnClick({R.id.hang_up_iv,
            R.id.reward_iv,
            R.id.change_iv,
            R.id.local_view,
            R.id.remote_view,
            R.id.close_camera_iv,
            R.id.close_camera_tv,
            R.id.message_iv,
            R.id.hang_up_tv,
            R.id.close_micro_iv,
            R.id.close_remote_iv,
            R.id.follow_btn,
            R.id.text_cover_v})
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.follow_btn: {
                new FocusRequester() {
                    @Override
                    public void onSuccess(BaseResponse response, boolean focus) {
                        if (isFinishing()) {
                            return;
                        }
                        setFollow(focus);
                    }
                }.focus(chatBean.otherId, !v.isSelected());
                break;
            }

            //挂断
            case R.id.hang_up_tv:
            case R.id.hang_up_iv: {
                new AlertDialog.Builder(mContext)
                        .setMessage("你确定退出聊天吗？")
                        .setNegativeButton("狠心挂断", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                hangUp(HANGUP_CLICK_BREAK, AppManager.getInstance().getUserInfo().t_id);
                            }
                        })
                        .setPositiveButton("再聊一会", null).create().show();
                break;
            }

            //打赏礼物
            case R.id.reward_iv: {
                new GiftDialog(mContext, chatBean.otherId).show();
                break;
            }

            //切换前后置
            case R.id.change_iv: {
                rtcManager.getCameraManager().switchCamera();
                break;
            }

            //屏蔽远端
            case R.id.close_remote_iv: {
                if (remoteCoverView.getTag() == null) {
                    remoteCoverView.setVisibility(remoteCoverView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    remoteCloseBtn.setSelected(remoteCoverView.getVisibility() == View.VISIBLE);
                }
                break;
            }

            case R.id.text_cover_v:
            case R.id.local_view:
            case R.id.remote_view: {

                ViewGroup viewGroup = (ViewGroup) v.getParent();
                int index0 = viewGroup.indexOfChild(v);

                if (v.getId() == R.id.text_cover_v || index0 == 0) {
                    if (chatLayout.getVisibility() != View.VISIBLE) {
                        return;
                    }
                    //显示或隐藏操作按钮
                    if (mControlLl.getVisibility() == View.VISIBLE) {
                        mControlLl.setVisibility(View.INVISIBLE);
                        mTextListRv.setVisibility(View.GONE);
                    } else {
                        mControlLl.setVisibility(View.VISIBLE);
                        mTextListRv.setVisibility(View.VISIBLE);
                    }
                } else {
                    //切换视图
                    toggleBigAndSmall();
                }
                break;
            }

            //关闭摄像头按钮
            case R.id.close_camera_tv:
            case R.id.close_camera_iv: {
                switchCamera(!v.isSelected());
                break;
            }

            //文字消息
            case R.id.message_iv: {
                final InputDialogFragment inputDialogFragment = new InputDialogFragment();
                inputDialogFragment.setOnTextSendListener(new InputDialogFragment.OnTextSendListener() {
                    @Override
                    public void onTextSend(String text) {
                        if (!TextUtils.isEmpty(text)) {
                            sendTextMessage(text);
                            inputDialogFragment.dismiss();
                        } else {
                            ToastUtil.INSTANCE.showToast(R.string.please_input_text_message);
                        }
                    }
                });
                inputDialogFragment.show(getSupportFragmentManager(), "tag");
                break;
            }

            //关闭麦克风
            case R.id.close_micro_iv: {
                clickMicro();
                break;
            }
        }
    }

    /**
     * 禁用麦克风
     */
    private void clickMicro() {
        mCloseMicroIv.setSelected(!mCloseMicroIv.isSelected());
        rtcManager.rtcEngine().muteLocalAudioStream(mCloseMicroIv.isSelected());
    }

    /**
     * 使能摄像头
     */
    protected void switchCamera(boolean mute) {
        if (rtcManager != null) {
            rtcManager.rtcEngine().muteLocalVideoStream(mute);
            localCoverView.setVisibility(mute ? View.VISIBLE : View.GONE);
        }
        if (callingLayout != null) {
            mCloseCameraTv.setSelected(mute);
            mCloseCameraTv.setText(mute ? R.string.off_camera : R.string.open_camera);
        }
        mCloseVideoIv.setSelected(mute);
    }

    /**
     * 正常挂断视频
     * hangUpId = 0 系统挂断
     */
    protected void hangUp(int type, int hangUpId) {

        //过滤掉非本次通信的挂断
        if (type != HANGUP_SYSTEM && (hangUpId != AppManager.getInstance().getUserInfo().t_id && hangUpId != chatBean.otherId)) {
            return;
        }

        finish();
    }

    /**
     * 请求挂断
     */
    protected void requestBreak(int roomId, OnCommonListener<Boolean> listener) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("roomId", roomId);
        OkHttpUtils.post().url(ChatApi.BREAK_LINK())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build()
                .execute(new AjaxCallback<BaseResponse<String>>() {
                    @Override
                    public void onResponse(BaseResponse response, int id) {
                        if (response != null && response.m_istatus == NetCode.SUCCESS && listener != null) {
                            listener.execute(true);
                        }
                    }
                });
    }

    //-------------------权限---------------------
    protected void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this,
                        permissions.toArray(new String[0]),
                        100);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    /**
     * 对方已挂断
     */
    protected void onHangUp(int roomId, int breakUserID) {
        if (roomId != chatBean.roomId)
            return;
        int type = HANGUP_BREAK;
        if (breakUserID == 0) {
            type = HANGUP_SYSTEM;
        }
        hangUp(type, breakUserID);
    }

    /**
     * 跳转评价页
     */
    private void toComment() {
        if (mHaveUserJoin && !isDisable) {
            VideoCommentActivity.start(chatBean);
        }
    }

    /**
     * 礼物动画
     */
    private void startGiftInAnim(CustomMessageBean bean, boolean fromSend) {

        String lastDes = mGiftDesTv.getText().toString().trim();

        //是否礼物连击
        if (!TextUtils.isEmpty(lastDes) && !lastDes.contains(bean.gift_name)) {
            mSingleTimeSendGiftCount = 0;
        }

        if (bean.gift_number == 0) {
            bean.gift_number = 1;
        }

        mSingleTimeSendGiftCount += bean.gift_number;
        if (mGiftLl.getVisibility() != View.VISIBLE) {//未显示礼物动画
            mGiftDesTv.setText(fromSend ? R.string.send_to : R.string.send_you);
            mGiftDesTv.append(bean.gift_name);

            ImageLoadHelper.glideShowImageWithUrl(this, bean.gift_still_url, mGiftIv);
            mGiftNumberTv.setText(String.format("x%s", bean.gift_number));

            mGiftLl.setVisibility(View.VISIBLE);
            mGiftLl.clearAnimation();
            TranslateAnimation mGiftLayoutInAnim = (TranslateAnimation) AnimationUtils.loadAnimation(getApplicationContext(), R.anim.lp_gift_in);
            mGiftLl.setAnimation(mGiftLayoutInAnim);
            mGiftLayoutInAnim.start();
            mHandler.removeCallbacks(mGiftRunnable);
            mGiftLayoutInAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //3秒后消失
                    mHandler.postDelayed(mGiftRunnable, 3000);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else {
            String giftNumber = getResources().getString(R.string.multi) + mSingleTimeSendGiftCount;
            mGiftNumberTv.setText(giftNumber);
            mHandler.removeCallbacks(mGiftRunnable);
            mHandler.postDelayed(mGiftRunnable, 3000);
            startComboAnim(mGiftNumberTv);
        }

    }

    private void startGiftOutAnim() {
        mGiftLl.clearAnimation();
        Animation mGiftLayoutInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.lp_gift_out);
        mGiftLl.setAnimation(mGiftLayoutInAnim);
        mGiftLayoutInAnim.start();
        mGiftLayoutInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mSingleTimeSendGiftCount != 1) {
                    mGiftLl.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void startComboAnim(final TextView giftNumView) {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(giftNumView, "scaleX", 1.8f, 1.0f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(giftNumView, "scaleY", 1.8f, 1.0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(300);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(anim1, anim2);
        animSet.start();
    }

    /**
     * 礼物runnable
     */
    private Runnable mGiftRunnable = new Runnable() {
        @Override
        public void run() {
            mSingleTimeSendGiftCount = 0;
            startGiftOutAnim();
        }
    };

    /**
     * 被封号
     */
    protected void beenShutDown() {
        isDisable = true;
        hangUp(HANGUP_BEAN_SUSPEND, AppManager.getInstance().getUserInfo().t_id);
    }

    /**
     * 每隔30s判断通话状态
     * int videoUserId 视频用户Id
     * int videoCoverUserId, 视频主播Id
     * int userId 自己Id
     * int roomId 房间号
     */
    private void getChatState() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("videoUserId", chatBean.getUserId());
        paramMap.put("videoCoverUserId", chatBean.getActorId());
        paramMap.put("roomId", chatBean.roomId);

        OkHttpUtils.post().url(ChatApi.getVideoStatus())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<Integer>>() {

            @Override
            public void onResponse(BaseResponse<Integer> response, int id) {
                //后台状态挂断
                if (response != null && response.m_istatus == NetCode.SUCCESS && response.m_object != null) {
                    if (response.m_object != 1) {
                        ToastUtil.INSTANCE.showToast("视频已挂断");
                        hangUp(HANGUP_LOOP_BREAK, AppManager.getInstance().getUserInfo().t_id);
                    }
                }
            }
        });
    }

    protected void moneyNotEnough() {
        if (!isFinishing()) {
            showGoldJustEnoughDialog();
        }
    }

    /**
     * 显示约豆一分钟后挂断dialog
     */
    private void showGoldJustEnoughDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_money_not_enough_layout, null);
        setGoldDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置头像选择dialog的view
     */
    private void setGoldDialogView(View view, final Dialog mDialog) {
        //取消
        TextView ignore_tv = view.findViewById(R.id.ignore_tv);
        ignore_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //充值
        TextView charge_tv = view.findViewById(R.id.charge_tv);
        charge_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空房间
                Intent intent = new Intent(getApplicationContext(), ChargeActivity.class);
                startActivity(intent);
                mDialog.dismiss();
            }
        });
    }

    boolean isAddVideoHint;

    //接通视频的时候,下发提示消息,主播用户都要提示
    protected void onVideoStartSocketHint(String hintMessage) {
        if (!isAddVideoHint && !TextUtils.isEmpty(hintMessage)) {
            isAddVideoHint = true;
            mTextRecyclerAdapter.addHintData(hintMessage);
            if (mTextListRv != null) {
                if (mTextListRv.getVisibility() != View.VISIBLE) {
                    mTextListRv.setVisibility(View.VISIBLE);
                }
                mTextListRv.scrollToPosition(mTextRecyclerAdapter.getPosition());
            }
        }
    }

    //----------------------------------消息部分-----------------------------

    /**
     * 新消息
     */
    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        if (isFinishing()) {
            return false;
        }
        LogUtil.i("单人聊天页面新消息来了");
        String peer = String.valueOf(10000 + chatBean.otherId);
        for (TIMMessage timMessage : list) {
            TIMConversation conversation = timMessage.getConversation();
            if (conversation != null
                    && conversation.getType() == TIMConversationType.C2C
                    && !TextUtils.isEmpty(conversation.getPeer())
                    && TextUtils.equals(conversation.getPeer(), peer)) {
                addNewMessage(timMessage, true);
            }
        }
        mTextListRv.setVisibility(View.VISIBLE);
        mTextListRv.scrollToPosition(mTextRecyclerAdapter.getItemCount() - 1);
        return true;
    }

    /**
     * 列表添加消息
     */
    private void addNewMessage(TIMMessage msg, boolean needParse) {
        for (int i = 0; i < msg.getElementCount(); ++i) {
            ChatMessageBean chatMessageBean = new ChatMessageBean();
            chatMessageBean.isSelf = msg.isSelf();
            chatMessageBean.time = msg.timestamp();
            TIMElem elem = msg.getElement(i);
            if (elem.getType() == TIMElemType.Text) {//处理文本消息
                TIMTextElem textElem = (TIMTextElem) elem;
                chatMessageBean.type = 0;
                chatMessageBean.textContent = textElem.getText();
                mTextRecyclerAdapter.addData(chatMessageBean);
            } else if (elem.getType() == TIMElemType.Custom) {//礼物
                TIMCustomElem customElem = (TIMCustomElem) elem;
                byte[] data = customElem.getData();
                String json = new String(data);
                if (needParse) {
                    parseCustomMessage(json);
                }
            }
        }
    }

    /**
     * 解析自定义消息
     */
    private void parseCustomMessage(String json) {
        if (isFinishing()) {
            return;
        }
        CustomMessageBean bean = CustomMessageBean.parseBean(json);
        if (bean != null) {
            if (ImCustomMessage.Type_gift.equals(bean.type)) {
                //礼物
                LogUtil.i("接收到的礼物: " + bean.gift_name);
                startGif(bean.gift_gif_url);
                startGiftInAnim(bean, false);
            } else if (ImCustomMessage.Type_pulp.equals(bean.type)) {
                //远端视频非法
                IllegalVideo(false);
            }
        }
    }

    /**
     * 对方视频非法
     */
    private void IllegalVideo(boolean self) {
        if (!self) {
            remoteCoverView.setTag("");
            remoteCoverView.setVisibility(View.VISIBLE);
            remoteCloseBtn.setSelected(true);
            illegalTv.setText(R.string.illegal_info_other);
        } else {
            localIllegalView.setVisibility(View.VISIBLE);
        }
        illegalTv.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }
                if (!self) {
                    remoteCoverView.setTag(null);
                    remoteCoverView.setVisibility(View.GONE);
                    remoteCloseBtn.setSelected(false);
                    illegalTv.setText(null);
                } else {
                    localIllegalView.setVisibility(View.GONE);
                }
                if (illegalAlert != null) {
                    illegalAlert.dismiss();
                    illegalAlert = null;
                }
            }
        }, illegalTime);
    }

    /**
     * 发送文本消息
     */
    private void sendTextMessage(String text) {
        if (TextUtils.isEmpty(text)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_text_message);
            return;
        }
        text = IMFilterHelper.getInstance().switchFilterWord(getApplicationContext(), text);
        TIMTextElem textElem = new TIMTextElem();
        textElem.setText(text);
        sendMessage(textElem);
    }

    /**
     * 发送消息
     */
    private void sendMessage(TIMElem timElem) {
        //构造一条消息并添加一个文本内容
        TIMMessage msg = new TIMMessage();
        if (msg.addElement(timElem) != 0) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.element_send_fail);
            return;
        }
        if (mConversation != null) {
            if (timElem != null) {
                //发送消息
                mConversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {
                    @Override
                    public void onError(int code, String desc) {//发送消息失败
                        //错误码 code 和错误描述 desc，可用于定位请求失败原因
                        String content = "TIM send message failed. code: " + code + " errmsg: " + desc;
                        LogUtil.i(content);
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), content);
                        IMHelper.resLogin(code);
                    }

                    @Override
                    public void onSuccess(TIMMessage msg) {//发送消息成功
                        LogUtil.i("TIM SendMsg bitmap");
                        addNewMessage(msg, false);
                        mTextListRv.setVisibility(View.VISIBLE);
                        mTextListRv.scrollToPosition(mTextRecyclerAdapter.getItemCount() - 1);
                    }
                });
            } else {
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.element_send_fail);
            }
        } else {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.tim_send_fail);
            initIm();
        }
    }

    /**
     * 开始GIF动画
     */
    private void startGif(String path) {
        if (!TextUtils.isEmpty(path)) {
            SVGAParser parser = new SVGAParser(this);
            try {
                URL url = new URL(path);
                parser.parse(url, new SVGAParser.ParseCompletion() {
                    @Override
                    public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                        if (isFinishing())
                            return;
                        SVGADrawable drawable = new SVGADrawable(videoItem);
                        mGifSv.setImageDrawable(drawable);
                        mGifSv.startAnimation();
                    }

                    @Override
                    public void onError() {

                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 订阅Socket Message
     */
    int[] Subscriptions = {
            Mid.brokenVIPLineRes,
            Mid.brokenUserLineRes,
            Mid.HAVE_HANG_UP,
            Mid.BEAN_SUSPEND,
            Mid.MONEY_NOT_ENOUGH,
            Mid.invalidChat
    };

    OnCommonListener<SocketResponse> subscription = new OnCommonListener<SocketResponse>() {

        @Override
        public void execute(SocketResponse response) {
            switch (response.mid) {
                case Mid.brokenVIPLineRes:
                case Mid.brokenUserLineRes:
                    brokenVIPLineRes(response.roomId, response.breakUserId, response.mid);
                    break;
                case Mid.HAVE_HANG_UP:
                    onHangUp(response.roomId, response.breakUserId);
                    break;
                case Mid.BEAN_SUSPEND:
                    beenShutDown();
                    break;
                case Mid.MONEY_NOT_ENOUGH:
                    moneyNotEnough();
                    break;
                case Mid.invalidChat:
                    new InvalidChatDialog(VideoChatActivity.this, response.content).show();
                    break;
            }
        }

    };
}