package com.lovechatapp.chat.activity;

import android.view.View;

import com.lovechatapp.chat.base.BaseActivity;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;

import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：大房间直播页面
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class BigHouseActivity extends BaseActivity implements TIMMessageListener {
    @Override
    protected View getContentView() {
        return null;
    }

    @Override
    protected void onContentAdded() {

    }

    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        return false;
    }

//    //视频View
//    @BindView(R.id.content_fl)
//    ConstraintLayout mContentFl;
//
//    //模糊背景
//    @BindView(R.id.furry_cover_iv)
//    ImageView mFurryCoverIv;
//
//    //直播已结束
//    @BindView(R.id.live_end_tv)
//    TextView mLiveEndTv;
//
//    //主播开始直播FrameLayout
//    @BindView(R.id.start_live_fl)
//    FrameLayout mStartLiveFl;
//
//    //正在直播界面(主播 用户界面应该是差不多的)
//    @BindView(R.id.living_layout_fl)
//    FrameLayout mLivingLayoutFl;
//
//    //-----开始直播相关---
//    //封面图
//    @BindView(R.id.cover_iv)
//    ImageView mCoverIv;
//
//    //美颜UI
//    @BindView(R.id.beautyView)
//    FrameLayout beautyFrameLayout;
//
//    //底部
//    @BindView(R.id.start_bottom_ll)
//    LinearLayout mStartBottomLl;
//
//    //----------正在直播页面-------------
//
//    //头像
//    @BindView(R.id.head_iv)
//    ImageView mHeadIv;
//
//    //昵称
//    @BindView(R.id.nick_tv)
//    TextView mNickTv;
//
//    //关注人数
//    @BindView(R.id.focus_number_tv)
//    TextView mFocusNumberTv;
//
//    //关注按钮
//    @BindView(R.id.focus_tv)
//    TextView mFocusTv;
//
//    //观看人数
//    @BindView(R.id.total_number_tv)
//    TextView mTotalNumberTv;
//
//    @BindView(R.id.top_user_rv)
//    RecyclerView mTopUserRv;
//
//    //顶部
//    @BindView(R.id.camera_iv)
//    ImageView mCameraIv;
//
//    //文字消息
//    @BindView(R.id.message_rv)
//    RecyclerView mMessageRv;
//
//    //礼物
//    @BindView(R.id.gift_container_ll)
//    LinearLayout mGiftContainerLl;
//
//    //上方信息
//    @BindView(R.id.top_info_ll)
//    LinearLayout mTopInfoLl;
//
//    //gif动图礼物
//    @BindView(R.id.gif_sv)
//    SVGAImageView mGifSv;
//
//    //视频聊天相关
//    private TTTRtcEngine mTttRtcEngine;
//
//    //头部用户列表
//    private TopUserRecyclerAdapter mTopUserRecyclerAdapter;
//
//    //分享
//    private Tencent mTencent;
//    private IWXAPI mWxApi;
//    private CoverBean mCoverBean;//主播还未开播分享用的封面 昵称
//    private String mActorHeadImg;//主播开播后的头像地址
//
//    //其他
//    private long mRoomId;
//    private int mFromType;//是主播开播,还是用户进来看
//    private int mActorId;//主播ID
//    private long mChatRoomId;//聊天室ID
//
//    //文字聊天
//    private BigRoomChatTextRecyclerAdapter mTextRecyclerAdapter;
//
//    //分享链接
//    private String mShareUrl;
//
//    //会话
//    private TIMConversation mConversation;
//
//    @Override
//    protected View getContentView() {
//        return inflate(R.layout.activity_big_house_layout);
//    }
//
//    @Override
//    protected boolean supportFullScreen() {
//        return true;
//    }
//
//    @Override
//    protected void onContentAdded() {
//        needHeader(false);
//
//        SocketMessageManager.get().subscribe(subscription, Subscriptions);
//
//        mFromType = getIntent().getIntExtra(Constant.FROM_TYPE, Constant.FROM_USER);
//        mRoomId = getIntent().getLongExtra(Constant.ROOM_ID, 0);
//        mActorId = getIntent().getIntExtra(Constant.ACTOR_ID, 0);
//
//        initConfig();
//        initTTT();
//        initView();
//    }
//
//    /**
//     * 初始化配置
//     */
//    private void initConfig() {
//        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, getApplicationContext());
//        mWxApi = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID, true);
//        mWxApi.registerApp(Constant.WE_CHAT_APPID);
//        //礼物
//        LPAnimationManager.init(this);
//        LPAnimationManager.addGiftContainer(mGiftContainerLl);
//        //TIM回调
//        TIMManager.getInstance().addMessageListener(this);
//    }
//
//    /**
//     * 根据角色不同, 进来的界面也不同
//     */
//    @SuppressLint("ClickableViewAccessibility")
//    private void initView() {
//        if (mFromType == Constant.FROM_ACTOR) {
//
//            //主播进来开播,显示开始直播页面
//            mStartLiveFl.setVisibility(VISIBLE);
//
//            //显示切换摄像头
//            mCameraIv.setVisibility(VISIBLE);
//
//            if (BeautyManager.BeautyEnable) {
//                BeautyManager.get().getControlView(beautyFrameLayout);
//            }
//
//            //设置触摸关闭美颜
//            mStartLiveFl.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    v.performClick();
//                    mStartBottomLl.setVisibility(View.VISIBLE);
//                    beautyFrameLayout.setVisibility(GONE);
//                    return false;
//                }
//            });
//            //获取直播自己封面
//            getUserCoverImg();
//        } else {
//            //用户进来,显示正在直播
//            mLivingLayoutFl.setVisibility(VISIBLE);
//            mFurryCoverIv.setVisibility(VISIBLE);
//            //不显示切换摄像头
//            mCameraIv.setVisibility(GONE);
//            //聊天室ID,用户直接加入聊天室
//            mChatRoomId = getIntent().getLongExtra(Constant.CHAT_ROOM_ID, 0);
//            joinChatRoom(String.valueOf(mChatRoomId));
//            //获取直播间信息
//            getActorInfo(getUserId(), String.valueOf(mActorId));
//        }
//
//        //初始化头部用户
//        mTopUserRecyclerAdapter = new TopUserRecyclerAdapter(this);
//        mTopUserRv.setAdapter(mTopUserRecyclerAdapter);
//        LinearLayoutManager manager = new LinearLayoutManager(this,
//                LinearLayoutManager.HORIZONTAL, false);
//        mTopUserRv.setLayoutManager(manager);
//        mTopUserRecyclerAdapter.setOnItemClickListener(new TopUserRecyclerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BigRoomUserBean bean) {
//                if (bean.t_id > 0) {
//                    showUserInfoDialog(bean.t_id);
//                }
//            }
//        });
//        //文字列表配置
//        mTextRecyclerAdapter = new BigRoomChatTextRecyclerAdapter(this);
//        LinearLayoutManager textManager = new LinearLayoutManager(this);
//        mMessageRv.setLayoutManager(textManager);
//        mMessageRv.setAdapter(mTextRecyclerAdapter);
//
//        //分享
//        initShare();
//    }
//
//    /**
//     * 初始化TIM
//     */
//    private void joinChatRoom(String groupId) {
//        //如果是主播进来, 创建音视频聊天室（AVChatRoom）
//        if (mFromType == Constant.FROM_ACTOR) {
//            TIMGroupManager.CreateGroupParam param = new TIMGroupManager.CreateGroupParam("AVChatRoom",
//                    groupId);
//            param.setGroupId(groupId);
//            TIMGroupManager.getInstance().createGroup(param, new TIMValueCallBack<String>() {
//                @Override
//                public void onError(int code, String desc) {
//                    LogUtil.i("创建群失败. code: " + code + " errmsg: " + desc);
//                }
//
//                @Override
//                public void onSuccess(String s) {
//                    LogUtil.i("创建群成功, 群ID: " + s);
//                }
//            });
//        } else {//用户进来 申请加入聊天室
//            TIMGroupManager.getInstance().applyJoinGroup(groupId,
//                    "some reason", new TIMCallBack() {
//                        @Override
//                        public void onError(int code, String desc) {
//                            LogUtil.i("申请加入群失败 err code = " + code + ", desc = " + desc);
//                        }
//
//                        @Override
//                        public void onSuccess() {
//                            LogUtil.i("申请加入群成功 success");
//                        }
//                    });
//        }
//        //获取会话扩展实例
//        mConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, groupId);
//    }
//
//    /**
//     * 初始化分享
//     */
//    private void initShare() {
//        String saveUrl = SharedPreferenceHelper.getShareUrl(getApplicationContext());
//        if (!TextUtils.isEmpty(saveUrl)) {
//            mShareUrl = saveUrl;
//        }
//        getShareUrl();
//    }
//
//    /**
//     * 初始化TTT
//     */
//    private void initTTT() {
//        BeautyManager.get().create();
//        mTttRtcEngine = TTTRtcEngine.create(getApplicationContext(), Constant.TTT_APP_ID,
//                new TTTRtcEngineEventHandler() {
//                    @Override
//                    public void onError(int errorType) {
//                        super.onError(errorType);
//                        if (errorType == ERROR_ENTER_ROOM_TIMEOUT) {
//                            LogUtil.i("超时，10秒未收到服务器返回结果");
//                        } else if (errorType == ERROR_ENTER_ROOM_UNKNOW) {
//                            LogUtil.i("无法连接服务器");
//                        } else if (errorType == ERROR_ENTER_ROOM_VERIFY_FAILED) {
//                            LogUtil.i("验证码错误");
//                        } else if (errorType == ERROR_ENTER_ROOM_BAD_VERSION) {
//                            LogUtil.i("版本错误");
//                        } else if (errorType == 6) {
//                            LogUtil.i("该直播间不存在");
//                        }
//                    }
//
//                    @Override
//                    public void onJoinChannelSuccess(String channel, final long uid, int elapsed) {
//                        super.onJoinChannelSuccess(channel, uid, elapsed);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (mFromType == Constant.FROM_ACTOR) {//是主播自己
//                                    LogUtil.i("主播加入房间成功: " + uid);
//                                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.open_success);
//                                    dismissLoadingDialog();
//                                    //改变界面
//                                    mStartLiveFl.setVisibility(GONE);
//                                    mLivingLayoutFl.setVisibility(VISIBLE);
//                                } else {
//                                    LogUtil.i("用户加入房间成功: " + uid);
//                                }
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onUserJoined(final long nUserId, final int identity, int elapsed) {
//                        super.onUserJoined(nUserId, identity, elapsed);
//                        LogUtil.i("用户加入: nUserId = " + nUserId + "  identity: " + identity);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (identity == CLIENT_ROLE_ANCHOR && mFromType == Constant.FROM_USER) {
//                                    //获取远端主播的视频
//                                    SurfaceView mSurfaceView = TTTRtcEngine.CreateRendererView(BigHouseActivity.this);
//                                    mTttRtcEngine.setupRemoteVideo(new VideoCanvas(nUserId,
//                                            Constants.RENDER_MODE_HIDDEN, mSurfaceView));
//                                    mContentFl.addView(mSurfaceView);
//                                }
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onLocalVideoFrameCaptured(TTTVideoFrame frame) {
//                        super.onLocalVideoFrameCaptured(frame);
//                        BeautyManager.get().onDrawFrame(frame);
//                    }
//
//                    @Override
//                    public void onUserKicked(long uid, int reason) {
//                        super.onUserKicked(uid, reason);
//                        LogUtil.i("用户端收到: onUserKicked:  uid:  " + uid);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mLiveEndTv.setText(getString(R.string.live_end));
//                                mFurryCoverIv.setVisibility(VISIBLE);
//                                mLiveEndTv.setVisibility(VISIBLE);
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onFirstRemoteVideoFrame(long uid, int width, int height, int elapsed) {
//                        super.onFirstRemoteVideoFrame(uid, width, height, elapsed);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (mFromType == Constant.FROM_USER) {
//                                    mFurryCoverIv.setVisibility(GONE);
//                                }
//                            }
//                        });
//                    }
//                });
//        if (mTttRtcEngine != null) {
//            mTttRtcEngine.setLogFilter(LOG_FILTER_OFF);
//            mTttRtcEngine.enableVideo();
//            mTttRtcEngine.setChannelProfile(CHANNEL_PROFILE_LIVE_BROADCASTING);
//            mTttRtcEngine.setVideoProfile(480, 640, 15, 900);
//            if (mFromType == Constant.FROM_ACTOR) {//主播
//                mTttRtcEngine.muteLocalAudioStream(false);
//                mTttRtcEngine.setClientRole(CLIENT_ROLE_ANCHOR);
//                mTttRtcEngine.startPreview();
//                delayShow();
//            } else {//观众
//                LogUtil.i("用户加入:  房间号: " + mRoomId);
//                mTttRtcEngine.muteLocalAudioStream(true);
//                mTttRtcEngine.setClientRole(CLIENT_ROLE_AUDIENCE);
//                mTttRtcEngine.joinChannel("", String.valueOf(mRoomId), Integer.parseInt(getUserId()));
//            }
//        }
//    }
//
//    /**
//     * 上传自己的流
//     */
//    private void delayShow() {
//        getWindow().getDecorView().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                SurfaceView mLocalSurfaceView = TTTRtcEngine.CreateRendererView(BigHouseActivity.this);
//                mTttRtcEngine.setupLocalVideo(new VideoCanvas(Integer.parseInt(getUserId()),
//                        Constants.RENDER_MODE_HIDDEN, mLocalSurfaceView), getRequestedOrientation());
//                mContentFl.addView(mLocalSurfaceView);
//                SurfaceHolder holder = mLocalSurfaceView.getHolder();
//                holder.addCallback(new SurfaceHolder.Callback() {
//                    @Override
//                    public void surfaceCreated(SurfaceHolder holder) {
//                        BeautyManager.get().onSurfaceCreated();
//                    }
//
//                    @Override
//                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//                    }
//
//                    @Override
//                    public void surfaceDestroyed(SurfaceHolder holder) {
//                        BeautyManager.get().onSurfaceDestroyed();
//                    }
//                });
//            }
//        }, 10);
//    }
//
//    /**
//     * 获取自己封面
//     */
//    private void getUserCoverImg() {
//        Map<String, String> paramMap = new HashMap<>();
//        paramMap.put("userId", getUserId());
//        OkHttpUtils.post().url(ChatApi.GET_USER_COVER_IMG)
//                .addParams("param", ParamUtil.getParam(paramMap))
//                .build().execute(new AjaxCallback<BaseResponse<CoverBean>>() {
//            @Override
//            public void onResponse(BaseResponse<CoverBean> response, int id) {
//                if (response != null && response.m_istatus == NetCode.SUCCESS) {
//                    mCoverBean = response.m_object;
//                    if (mCoverBean != null && !TextUtils.isEmpty(mCoverBean.t_cover_img)) {
//                        ImageLoadHelper.glideShowCornerImageWithUrl(BigHouseActivity.this,
//                                mCoverBean.t_cover_img, mCoverIv);
//                    }
//                }
//            }
//        });
//    }
//
//    @OnClick({R.id.close_iv, R.id.beauty_tv, R.id.camera_tv, R.id.start_live_tv, R.id.focus_tv,
//            R.id.live_close_iv, R.id.total_number_tv, R.id.head_iv, R.id.qq_iv, R.id.we_chat_iv,
//            R.id.we_circle_iv, R.id.qzone_iv, R.id.share_iv, R.id.camera_iv, R.id.input_tv,
//            R.id.gift_iv, R.id.deal_one_tv, R.id.connect_tv, R.id.living_layout_fl})
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.close_iv: {//开始直播页面,关闭页面
//                if (mTttRtcEngine != null) {
//                    mTttRtcEngine.leaveChannel();
//                    mTttRtcEngine = null;
//                }
//                finish();
//                break;
//            }
//            case R.id.deal_one_tv: {//帮助中心
//                Intent intent = new Intent(getApplicationContext(), HelpCenterActivity.class);
//                startActivity(intent);
//                break;
//            }
//            case R.id.connect_tv: {//联系客服
//                CodeUtil.jumpToQQ(BigHouseActivity.this);
//                break;
//            }
//            case R.id.beauty_tv: {//开始直播页面,设置美颜,开启美颜面板
//                mStartBottomLl.setVisibility(GONE);
//                beautyFrameLayout.setVisibility(VISIBLE);
//                break;
//            }
//            case R.id.camera_iv:
//            case R.id.camera_tv: {//开始直播页面,切换摄像头
//                if (mTttRtcEngine != null) {
//                    mTttRtcEngine.switchCamera();
//                }
//                break;
//            }
//            case R.id.start_live_tv: {//开始直播页面,开始直播
//                showLoadingDialog();
//                startLive();
//                break;
//            }
//            case R.id.focus_tv: {//关注
//                if (mActorId > 0) {
//                    saveFollow(mActorId);
//                }
//                break;
//            }
//            case R.id.live_close_iv: {//关闭直播
//                if (mFromType == Constant.FROM_ACTOR) {
//                    actorCloseLive();
//                } else {
//                    userExitRoom();
//                }
//                break;
//            }
//            case R.id.total_number_tv: {//贡献值 在线人数
//                showUserDialog();
//                break;
//            }
//            case R.id.head_iv: {//主播信息
//                if (mActorId > 0) {
//                    showUserInfoDialog(mActorId);
//                }
//                break;
//            }
//            case R.id.qq_iv: {//QQ分享
//                shareUrlToQQ(false);
//                break;
//            }
//            case R.id.we_chat_iv: {//微信分享
//                shareUrlToWeChat(false, false);
//                break;
//            }
//            case R.id.we_circle_iv: {//朋友圈
//                shareUrlToWeChat(true, false);
//                break;
//            }
//            case R.id.qzone_iv: {//QQ空间
//                shareUrlToQZone(false);
//                break;
//            }
//            case R.id.share_iv: {//开播后的分享
//                showShareDialog();
//                break;
//            }
//            case R.id.input_tv: {//输入文字
//                showInputDialog();
//                break;
//            }
//            case R.id.gift_iv: {//礼物
//                new GiftDialog(mContext, mActorId) {
//
//                    @Override
//                    public void sendOk(GiftBean giftBean, int gold) {
//                        //发送自定义消息
//                        CustomMessageBean bean = new CustomMessageBean();
//                        bean.type = "1";
//                        bean.gift_id = giftBean.t_gift_id;
//                        bean.gift_name = giftBean.t_gift_name;
//                        bean.gift_still_url = giftBean.t_gift_still_url;
//                        bean.gift_gif_url = giftBean.t_gift_gif_url;
//                        bean.gold_number = giftBean.t_gift_gold;
//                        bean.nickName = AppManager.getInstance().getUserInfo().t_nickName;
//                        bean.headUrl = AppManager.getInstance().getUserInfo().headUrl;
//
//                        String json = JSON.toJSONString(bean);
//                        if (!TextUtils.isEmpty(json)) {
//                            TIMCustomElem elem = new TIMCustomElem();
//                            elem.setData(json.getBytes());
//                            elem.setDesc(getString(R.string.get_a_gift));
//                            sendMessage(elem);
//                        }
//                    }
//
//                }.show();
//                break;
//            }
//            case R.id.living_layout_fl: {//清除
//                if (mMessageRv.getVisibility() == VISIBLE) {
//                    mMessageRv.setVisibility(View.INVISIBLE);
//                } else {
//                    mMessageRv.setVisibility(View.VISIBLE);
//                }
//                break;
//            }
//        }
//    }
//
//    /**
//     * 显示输入文字对话框
//     */
//    private void showInputDialog() {
//        final InputDialogFragment inputDialogFragment = new InputDialogFragment();
//        inputDialogFragment.setOnTextSendListener(new InputDialogFragment.OnTextSendListener() {
//            @Override
//            public void onTextSend(String text) {
//                if (!TextUtils.isEmpty(text)) {
//                    //发送文字
//                    LogUtil.i("发送文字: " + text);
//                    text = IMFilterHelper.getInstance().switchFilterWord(getApplicationContext(), text);
//                    TIMTextElem textElem = new TIMTextElem();
//                    textElem.setText(text);
//                    sendMessage(textElem);
//                    inputDialogFragment.dismiss();
//                } else {
//                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_text_message);
//                }
//            }
//        });
//        inputDialogFragment.show(getSupportFragmentManager(), "tag");
//    }
//
//    /**
//     * 用户退出房间
//     */
//    private void userExitRoom() {
//        Map<String, String> paramMap = new HashMap<>();
//        paramMap.put("userId", getUserId());
//        OkHttpUtils.post().url(ChatApi.USER_QUIT_BIG_ROOM)
//                .addParams("param", ParamUtil.getParam(paramMap))
//                .build().execute(new AjaxCallback<BaseResponse>() {
//            @Override
//            public void onResponse(BaseResponse response, int id) {
//                if (response != null && response.m_istatus == NetCode.SUCCESS) {
//                    if (mTttRtcEngine != null) {
//                        mTttRtcEngine.leaveChannel();
//                        mTttRtcEngine = null;
//                    }
//                    // 离开聊天室
//                    TIMGroupManager.getInstance().quitGroup(String.valueOf(mChatRoomId), new TIMCallBack() {
//                        @Override
//                        public void onError(int i, String s) {
//                            LogUtil.i("退出群失败: code " + i + " desc: " + s);
//                        }
//
//                        @Override
//                        public void onSuccess() {
//                            LogUtil.i("退出群成功");
//                        }
//                    });
//                    finish();
//                }
//            }
//        });
//    }
//
//    /**
//     * 主播关闭或者暂停直播
//     */
//    private void actorCloseLive() {
//        Map<String, String> paramMap = new HashMap<>();
//        paramMap.put("userId", getUserId());
//        OkHttpUtils.post().url(ChatApi.CLOSE_LIVE_TELECAST)
//                .addParams("param", ParamUtil.getParam(paramMap))
//                .build().execute(new AjaxCallback<BaseResponse>() {
//            @Override
//            public void onResponse(BaseResponse response, int id) {
//                if (response != null && response.m_istatus == NetCode.SUCCESS) {
//                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.have_finish_live);
//                    if (mTttRtcEngine != null) {
//                        mTttRtcEngine.leaveChannel();
//                        mTttRtcEngine = null;
//                    }
//                    finish();
//                }
//            }
//        });
//    }
//
//    /**
//     * 主播开启直播
//     */
//    private void startLive() {
//        Map<String, String> paramMap = new HashMap<>();
//        paramMap.put("userId", getUserId());
//        OkHttpUtils.post().url(ChatApi.OPEN_LIVE_TELECAST)
//                .addParams("param", ParamUtil.getParam(paramMap))
//                .build().execute(new AjaxCallback<BaseResponse<StartBean>>() {
//            @Override
//            public void onResponse(BaseResponse<StartBean> response, int id) {
//                if (response != null) {
//                    if (response.m_istatus == NetCode.SUCCESS) {
//                        StartBean startBean = response.m_object;
//                        if (startBean.roomId > 0 && startBean.chatRoomId > 0) {
//                            //聊天室ID
//                            mChatRoomId = startBean.chatRoomId;
//                            //加入聊天室
//                            joinChatRoom(String.valueOf(mChatRoomId));
//                            //推流地址
//                            String pushUrl = Constant.TTT_PUSH_ADDRESS + getUserId() + "/" + startBean.roomId;
//                            LogUtil.i("推流地址: " + pushUrl);
//                            PublisherConfiguration configuration = new PublisherConfiguration();
//                            configuration.setPushUrl(pushUrl);
//                            mTttRtcEngine.configPublisher(configuration);
//                            //主播加入房间
//                            mTttRtcEngine.joinChannel("", String.valueOf(startBean.roomId), Integer.parseInt(getUserId()));
//
//                            // 发送SEI
//                            VideoCompositingLayout layout = new VideoCompositingLayout();
//                            layout.regions = buildRemoteLayoutLocation();
//                            TTTRtcEngine.getInstance().setVideoCompositingLayout(layout);
//
//                            //开启直播成功后获取直播间信息
//                            getActorInfo(getUserId(), getUserId());
//                        } else {
//                            dismissLoadingDialog();
//                        }
//                    } else {
//                        String message = response.m_strMessage;
//                        if (!TextUtils.isEmpty(message)) {
//                            ToastUtil.INSTANCE.showToast(getApplicationContext(), message);
//                        } else {
//                            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.open_fail);
//                        }
//                        dismissLoadingDialog();
//                    }
//                } else {
//                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.open_fail);
//                    dismissLoadingDialog();
//                }
//            }
//
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                super.onError(call, e, id);
//                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.open_fail);
//                dismissLoadingDialog();
//            }
//
//        });
//    }
//
//    /**
//     * 获取主播 房间内相关信息
//     */
//    private void getActorInfo(final String userId, String anchorId) {
//        Map<String, String> paramMap = new HashMap<>();
//        paramMap.put("userId", userId);
//        paramMap.put("anchorId", anchorId);
//        OkHttpUtils.post().url(ChatApi.USER_MIX_BIG_ROOM)
//                .addParams("param", ParamUtil.getParam(paramMap))
//                .build().execute(new AjaxCallback<BaseResponse<BigRoomInfoBean<BigRoomUserBean>>>() {
//            @Override
//            public void onResponse(BaseResponse<BigRoomInfoBean<BigRoomUserBean>> response, int id) {
//                if (response != null && response.m_istatus == NetCode.SUCCESS) {
//                    if (isFinishing()) {
//                        return;
//                    }
//                    BigRoomInfoBean<BigRoomUserBean> roomInfoBean = response.m_object;
//                    if (roomInfoBean != null) {
//                        mTopInfoLl.setVisibility(VISIBLE);
//                        //头像
//                        mActorHeadImg = roomInfoBean.t_handImg;
//                        if (!TextUtils.isEmpty(mActorHeadImg)) {
//                            ImageLoadHelper.glideShowCircleImageWithUrl(BigHouseActivity.this,
//                                    mActorHeadImg, mHeadIv);
//                        } else {
//                            mHeadIv.setImageResource(R.drawable.default_head_img);
//                        }
//                        //昵称
//                        mNickTv.setText(roomInfoBean.t_nickName);
//                        //关注人数
//                        int number = roomInfoBean.followNumber;
//                        if (number > 0) {
//                            String content;
//                            if (number < 10000) {
//                                content = String.valueOf(number);
//                            } else {
//                                BigDecimal old = new BigDecimal(number);
//                                BigDecimal ten = new BigDecimal(10000);
//                                BigDecimal res = old.divide(ten, 1, RoundingMode.UP);
//                                content = res + mContext.getString(R.string.number_ten_thousand);
//                            }
//                            mFocusNumberTv.setText(content);
//                        }
//                        //是否关注   0.未关注 1.已关注
//                        if (roomInfoBean.isFollow == 0 && (AppManager.getInstance().getUserInfo().t_id + "").equals(userId)) {
//                            mFocusTv.setVisibility(VISIBLE);
//                        }
//                        //观看人数
//                        int viewNumber = roomInfoBean.viewer;
//                        String content;
//                        if (viewNumber < 10000) {
//                            content = String.valueOf(viewNumber);
//                        } else {
//                            BigDecimal old = new BigDecimal(viewNumber);
//                            BigDecimal ten = new BigDecimal(10000);
//                            BigDecimal res = old.divide(ten, 1, RoundingMode.UP);
//                            content = res + mContext.getString(R.string.number_ten_thousand);
//                        }
//                        mTotalNumberTv.setText(content);
//                        //用户
//                        List<BigRoomUserBean> userBeanList = roomInfoBean.devoteList;
//                        if (userBeanList != null && userBeanList.size() > 0) {
//                            mTopUserRecyclerAdapter.loadData(userBeanList);
//                        }
//                        //提示
//                        String warning = roomInfoBean.warning;
//                        if (!TextUtils.isEmpty(warning) && mTextRecyclerAdapter != null) {
//                            BigRoomTextBean textBean = new BigRoomTextBean();
//                            textBean.content = warning;
//                            textBean.type = 3;
//                            mTextRecyclerAdapter.addData(textBean);
//                        }
//                        //是否开播  0.未开播 1.已开播
//                        if (roomInfoBean.isDebut == 0) {
//                            mLiveEndTv.setText(getString(R.string.live_not_start));
//                            mLiveEndTv.setVisibility(VISIBLE);
//                        }
//                    }
//                }
//            }
//        });
//    }
//
//    /**
//     * 添加关注
//     */
//    private void saveFollow(int actorId) {
//        Map<String, String> paramMap = new HashMap<>();
//        paramMap.put("userId", getUserId());//关注人
//        paramMap.put("coverFollowUserId", String.valueOf(actorId));//	被关注人
//        OkHttpUtils.post().url(ChatApi.SAVE_FOLLOW)
//                .addParams("param", ParamUtil.getParam(paramMap))
//                .build().execute(new AjaxCallback<BaseResponse>() {
//            @Override
//            public void onResponse(BaseResponse response, int id) {
//                if (isFinishing()) {
//                    return;
//                }
//                if (response != null && response.m_istatus == NetCode.SUCCESS) {
//                    String message = response.m_strMessage;
//                    if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.success_str))) {
//                        ToastUtil.INSTANCE.showToast(getApplicationContext(), message);
//                        mFocusTv.setVisibility(GONE);
//                    }
//                } else {
//                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
//                }
//            }
//
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                super.onError(call, e, id);
//                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
//            }
//
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
//
//        SocketMessageManager.get().unsubscribe(subscription);
//
//        try {
//            BeautyManager.get().onDestroy();
//
//            //注销新消息回调
//            TIMManager.getInstance().removeMessageListener(this);
//
//            //释放礼物
//            LPAnimationManager.release();
//            if (mGifSv != null) {
//                mGifSv.pauseAnimation();
//                mGifSv = null;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        super.onDestroy();
//    }
//
//    //------------------贡献值 在线观众------------------------
//
//    /**
//     * 显示打赏礼物Dialog
//     */
//    private void showUserDialog() {
//        Bundle bundle = new Bundle();
//        bundle.putInt(Constant.ACTOR_ID, mActorId);
//        UserDialogFragment userDialog = new UserDialogFragment();
//        userDialog.setArguments(bundle);
//        userDialog.show(getSupportFragmentManager(), "tag");
//    }
//
//    /**
//     * 显示用户信息Dialog
//     */
//    private void showUserInfoDialog(int mActorId) {
//        Bundle bundle = new Bundle();
//        bundle.putInt(Constant.ACTOR_ID, mActorId);
//        UserInfoDialogFragment userDialog = new UserInfoDialogFragment();
//        userDialog.setArguments(bundle);
//        userDialog.show(getSupportFragmentManager(), "tag");
//    }
//
//    //-----------人数变化------------
//
//    protected void onBigRoomCountChange(int count, String sendUserName) {
//        LogUtil.i("大房间人数变化: " + count);
//        mTotalNumberTv.setText(String.valueOf(count));
//        if (!TextUtils.isEmpty(sendUserName)) {
//            BigRoomTextBean textBean = new BigRoomTextBean();
//            textBean.nickName = sendUserName;
//            textBean.type = 2;
//            if (mTextRecyclerAdapter != null) {
//                mTextRecyclerAdapter.addData(textBean);
//                if (mMessageRv != null) {
//                    mMessageRv.scrollToPosition(mTextRecyclerAdapter.getPosition());
//                }
//            }
//        }
//    }
//
//    //----------------------分享----------------------
//
//    /**
//     * 分享链接到QQ
//     */
//    private void shareUrlToQQ(boolean haveStart) {
//        if (mTencent != null) {
//            if (TextUtils.isEmpty(mShareUrl)) {
//                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.share_url_empty);
//                return;
//            }
//            String title;
//            String des;
//            String mineUrl;
//            if (haveStart) {//已经开播
//                String actorNick = mNickTv.getText().toString().trim();
//                title = getString(R.string.share_five) + actorNick + getString(R.string.share_six);
//                des = getString(R.string.share_four);
//                mineUrl = mActorHeadImg;
//            } else {//未开播
//                if (mCoverBean != null) {
//                    title = getString(R.string.hao_one) + mCoverBean.t_nickName + getString(R.string.share_one);
//                    des = getString(R.string.share_two) + getString(R.string.app_name) + getString(R.string.share_three);
//                    mineUrl = mCoverBean.t_cover_img;
//                } else {
//                    title = getString(R.string.hao_one) + getString(R.string.share_one);
//                    des = getString(R.string.share_two) + getString(R.string.app_name) + getString(R.string.share_three);
//                    mineUrl = getString(R.string.empty_text);
//                }
//            }
//
//            Bundle params = new Bundle();
//            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
//            params.putString(QQShare.SHARE_TO_QQ_TITLE, title);// 标题
//            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, des);// 摘要
//            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mShareUrl);// 内容地址
//            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mineUrl);// 图片地址
//            mTencent.shareToQQ(BigHouseActivity.this, params, new MyUIListener());
//        }
//    }
//
//    /**
//     * 分享链接到QQ空间
//     */
//    private void shareUrlToQZone(boolean haveStart) {
//        if (mTencent != null) {
//            if (TextUtils.isEmpty(mShareUrl)) {
//                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.share_url_empty);
//                return;
//            }
//            String title;
//            String des;
//            String mineUrl;
//            if (haveStart) {//已经开播
//                String actorNick = mNickTv.getText().toString().trim();
//                title = getString(R.string.share_five) + actorNick + getString(R.string.share_six);
//                des = getString(R.string.share_four);
//                mineUrl = mActorHeadImg;
//            } else {//未开播
//                if (mCoverBean != null) {
//                    title = getString(R.string.hao_one) + mCoverBean.t_nickName + getString(R.string.share_one);
//                    des = getString(R.string.share_two) + getString(R.string.app_name) + getString(R.string.share_three);
//                    mineUrl = mCoverBean.t_cover_img;
//                } else {
//                    title = getString(R.string.hao_one) + getString(R.string.share_one);
//                    des = getString(R.string.share_two) + getString(R.string.app_name) + getString(R.string.share_three);
//                    mineUrl = getString(R.string.empty_text);
//                }
//            }
//
//            Bundle params = new Bundle();
//            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
//            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);// 标题
//            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, des);// 摘要
//            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, mShareUrl);// 内容地址
//            ArrayList<String> images = new ArrayList<>();
//            images.add(mineUrl);
//            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, images);// 图片地址
//            mTencent.shareToQzone(BigHouseActivity.this, params, new MyUIListener());
//        }
//    }
//
//    class MyUIListener implements IUiListener {
//
//        @Override
//        public void onComplete(Object o) {
//            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.share_success);
//        }
//
//        @Override
//        public void onError(UiError uiError) {
//            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.share_fail);
//        }
//
//        @Override
//        public void onCancel() {
//            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.share_cancel);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Tencent.onActivityResultData(requestCode, resultCode, data, new MyUIListener());
//        if (requestCode == com.tencent.connect.common.Constants.REQUEST_API) {
//            if (resultCode == com.tencent.connect.common.Constants.REQUEST_QQ_SHARE
//                    || resultCode == com.tencent.connect.common.Constants.REQUEST_QZONE_SHARE
//                    || resultCode == com.tencent.connect.common.Constants.REQUEST_OLD_SHARE) {
//                Tencent.handleResultData(data, new MyUIListener());
//            }
//        }
//    }
//
//    /**
//     * 分享url到微信
//     */
//    private void shareUrlToWeChat(boolean isFriendCricle, boolean haveStart) {
//        if (mWxApi == null || !mWxApi.isWXAppInstalled()) {
//            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.not_install_we_chat);
//            return;
//        }
//        if (TextUtils.isEmpty(mShareUrl)) {
//            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.share_url_empty);
//            return;
//        }
//
//        String title;
//        String des;
//        if (haveStart) {//已经开播
//            String actorNick = mNickTv.getText().toString().trim();
//            title = getString(R.string.share_five) + actorNick + getString(R.string.share_six);
//            des = getString(R.string.share_four);
//        } else {//未开播
//            if (mCoverBean != null) {
//                title = getString(R.string.hao_one) + mCoverBean.t_nickName + getString(R.string.share_one);
//                des = getString(R.string.share_two) + getString(R.string.app_name) + getString(R.string.share_three);
//            } else {
//                title = getString(R.string.hao_one) + getString(R.string.share_one);
//                des = getString(R.string.share_two) + getString(R.string.app_name) + getString(R.string.share_three);
//            }
//        }
//
//        WXWebpageObject webpage = new WXWebpageObject();
//        webpage.webpageUrl = mShareUrl;
//        WXMediaMessage msg = new WXMediaMessage(webpage);
//        msg.title = title;
//        msg.description = des;
//        Bitmap thumb = BitmapUtil.setBackGroundColor(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
//        msg.setThumbImage(thumb);
//
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        //WXSceneTimeline朋友圈    WXSceneSession聊天界面
//        req.scene = isFriendCricle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;//聊天界面
//        req.message = msg;
//        req.transaction = String.valueOf(System.currentTimeMillis());
//        mWxApi.sendReq(req);
//        boolean res = mWxApi.sendReq(req);
//        if (res) {
//            AppManager.getInstance().setIsMainPageShareQun(false);
//        }
//    }
//
//    /**
//     * 显示分享dialog
//     */
//    private void showShareDialog() {
//        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
//        @SuppressLint("InflateParams")
//        View view = LayoutInflater.from(this).inflate(R.layout.dialog_share_layout, null);
//        setDialogView(view, mDialog);
//        mDialog.setContentView(view);
//        Point outSize = new Point();
//        getWindowManager().getDefaultDisplay().getSize(outSize);
//        Window window = mDialog.getWindow();
//        if (window != null) {
//            WindowManager.LayoutParams params = window.getAttributes();
//            params.width = outSize.x;
//            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
//            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
//        }
//        mDialog.setCanceledOnTouchOutside(true);
//        if (!isFinishing()) {
//            mDialog.show();
//        }
//    }
//
//    /**
//     * 设置头像选择dialog的view
//     */
//    private void setDialogView(View view, final Dialog mDialog) {
//        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
//        cancel_tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDialog.dismiss();
//            }
//        });
//
//        RecyclerView content_rv = view.findViewById(R.id.content_rv);
//        GridLayoutManager manager = new GridLayoutManager(getBaseContext(), 4);
//        content_rv.setLayoutManager(manager);
//        ShareRecyclerAdapter adapter = new ShareRecyclerAdapter(BigHouseActivity.this);
//        content_rv.setAdapter(adapter);
//        adapter.setOnItemClickListener(new ShareRecyclerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                if (position == 0) {//微信
//                    shareUrlToWeChat(false, true);
//                } else if (position == 1) {//朋友圈
//                    shareUrlToWeChat(true, true);
//                } else if (position == 2) {//QQ
//                    shareUrlToQQ(true);
//                } else if (position == 3) {//QQ空间
//                    shareUrlToQZone(true);
//                }
//                mDialog.dismiss();
//            }
//        });
//        List<ShareLayoutBean> beans = new ArrayList<>();
//        beans.add(new ShareLayoutBean("微信", R.drawable.share_wechat));
//        beans.add(new ShareLayoutBean("朋友圈", R.drawable.share_wechatfriend));
//        beans.add(new ShareLayoutBean("QQ", R.drawable.share_qq));
//        beans.add(new ShareLayoutBean("QQ空间", R.drawable.share_qzone));
//        adapter.loadData(beans);
//    }
//
//    //---------------------------------分享结束-------------------------
//
//    /**
//     * 显示礼物动画
//     */
//    private void showGiftAnim(CustomMessageBean customMessageBean) {
//        AnimMessage animMessage = new AnimMessage();
//        animMessage.userName = customMessageBean.nickName;
//        animMessage.headUrl = customMessageBean.headUrl;
//        animMessage.giftImgUrl = customMessageBean.gift_still_url;
//        if (customMessageBean.type.equals("1")) {// 0-约豆 1-礼物
//            animMessage.giftNum = 1;
//        } else {
//            animMessage.giftNum = customMessageBean.gold_number;
//        }
//        animMessage.giftName = customMessageBean.gift_name;
//        animMessage.giftType = customMessageBean.type;
//        LPAnimationManager.addAnimalMessage(animMessage);
//    }
//
//    /**
//     * 建造推流
//     */
//    private VideoCompositingLayout.Region[] buildRemoteLayoutLocation() {
//        List<VideoCompositingLayout.Region> tempList = new ArrayList<>();
//        //主播
//        if (Long.parseLong(getUserId()) > 0) {
//            VideoCompositingLayout.Region mRegion = new VideoCompositingLayout.Region();
//            mRegion.mUserID = Long.parseLong(getUserId());
//            //上下
//            //1:1
//            mRegion.x = 0;
//            mRegion.y = 0;
//            mRegion.width = 1;
//            mRegion.height = 1;
//            mRegion.zOrder = 0;
//            tempList.add(mRegion);
//        }
//        VideoCompositingLayout.Region[] mRegions = new VideoCompositingLayout.Region[tempList.size()];
//        for (int k = 0; k < tempList.size(); k++) {
//            VideoCompositingLayout.Region region = tempList.get(k);
//            mRegions[k] = region;
//        }
//        return mRegions;
//    }
//
//    protected void beenShutDown() {
//        if (mTttRtcEngine != null) {
//            mTttRtcEngine.leaveChannel();
//            mTttRtcEngine = null;
//            finish();
//        }
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
//    }
//
//    /**
//     * 获取分享链接
//     */
//    private void getShareUrl() {
//        ShareUrlHelper.getShareUrl(new OnCommonListener<ErWeiBean<PosterBean>>() {
//            @Override
//            public void execute(ErWeiBean<PosterBean> bean) {
//                if (!isFinishing() && bean != null) {
//                    mShareUrl = bean.shareUrl;
//                }
//            }
//        });
//    }
//
//
//    //------------------------消息部分----------------------
//
//    /**
//     * TIM 新消息
//     */
//    @Override
//    public boolean onNewMessages(List<TIMMessage> list) {
//        LogUtil.i("大房间聊天页面新消息来了");
//        //过滤消息
//        for (TIMMessage timMessage : list) {
//            TIMConversation conversation = timMessage.getConversation();
//            if (conversation != null && conversation.getType() == TIMConversationType.Group) {
//                addNewMessage(timMessage);
//            }
//        }
//        return true;
//    }
//
//    /**
//     * 列表添加消息
//     */
//    private void addNewMessage(TIMMessage msg) {
//        for (int i = 0; i < msg.getElementCount(); ++i) {
//            TIMElem elem = msg.getElement(i);
//            if (elem.getType() == TIMElemType.Text) {//处理文本消息
//                TIMTextElem textElem = (TIMTextElem) elem;
//                BigRoomTextBean textBean = new BigRoomTextBean();
//                textBean.content = textElem.getText();
//                textBean.type = 1;
//                textBean.nickName = msg.getSenderNickname();
//                mTextRecyclerAdapter.addData(textBean);
//                if (mMessageRv.getVisibility() != VISIBLE) {
//                    mMessageRv.setVisibility(VISIBLE);
//                }
//                mMessageRv.scrollToPosition(mTextRecyclerAdapter.getItemCount() - 1);
//            } else if (elem.getType() == TIMElemType.Custom) {//礼物
//                TIMCustomElem customElem = (TIMCustomElem) elem;
//                byte[] data = customElem.getData();
//                String json = new String(data);
//                parseCustomMessage(json);
//            }
//        }
//    }
//
//    /**
//     * 发送消息
//     */
//    private void sendMessage(TIMElem timElem) {
//        //构造一条消息并添加一个文本内容
//        TIMMessage msg = new TIMMessage();
//        if (msg.addElement(timElem) != 0) {
//            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.element_send_fail);
//            return;
//        }
//        if (mConversation != null) {
//            if (timElem != null) {
//                //发送消息
//                mConversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {
//                    @Override
//                    public void onError(int code, String desc) {//发送消息失败
//                        //错误码 code 和错误描述 desc，可用于定位请求失败原因
//                        LogUtil.i("TIM send message failed. code: " + code + " errmsg: " + desc);
//                        String content = getString(R.string.send_fail) + desc;
//                        ToastUtil.INSTANCE.showToast(getApplicationContext(), content);
//                        IMHelper.resLogin(code);
//                    }
//
//                    @Override
//                    public void onSuccess(TIMMessage msg) {//发送消息成功
//                        LogUtil.i("TIM SendMsg bitmap");
//                        addNewMessage(msg);
//                    }
//                });
//            } else {
//                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.element_send_fail);
//            }
//        } else {
//            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.tim_send_fail);
//        }
//    }
//
//
//    /**
//     * 解析自定义消息
//     */
//    private void parseCustomMessage(String json) {
//        try {
//            CustomMessageBean bean = CustomMessageBean.parseBean(json);
//            if (bean != null) {
//                if (bean.type.equals("0")) {//约豆
//                    bean.gift_name = getResources().getString(R.string.gold);
//                }
//                //显示大礼物
//                startGif(bean.gift_gif_url);
//                showGiftAnim(bean);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 开始GIF动画
//     */
//    private void startGif(String path) {
//        if (!TextUtils.isEmpty(path)) {
//            SVGAParser parser = new SVGAParser(this);
//            try {
//                URL url = new URL(path);
//                parser.parse(url, new SVGAParser.ParseCompletion() {
//                    @Override
//                    public void onComplete(@NotNull SVGAVideoEntity videoItem) {
//                        SVGADrawable drawable = new SVGADrawable(videoItem);
//                        mGifSv.setImageDrawable(drawable);
//                        mGifSv.startAnimation();
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//                });
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 订阅Socket Message
//     */
//    int[] Subscriptions = {
//            Mid.BEAN_SUSPEND,
//            Mid.BIG_ROOM_COUNT_CHANGE
//    };
//
//    OnCommonListener<SocketResponse> subscription = new OnCommonListener<SocketResponse>() {
//        @Override
//        public void execute(SocketResponse response) {
//            switch (response.mid) {
//                case Mid.BEAN_SUSPEND:
//                    beenShutDown();
//                    break;
//                case Mid.BIG_ROOM_COUNT_CHANGE:
//                    onBigRoomCountChange(response.userCount, response.sendUserName);
//                    break;
//            }
//        }
//    };

}
