package com.lovechatapp.chat.activity;

import android.Manifest;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.AudioUserBean;
import com.lovechatapp.chat.bean.MultipleChatInfo;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.dialog.VipDialog;
import com.lovechatapp.chat.helper.ChargeHelper;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.AudioVideoRequester;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.socket.SocketMessageManager;
import com.lovechatapp.chat.socket.domain.Mid;
import com.lovechatapp.chat.socket.domain.SocketResponse;
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
import okhttp3.Call;

/**
 * 1v2接听页面
 */
public class CallingActivity extends BaseActivity {

    @BindView(R.id.head_iv)
    ImageView mHeadIv;

    @BindView(R.id.head2_iv)
    ImageView mHead2Iv;

    @BindView(R.id.name_tv)
    TextView mNameTv;

    @BindView(R.id.name2_tv)
    TextView mName2Tv;

    @BindView(R.id.invite_tv)
    TextView inviteTv;

    @BindView(R.id.camera_ll)
    LinearLayout mCameraLl;

    @BindView(R.id.camera_iv)
    ImageView mCameraIv;

    @BindView(R.id.camera_tv)
    TextView mCameraTv;

    private SoundRing soundRing;

    //30秒计时器 自动挂断
    private CountDownTimer mAutoHangUpTimer;

    private MultipleChatInfo chatInfo;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_calling_1v2);
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    protected void onContentAdded() {

        //正在视频或者语音则不弹出来电call
        if (AppManager.getInstance().getActivityObserve().isChatting()) {
            finish();
            return;
        }

        SocketMessageManager.get().subscribe(subscription, Subscriptions);

        soundRing = new SoundRing();

        chatInfo = (MultipleChatInfo) getIntent().getSerializableExtra("chatInfo");

        needHeader(false);

        initAutoCountTimer();

        playMusic();

        getPassUserInfo();

        //语音
        if (chatInfo.isAudioChat()) {
            findViewById(R.id.video_ll).setVisibility(View.GONE);
        }
        //视频
        else {
            findViewById(R.id.audio_ll).setVisibility(View.GONE);
        }
    }

    /**
     * 倒计时
     */
    private void initAutoCountTimer() {
//        if (AppManager.getInstance().getUserInfo().isWomenActor()) {
//            mCameraLl.setVisibility(View.VISIBLE);
//            boolean mute = SharedPreferenceHelper.getMute(getApplicationContext());
//            if (mute) {//缓存是关闭的
//                mCameraIv.setSelected(false);
//                mCameraTv.setText(getResources().getString(R.string.off_camera));
//            } else {
//                mCameraIv.setSelected(true);
//                mCameraTv.setText(getResources().getString(R.string.open_camera));
//            }
//        }
        if (mAutoHangUpTimer == null) {
            mAutoHangUpTimer = new CountDownTimer(35 * 1000, 1000) {

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
        paramMap.put("coverUserId", chatInfo.connectUserId);
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

                        inviteTv.setText(String.format("%1$s邀请你加入他的一对二%2$s聊天房间",
                                bean.nickName, !chatInfo.isAudioChat() ? "视频" : "语音"));

                        //昵称
                        mNameTv.setText(bean.nickName);
                        mName2Tv.setText(bean.nickName);

                        //头像
                        Glide.with(mContext)
                                .load(bean.handImg)
                                .error(R.drawable.default_head_img)
                                .transform(new CircleCrop())
                                .into(mHeadIv);

                        Glide.with(mContext)
                                .load(bean.handImg)
                                .error(R.drawable.default_head_img)
                                .transform(new CircleCrop())
                                .into(mHead2Iv);
                    }
                }
            }
        });
    }

    @OnClick({R.id.hang_up_tv, R.id.accept_tv, R.id.camera_ll})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hang_up_tv: {//挂断
                hangUp();
                break;
            }
            case R.id.accept_tv: {//接通
                PermissionUtil.requestPermissions(mContext, new PermissionUtil.OnPermissionListener() {

                    @Override
                    public void onPermissionGranted() {
                        showLoadingDialog();
                        AudioVideoRequester.getAgoraSign(chatInfo.mansionRoomId, new OnCommonListener<String>() {
                            @Override
                            public void execute(String s) {
                                if (isFinishing()) {
                                    return;
                                }
                                dismissLoadingDialog();
                                if (TextUtils.isEmpty(s)) {
                                    ToastUtil.INSTANCE.showToast("接听失败，请重试");
                                } else {
                                    chatInfo.sign = s;
                                    startCountTime();
                                }
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        ToastUtil.INSTANCE.showToast("无麦克风或者摄像头权限，无法使用该功能");
                    }

                }, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
                break;
            }
            case R.id.camera_ll: {//关闭摄像头
                if (mCameraIv.isSelected()) {//如果摄像头是开启的
                    mCameraIv.setSelected(false);
                    mCameraTv.setText(getResources().getString(R.string.off_camera));
                    SharedPreferenceHelper.saveMute(getApplicationContext(), true);
                } else {
                    mCameraIv.setSelected(true);
                    mCameraTv.setText(getResources().getString(R.string.open_camera));
                    SharedPreferenceHelper.saveMute(getApplicationContext(), false);
                }
                break;
            }
        }
    }

    /**
     * 计费
     */
    private void startCountTime() {
        showLoadingDialog();

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("roomId", chatInfo.roomId);
        paramMap.put("chatType", chatInfo.chatType);
        paramMap.put("mansionRoomId", chatInfo.mansionRoomId);
        OkHttpUtils.post().url(ChatApi.videoMansionChatBeginTiming())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                boolean ok = response != null && response.m_istatus == NetCode.SUCCESS;
                if (!ok) {
                    boolean notVipAlert = false;
                    if (response != null) {
                        if (response.m_istatus == -7) {
                            new VipDialog(CallingActivity.this).show();
                            notVipAlert = true;
                        } else if (response.m_istatus == -1) {
                            ChargeHelper.showSetCoverDialog(CallingActivity.this);
                        } else {
                            if (!TextUtils.isEmpty(response.m_strMessage)) {
                                ToastUtil.INSTANCE.showToast(getApplicationContext(), response.m_strMessage);
                            }
                        }
                    } else {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_retry);
                    }
                    if (!notVipAlert) {
                        cancelAutoTimer();
                        hangUp();
                    }
                } else {
                    LogUtil.i("开始计费成功");
                    MultipleVideoActivity.start(mContext, chatInfo, false);
                    cancelAutoTimer();
                    finish();
                }
                dismissLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 府邸挂断
     * breakUserId
     * breakType
     * roomId
     * mansionRoomId
     */
    private void hangUp() {
        cancelAutoTimer();
        finish();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("breakUserId", getUserId());
        paramMap.put("roomId", chatInfo.roomId);
        paramMap.put("mansionRoomId", chatInfo.mansionRoomId);
        paramMap.put("breakType", chatInfo.chatType);
        OkHttpUtils.post().url(ChatApi.breakMansionLink())
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
        if (roomId != chatInfo.roomId)
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
    protected void onDestroy() {

        SocketMessageManager.get().unsubscribe(subscription);

        super.onDestroy();

        if (soundRing != null) {
            soundRing.stop();
        }

        cancelAutoTimer();
    }

    /**
     * 订阅Socket Message
     */
    int[] Subscriptions = {
            Mid.video_brokenLineRes
    };

    OnCommonListener<SocketResponse> subscription = new OnCommonListener<SocketResponse>() {

        @Override
        public void execute(SocketResponse response) {
            switch (response.mid) {
                case Mid.video_brokenLineRes:
                    onHangUp(response.roomId, response.breakUserId);
                    break;
            }
        }

    };
}