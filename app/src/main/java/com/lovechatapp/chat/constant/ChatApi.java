package com.lovechatapp.chat.constant;

import com.lovechatapp.chat.BuildConfig;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：网络请求接口
 * 作者：
 * 创建时间：2018/6/25
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ChatApi {

    private static String SERVER = BuildConfig.hostAddress + "app/";
    private static String SHARE = BuildConfig.hostAddress + "share/";

    public static void setUrl(String url) {
        ChatApi.SERVER = url + "app/";
        ChatApi.SHARE = url + "share/";
    }

    /**
     * 发送短信验证码
     */
    public static String SEND_SMS_CODE() {
        return SERVER + "sendPhoneVerificationCode.html";
    }

    /**
     * 账号短信验证码登录
     */
    public static String LOGIN() {
        return SERVER + "login.html";
    }

    /**
     * 获取主页女神列表
     */
    public static String GET_HOME_PAGE_LIST() {
        return SERVER + "getHomePageList.html";
    }

    /**
     * 获取关注列表
     */
    public static String GET_FOLLOW_LIST() {
        return SERVER + "getFollowList.html";
    }

    /**
     * 选择性别
     */
    public static String UPDATE_USER_SEX() {
        return SERVER + "upateUserSex.html";
    }

    /**
     * 获取个人中心信息
     */
    public static String INDEX() {
        return SERVER + "index.html";
    }

    /**
     * 修改个人资料
     */
    public static String UPDATE_PERSON_DATA() {
        return SERVER + "updatePersonalData.html";
    }

    /**
     * 获取标签列表
     */
    public static String GET_LABEL_LIST() {
        return SERVER + "getLabelList.html";
    }

    /**
     * 获取个人资料
     */
    public static String GET_PERSONAL_DATA() {
        return SERVER + "getPersonalData.html";
    }

    /**
     * 微信登录获取access_token
     */
    public static String WX_GET_ACCESS_TOKEN() {
        return "https://api.weixin.qq.com/sns/oauth2/access_token";
    }

    /**
     * 微信登录获取user_info
     */
    public static String WX_GET_USER_INFO() {
        return "https://api.weixin.qq.com/sns/userinfo";
    }

    /**
     * 本app 微信登录
     */
    public static String USER_WEIXIN_LOGIN() {
        return SERVER + "userWeixinLogin.html";
    }

    /**
     * 本app QQ登录
     */
    public static String QQ_LOGIN() {
        return SERVER + "qqLogin.html";
    }

    /**
     * 获取主播资料
     */
    public static String GET_ACTOR_INFO() {
        return SERVER + "getUserData.html";
    }

    /**
     * 添加关注
     */
    public static String SAVE_FOLLOW() {
        return SERVER + "saveFollow.html";
    }

    /**
     * 取消关注
     */
    public static String DEL_FOLLOW() {
        return SERVER + "delFollow.html";
    }

    /**
     * 获取主播评论标签列表
     */
    public static String GET_EVALUATION_LIST() {
        return SERVER + "getEvaluationList.html";
    }

    /**
     * 获取主播视频或者照片
     * 1视频 0图片
     */
    public static String GET_DYNAMIC_LIST() {
        return SERVER + "getAlbumList.html";
    }

    /**
     * 获取充值列表
     */
    public static String GET_RECHARGE_DISCOUNT() {
        return SERVER + "getRechargeDiscount.html";
    }

    /**
     * 验证昵称是否重复
     */
    public static String GET_NICK_REPEAT() {
        return SERVER + "getNickRepeat.html";
    }

    /**
     * 获取提现比例
     */
    public static String GET_PUT_FORWARD_DISCOUNT() {
        return SERVER + "getPutforwardDiscount.html";
    }

    /**
     * 申请体现
     */
    public static String CONFIRM_PUT_FORWARD() {
        return SERVER + "confirmPutforward.html";
    }

    /**
     * 删除我的相册
     */
    public static String DEL_MY_PHOTO() {
        return SERVER + "delMyPhoto.html";
    }

    /**
     * 获取钱包消费或者提现明细
     */
    public static String GET_WALLET_DETAIL() {
        return SERVER + "getWalletDetail.html";
    }

    /**
     * 获取推广赚钱
     */
    public static String GET_SHARE_TOTAL() {
        return SERVER + "getShareTotal.html";
    }

    /**
     * 获取我的推广用户列表
     */
    public static String GET_SHARE_USER_LIST() {
        return SERVER + "getShareUserList.html";
    }

    /**
     * 获取消息列表
     */
    public static String GET_MESSAGE_LIST() {
        return SERVER + "getMessageList.html";
    }

    /**
     * 获取钱包余额
     */
    public static String GET_USER_BALANCE() {
        return SERVER + "getQueryUserBalance.html";
    }

    /**
     * 投诉用户
     */
    public static String SAVE_COMPLAINT() {
        return SERVER + "saveComplaint.html";
    }

    /**
     * 获取视频上传签名
     */
    public static String GET_VIDEO_SIGN() {
        return SERVER + "getVoideSign.html";
    }

    /**
     * 新增相册数据
     */
    public static String ADD_MY_PHOTO_ALBUM() {
        return SERVER + "addMyPhotoAlbum.html";
    }

    /**
     * 提交实名认证资料
     * t_type 0：主播认证  1：视频认证  3：身份证认证
     */
    public static String SUBMIT_VERIFY_DATA() {
        return SERVER + "submitIdentificationData.html";
    }

    /**
     * 获取实名认证状态
     */
    public static String GET_VERIFY_STATUS() {
        return SERVER + "getUserIsIdentification.html";
    }

    /**
     * 获取主播收费设置
     */
    public static String GET_ACTOR_CHARGE_SETUP() {
        return SERVER + "getAnchorChargeSetup.html";
    }

    /**
     * 修改主播收费设置
     */
    public static String UPDATE_CHARGE_SET() {
        return SERVER + "updateAnchorChargeSetup.html";
    }

    /**
     * 获取主播播放页数据
     */
    public static String GET_ACTOR_PLAY_PAGE() {
        return SERVER + "getAnchorPlayPage.html";
    }

    /**
     * 添加意见反馈
     */
    public static String ADD_FEED_BACK() {
        return SERVER + "addFeedback.html";
    }

    /**
     * 视频聊天id
     */
    public static String GET_VIDEO_CHAT_SIGN() {
        return SERVER + "getVideoChatAutograph.html";
    }

    /**
     * 视频签名
     */
    public static String getAgoraRoomSign() {
        return SERVER + "getAgoraRoomSign.html";
    }

    /**
     * 用户对主播发起聊天
     */
    public static String LAUNCH_VIDEO_CHAT() {
        return SERVER + "launchVideoChat.html";
    }

    /**
     * 用户或者主播挂断链接
     */
    public static String BREAK_LINK() {
        return SERVER + "breakLink.html";
    }

    /**
     * 查看微信号码
     */
    public static String SEE_WEI_XIN_CONSUME() {
        return SERVER + "seeWeiXinConsume.html";
    }

    /**
     * 查看手机号
     */
    public static String SEE_PHONE_CONSUME() {
        return SERVER + "seePhoneConsume.html";
    }

    /**
     * 查看QQ号
     */
    public static String SEE_QQ_CONSUME() {
        return SERVER + "seeQQConsume.html";
    }

    /**
     * 修改手机号码
     */
    public static String UPDATE_PHONE() {
        return SERVER + "updatePhone.html";
    }

    /**
     * 非VIP查看私密照片
     */
    public static String SEE_IMAGE_CONSUME() {
        return SERVER + "seeImgConsume.html";
    }

    /**
     * 非VIP查看私密视频
     */
    public static String SEE_VIDEO_CONSUME() {
        return SERVER + "seeVideoConsume.html";
    }

    /**
     * 非VIP查看私密视频
     */
    public static String VIP_SEE_DATA() {
        return SERVER + "vipSeeData.html";
    }

    /**
     * 开始计时
     */
    public static String VIDEO_CHAT_BIGIN_TIMING() {
        return SERVER + "videoCharBeginTiming.html";
    }

    /**
     * 设置勿扰
     */
    public static String UPDATE_USER_DISTURB() {
        return SERVER + "updateUserDisturb.html";
    }

    /**
     * 获取短视频列表
     */
    public static String GET_VIDEO_LIST() {
        return SERVER + "getVideoList.html";
    }

    /**
     * 非VIP发送文本消息
     */
    public static String SEND_TEXT_CONSUME() {
        return SERVER + "sendTextConsume.html";
    }

    /**
     * 发红包
     */
    public static String SEND_RED_ENVELOPE() {
        return SERVER + "sendRedEnvelope.html";
    }

    /**
     * 获取礼物列表
     */
    public static String GET_GIFT_LIST() {
        return SERVER + "getGiftList.html";
    }

    /**
     * 用户赠送礼物
     */
    public static String USER_GIVE_GIFT() {
        return SERVER + "userGiveGift.html";
    }

    /**
     * 获取用户收未拆开红包统计
     */
    public static String GET_RED_PACKET_COUNT() {
        return SERVER + "getRedPacketCount.html";
    }

    /**
     * 用户拆开红包
     */
    public static String RECEIVE_RED_PACKET() {
        return SERVER + "receiveRedPacket.html";
    }

    /**
     * 获取VIP套餐列表
     */
    public static String GET_VIP_SET_MEAL_LIST() {
        return SERVER + "getVIPSetMealList.html";
    }

    /**
     * VIP支付
     */
    public static String VIP_STORE_VALUE() {
        return SERVER + "vipStoreValue.html";
    }

    /**
     * 约豆充值
     */
    public static String GOLD_STORE_VALUE() {
        return SERVER + "goldStoreValue.html";
    }

    /**
     * 评价主播
     */
    public static String SAVE_COMMENT() {
        return SERVER + "saveComment.html";
    }

    /**
     * 获取用户可提现约豆
     */
    public static String GET_USEABLE_GOLD() {
        return SERVER + "getUsableGold.html";
    }

    /**
     * 更新提现资料
     */
    public static String MODIFY_PUT_FORWARD_DATA() {
        return SERVER + "modifyPutForwardData.html";
    }

    /**
     * 获取欢迎消息
     */
    public static String GET_PUSH_MSG() {
        return SERVER + "getPushMsg.html";
    }

    /**
     * 用户登出
     */
    public static String LOGOUT() {
        return SERVER + "logout.html";
    }

    /**
     * 获取最新版本
     */
    public static String GET_NEW_VERSION() {
        return SERVER + "getNewVersion.html";
    }

    /**
     * 主页搜索
     */
    public static String GET_SEARCH_LIST() {
        return SERVER + "getSearchList.html";
    }

    /**
     * 加载用户列表
     */
    public static String GET_ONLINE_USER_LIST() {
        return SERVER + "getOnLineUserList.html";
    }

    /**
     * 主播对用户发起聊天
     */
    public static String ACTOR_LAUNCH_VIDEO_CHAT() {
        return SERVER + "anchorLaunchVideoChat.html";
    }

    /**
     * 获取用户是否新用户
     */
    public static String GET_USER_NEW() {
        return SERVER + "getUserNew.html";
    }

    /**
     * 1.1版更新用户登陆时间
     */
    public static String UP_LOGIN_TIME() {
        return SERVER + "upLoginTime.html";
    }

    /**
     * 获取banner列表
     */
    public static String GET_BANNER_LIST() {
        return SERVER + "getBannerList.html";
    }

    /**
     * t_banner_type 安卓:0   IOS:1
     */
    public static String getAllBannerList() {
        return SERVER + "getAllBannerList.html";
    }

    /**
     * 1.2版 统计公会主播数和贡献值
     */
    public static String GET_GUILD_COUNT() {
        return SERVER + "getGuildCount.html";
    }

    /**
     * 1.2版 获取公会主播贡献列表
     */
    public static String GET_CONTRIBUTION_LIST() {
        return SERVER + "getContributionList.html";
    }

    /**
     * 1.2版 拉取是否邀请主播加入公会
     */
    public static String GET_ANCHOR_ADD_GUILD() {
        return SERVER + "getAnchorAddGuild.html";
    }

    /**
     * 1.2版 申请公会
     */
    public static String APPLY_GUILD() {
        return SERVER + "applyGuild.html";
    }

    /**
     * 1.2版 主播确认是否加入公会
     */
    public static String IS_APPLY_GUILD() {
        return SERVER + "isApplyGuild.html";
    }

    /**
     * 1.2版 获取公会主播贡献明细统计
     */
    public static String GET_ANTHOR_TOTAL() {
        return SERVER + "getAnthorTotal.html";
    }

    /**
     * 1.2版 公会主播贡献明细列表
     */
    public static String GET_CONTRIBUTION_DETAIL() {
        return SERVER + "getContributionDetail.html";
    }

    /**
     * 1.2版 获取礼物赠送列表
     */
    public static String GET_REWARD_LIST() {
        return SERVER + "getRewardList.html";
    }

    /**
     * 1.2版 获取提现方式列表
     */
    public static String GET_TAKE_OUT_MODE() {
        return SERVER + "getTakeOutMode.html";
    }

    /**
     * 1.2版 申请CPS联盟
     */
    public static String ADD_CPS_MS() {
        return SERVER + "addCpsMs.html";
    }

    /**
     * 1.2版 CPS联盟统计
     */
    public static String GET_TOTAL_DATEIL() {
        return SERVER + "getTotalDateil.html";
    }

    /**
     * 1.2版 CPS获取用户贡献列表
     */
    public static String GET_MY_CONTRIBUTION_LIST() {
        return SERVER + "getMyContributionList.html";
    }

    /**
     * 1.3版 获取魅力榜
     */
    public static String GET_GLAMOUR_LIST() {
        return SERVER + "getGlamourList.html";
    }

    /**
     * 1.3版 获取消费榜
     */
    public static String GET_CONSUME_LIST() {
        return SERVER + "getConsumeList.html";
    }

    /**
     * 1.3版 获取豪礼榜
     */
    public static String GET_COURTESY_LIST() {
        return SERVER + "getCourtesyList.html";
    }

    /**
     * 邀请榜
     */
    public static String getSpreadUser() {
        return SERVER + "getSpreadUser.html";
    }

    /**
     * 守护榜
     */
    public static String getUserGuardList() {
        return SERVER + "getUserGuardList.html";
    }

    /**
     * 1.3版 主播收益明细
     */
    public static String GET_ANCHOR_PROFIT_DETAIL() {
        return SERVER + "getAnchorProfitDetail.html";
    }

    /**
     * 1.3.1 新增动态查看次数
     */
    public static String ADD_QUERY_DYNAMIC_COUNT() {
        return SERVER + "addQueryDynamicCount.html";
    }

    /**
     * 1.3.1 用户点赞
     */
    public static String ADD_LAUD() {
        return SERVER + "addLaud.html";
    }

    /**
     * 1.3.1 用户取消点赞
     */
    public static String CANCEL_LAUD() {
        return SERVER + "cancelLaud.html";
    }

    /**
     * 1.3.1 获取推荐主播
     */
    public static String GET_HOME_NOMINATE_LIST() {
        return SERVER + "getHomeNominateList.html";
    }

    /**
     * 1.3.1 获取试看主播列表
     */
    public static String GET_TRY_COMPERE_LIST() {
        return SERVER + "getTryCompereList.html";
    }

    /**
     * 1.3.1 获取新人主播
     */
    public static String GET_NEW_COMPERE_LIST() {
        return SERVER + "getNewCompereList.html";
    }

    /**
     * 1.3.2 版 获推广奖励规则
     */
    public static String GET_SPREAD_AWARD() {
        return SERVER + "getSpreadAward.html";
    }

    /**
     * 1.3.2 版 获取推荐贡献排行榜
     */
    public static String GET_SPREAD_BONUSES() {
        return SERVER + "getSpreadBonuses.html";
    }

    /**
     * 1.3.2 版 获取推荐用户排行榜
     */
    public static String GET_SPREAD_USER() {
        return SERVER + "getSpreadUser.html";
    }

    /**
     * 添加分享次数
     */
    public static String ADD_SHARE_COUNT() {
        return SHARE + "addShareCount.html";
    }

    /**
     * 1.1小夫妻分享
     */
    public static String JUMP_SPOUSE() {
        return SHARE + "jumpSpouse.html?userId=";
    }

    /**
     * 1.1主播招募分享
     */
    public static String JUMP_ANCHORS() {
        return SHARE + "jumpAnchors.html?userId=";
    }

    /**
     * 1.1版 开红包游戏分享
     */
    public static String JUMP_GAME() {
        return SHARE + "jumpGame.html?userId=";
    }

    //-----------------------其他相关------------

    /**
     * CPS 分享 好友邀请你聊天链接
     */
    public static String CHAT_OFFICAL_URL() {
        return "http://www.1-liao.com";
    }

    //-------------------1.4版-------------

    /**
     * 1.4版 获取主播亲密排行和礼物排行
     */
    public static String GET_INITMATE_AND_GIFT() {
        return SERVER + "getIntimateAndGift.html";
    }

    /**
     * 1.4版 获取主播亲密排行和礼物排行
     */
    public static String GET_ANTHOR_INTIMATE_LIST() {
        return SERVER + "getAnthorIntimateList.html";
    }

    /**
     * 1.4版 获取主播亲密排行和礼物排行
     */
    public static String GET_ANTHOR_GIFT_LIST() {
        return SERVER + "getAnthorGiftList.html";
    }

    /**
     * 1.4版 钱包头部统计
     */
    public static String GET_PROFIT_AND_PAY_TOTAL() {
        return SERVER + "getProfitAndPayTotal.html";
    }

    /**
     * 1.4版 获取钱包明细
     */
    public static String GET_USER_GOLD_DETAILS() {
        return SERVER + "getUserGoldDetails.html";
    }

    /**
     * 1.4 版 获取我的相册列表
     */
    public static String GET_MY_ANNUAL_ALBUM() {
        return SERVER + "getMyAnnualAlbum.html";
    }

    /**
     * 1.4 版 获取指定用户是否关注
     */
    public static String GET_SPECIFY_USER_FOLLOW() {
        return SERVER + "getSpecifyUserFollow.html";
    }

    /**
     * 1.4 版 获取通话记录
     */
    public static String GET_CALL_LOG() {
        return SERVER + "getCallLog.html";
    }

    /**
     * 1.4版  获取收费设置
     */
    public static String GET_ANTHOR_CHARGE_LIST() {
        return SERVER + "getAnthorChargeList.html";
    }

    /**
     * 1.4版  获取认证微信号
     */
    public static String GET_IDENTIFICATION_WEI_XIN() {
        return SERVER + "getIdentificationWeiXin.html";
    }

    /**
     * 1.4版  获取私密视频收费设置
     */
    public static String GET_PRIVATE_VIDEO_MONEY() {
        return SERVER + "getPrivateVideoMoney.html";
    }

    /**
     * 1.4版  获取私密视频收费设置
     */
    public static String GET_PRIVATE_PHOTO_MONEY() {
        return SERVER + "getPrivatePhotoMoney.html";
    }

    /**
     * 1.5版  上传坐标
     */
    public static String UPLOAD_COORDINATE() {
        return SERVER + "uploadCoordinate.html";
    }

    /**
     * 1.5版  获取附近用户列表
     */
    public static String GET_ANTHOR_DISTANCE_LIST() {
        return SERVER + "getAnthorDistanceList.html";
    }

    /**
     * 1.5版  获取附近的用户列表
     */
    public static String GET_NEAR_BY_LIST() {
        return SERVER + "getNearbyList.html";
    }

    /**
     * 1.5版 获取用户信息
     */
    public static String GET_USER_DETA() {
        return SERVER + "getUserDeta.html";
    }

    /**
     * 用户编号挂断链接
     */
    public static String USER_HANG_UP_LINK() {
        return SERVER + "userHangupLink.html";
    }

    /**
     * 获取未读消息数
     */
    public static String GET_UN_READ_MESSAGE() {
        return SERVER + "getUnreadMessage.html";
    }

    /**
     * 设置为已读
     */
    public static String SET_UP_READ() {
        return SERVER + "setupRead.html";
    }

    /**
     * 设置为已读
     */
    public static String UNITE_ID_CARD() {
        return SERVER + "uniteIdCard.html";
    }

    /**
     * 1.6版 获取动态列表
     */
    public static String GET_USER_DYNAMIC_LIST() {
        return SERVER + "getUserDynamicList.html";
    }

    /**
     * 1.6版 发布动态
     */
    public static String RELEASE_DYNAMIC() {
        return SERVER + "releaseDynamic.html";
    }

    /**
     * 1.6版 添加点赞
     */
    public static String GIVE_THE_THUMB_UP() {
        return SERVER + "giveTheThumbsUp.html";
    }

    /**
     * 1.6版 获取评论列表
     */
    public static String GET_COMMENT_LIST() {
        return SERVER + "getCommentList.html";
    }

    /**
     * 1.6版 添加评论
     */
    public static String DISCUSS_DYNAMIC() {
        return SERVER + "discussDynamic.html";
    }

    /**
     * 1.6版 删除动态评论
     */
    public static String DEL_COMMENT() {
        return SERVER + "delComment.html";
    }

    /**
     * 查看付费动态
     */
    public static String DYNAMIC_PAY() {
        return SERVER + "dynamicPay.html";
    }

    /**
     * 1.6版 获取最新评论列表
     */
    public static String GET_USER_NEW_COMMENT() {
        return SERVER + "getUserNewComment.html";
    }

    /**
     * 1.6版 获取通知记录数或者新动态
     */
    public static String GET_USER_DYNAMIC_NOTICE() {
        return SERVER + "getUserDynamicNotice.html";
    }

    /**
     * 1.6版 获取图形验证码流
     */
    public static String GET_VERIFY() {
        return SERVER + "getVerify.html?phone=";
    }

    /**
     * 1.6版 获取通知记录数或者新动态
     */
    public static String GET_VERIFY_CODE_IS_CORRECT() {
        return SERVER + "getVerifyCodeIsCorrect.html";
    }

    /**
     * 1.6版 获取自己的动态
     */
    public static String GET_OWN_DYNAMIC_LIST() {
        return SERVER + "getOwnDynamicList.html";
    }

    /**
     * 1.6版 删除动态
     */
    public static String DEL_DYNAMIC() {
        return SERVER + "delDynamic.html";
    }

    /**
     * 1.7版 主播获取速配房间
     */
    public static String GET_SPEED_DATING_ROOM() {
        return SERVER + "getSpeedDatingRoom.html";
    }

    /**
     * 1.7版 开启速配
     */
    public static String OPEN_SPEED_DATING() {
        return SERVER + "openSpeedDating.html";
    }

    /**
     * 1.7版 主播结束速配
     */
    public static String END_SPEED_DATING() {
        return SERVER + "appEndSpeedDating.html";
    }

    /**
     * 1.7版 用户拉取速配主播
     */
    public static String GET_SPEED_DATING_ANCHOR() {
        return SERVER + "getSpeedDatingAnchor.html";
    }

    /**
     * 获取客服QQ
     */
    public static String GET_SERVICE_QQ() {
        return SERVER + "getServiceQQ.html";
    }

    /**
     * 1.7版 获取主播速配时长
     */
    public static String GET_USER_SPEED_TIME() {
        return SERVER + "getUserSpeedTime.html";
    }

    /**
     * 获取APP外部下载地址
     */
    public static String GET_DOLOAD_URL() {
        return SHARE + "getDoloadUrl.html";
    }

    /**
     * 1.7版 获取帮助列表
     */
    public static String GET_HELP_CONTRE() {
        return SERVER + "getHelpContre.html";
    }

    /**
     * 1.6版 获取主播动态列表
     */
    public static String GET_PRIVATE_DYNAMIC_LIST() {
        return SERVER + "getPrivateDynamicList.html";
    }

    /**
     * 用户注册账号
     */
    public static String REGISTER() {
        return SERVER + "register.html";
    }

    /**
     * 忘记密码重置
     */
    public static String UP_PASSWORD() {
        return SERVER + "upPassword.html";
    }

    /**
     * 账号密码登陆
     */
    public static String USER_LOGIN() {
        return SERVER + "userLogin.html";
    }

    //-----------------------------大房间----------------------------

    /**
     * 获取大房间列表
     */
    public static String GET_BIG_ROOM_LIST() {
        return SERVER + "getBigRoomList.html";
    }

    /**
     * 获取主播封面
     */
    public static String GET_USER_COVER_IMG() {
        return SERVER + "getUserCoverImg.html";
    }

    /**
     * 主播开启直播
     */
    public static String OPEN_LIVE_TELECAST() {
        return SERVER + "openLiveTelecast.html";
    }

    /**
     * 用户加入直播间
     */
    public static String USER_MIX_BIG_ROOM() {
        return SERVER + "userMixBigRoom.html";
    }

    /**
     * 主播关闭或者暂停直播
     */
    public static String CLOSE_LIVE_TELECAST() {
        return SERVER + "closeLiveTelecast.html";
    }

    /**
     * 获取直播间的贡献榜
     */
    public static String GET_BIG_CONTRIBUTION_LIST() {
        return SERVER + "getBigContributionList.html";
    }

    /**
     * 获取直播间的在线观众
     */
    public static String GET_ROOM_USER_LIST() {
        return SERVER + "getRoomUserList.html";
    }

    /**
     * 点击头像获取信息
     */
    public static String GET_USER_INDEX_DATA() {
        return SERVER + "getUserIndexData.html";
    }

    /**
     * 用户退出直播间
     */
    public static String USER_QUIT_BIG_ROOM() {
        return SERVER + "userQuitBigRoom.html";
    }

    /**
     * 获取分享链接
     */
    public static String GET_SPREAD_URL() {
        return SHARE + "getSpreadUrl.html";
    }

    /**
     * 封面上传
     */
    public static String REPLACE_COVER_IMG() {
        return SERVER + "replaceCoverImg.html";
    }

    /**
     * 删除封面
     */
    public static String DEL_COVER_IMG() {
        return SERVER + "delCoverImg.html";
    }

    /**
     * 设为主封面
     */
    public static String SET_MAIN_COVER_IMG() {
        return SERVER + "setMainCoverImg.html";
    }

    /**
     * 获取支付配置
     */
    public static String GET_PAY_DEPLOY_LIST() {
        return SERVER + "getPayDeployList.html";
    }

    /**
     * 获取真实IP
     */
    public static String GET_REAL_IP() {
        return "http://pv.sohu.com/cityjson?ie=utf-8";
    }

    /**
     * 获取广告
     */
    public static String getAdTable() {
        return SERVER + "getAdTable.html";
    }

    /**
     * 获取腾讯IM的userSig
     */
//    public static  String GET_IM_USER_SIG(){ return SERVER + "getUserImSig.html";}
    public static String GET_IM_USER_SIG() {
        return SERVER + "getNewImUserSign.html";
    }

    /**
     * 查询被浏览记录
     */
    public static String getCoverBrowseList() {
        return SERVER + "getCoverBrowseList.html";
    }

    /**
     * 视频语音私聊开关设置
     */
    public static String setUpChatSwitch() {
        return SERVER + "setUpChatSwitch.html";
    }

    /**
     * 获取用户头像昵称
     */
    public static String getUserInfoById() {
        return SERVER + "getUserInfoById.html";
    }

    /**
     * 主播端匹配用户
     */
    public static String getVIPUserInfo() {
        return SERVER + "getVIPUserInfo.html";
    }

    /**
     * 用户端匹配主播
     */
    public static String getOnlineAnoInfo() {
        return SERVER + "getOnlineAnoInfo.html";
    }

    /**
     * 更新选聊主播
     */
    public static String getSelectCharAnother() {
        return SERVER + "getSelectCharAnother.html";
    }

    /**
     * 更新选聊主播
     */
    public static String GET_PHONE_SMS_STATUS() {
        return SERVER + "getPhoneSmsStatus.html";
    }

    /**
     * 用户一键随机获取主播的封面图 5张
     */
    public static String getAnoCoverImg() {
        return SERVER + "getAnoCoverImg.html";
    }

    /**
     * 添加黑名单
     */
    public static String addBlackUser() {
        return SERVER + "addBlackUser.html";
    }

    /**
     * 黑名单列表
     */
    public static String getBlackUserList() {
        return SERVER + "getBlackUserList.html";
    }

    /**
     * 取消黑名单
     */
    public static String delBlackUser() {
        return SERVER + "delBlackUser.html";
    }

    /**
     * 获取IM过滤词
     */
    public static String GET_IM_FILTER() {
        return SERVER + "getImFilter.html";
    }

    /**
     * 是否录音
     */
    public static String getSounRecordingSwitch() {
        return SERVER + "getSounRecordingSwitch.html";
    }

    /**
     * 上传录音文件
     * userId 自己的ID
     * cover_userId 对方的ID
     * soundUrl 录音地址
     */
    public static String saveSounRecording() {
        return SERVER + "saveSounRecording.html";
    }

    /**
     * 我要置顶
     */
    public static String OPERATION_TOPPING() {
        return SERVER + "setOperatingTopping.html";
    }

    /**
     * 我要置顶男用户
     */
    public static String OPERATION_TOPPING_MAN() {
        return SERVER + "setUserTopping.html";
    }

    /**
     * 七牛token
     */
    public static String getQiNiuKey() {
        return SERVER + "getQiNiuKey.html";
    }

    /**
     * 上传涉黄图片
     */
    public static String addVideoScreenshotInfo() {
        return SERVER + "addVideoScreenshotInfo.html";
    }

    /**
     * 是否上传图片鉴黄
     */
    public static String getVideoScreenshotStatus() {
        return SERVER + "getVideoScreenshotStatus.html";
    }

    /**
     * 获取通话状态
     */
    public static String getVideoStatus() {
        return SERVER + "getVideoStatus.html";
    }

    /**
     * 查询守护
     */
    public static String getGuard() {
        return SERVER + "getGuard.html";
    }

    /**
     * 获取私信条数
     */
    public static String privateLetterNumber() {
        return SERVER + "privateLetterNumber.html";
    }

    /**
     * 打招呼
     */
    public static String greet() {
        return SERVER + "greet.html";
    }

    /**
     * 获取关注列表
     */
    public static String GET_COVER_FOLLOW() {
        return SERVER + "getCoverFollowList.html";
    }

    /**
     * svipSwitch
     */
    public static String svipSwitch() {
        return SERVER + "svipSwitch.html";
    }

    /**
     * 设置封面视频
     */
    public static String setFirstAlbum() {
        return SERVER + "setFirstAlbum.html";
    }

    /**
     * 获取vip状态
     */
    public static String getUserVipInfo() {
        return SERVER + "getUserVipInfo.html";
    }

    /**
     * 上传涉黄图片
     */
    public static String uploadAPPFile() {
        return SHARE + "uploadAPPFile.html";
    }

    /**
     * 判断用户是否有权限
     * 返回参数
     * houseSwitch:0 没有权限 1 有权限
     * mansionId: 默认返回0
     */
    public static String getMansionHouseSwitch() {
        return SERVER + "getMansionHouseSwitch.html";
    }

    /**
     * 用户查看自己府邸的详情信息
     * 参数:
     * t_mansion_house_id 房间号ID
     */
    public static String getMansionHouseInfo() {
        return SERVER + "getMansionHouseInfo.html";
    }

    /**
     * 用户查看自己府邸的详情信息
     * 参数:
     * t_mansion_house_id 房间号ID
     */
    public static String getMansionHouseFollowList() {
        return SERVER + "getMansionHouseFollowList.html";
    }

    /**
     * 用户邀请主播到自己府邸
     * 请求参数:
     * t_mansion_house_id
     * anchorId
     */
    public static String inviteMansionHouseAnchor() {
        return SERVER + "inviteMansionHouseAnchor.html";
    }

    /**
     * t_mansion_house_id 府邸Id
     * anchorId 主播Id
     * agreeType: 1:同意 2:拒绝
     */
    public static String agreeMansionHouseInvite() {
        return SERVER + "agreeMansionHouseInvite.html";
    }

    /**
     * 主播获取自己的被邀请列表
     */
    public static String getMansionHouseInviteList() {
        return SERVER + "getMansionHouseInviteList.html";
    }

    /**
     * 主播查看自己所在府邸列表
     */
    public static String getMansionHouseList() {
        return SERVER + "getMansionHouseList.html";
    }

    /**
     * 主播退出府邸
     */
    public static String anchorOutOfMansionHouse() {
        return SERVER + "anchorOutOfMansionHouse.html";
    }

    /**
     * 删除府邸中的主播
     */
    public static String delMansionHouseAnchor() {
        return SERVER + "delMansionHouseAnchor.html";
    }

    /**
     * 设置主播封面
     */
    public static String setMansionHouseCoverImg() {
        return SERVER + "setMansionHouseCoverImg.html";
    }

    /**
     * 府邸用户创建房间
     * t_mansion_house_id 府邸ID
     * roomName 房间名称
     * chatType 1:视频 2:语音
     */
    public static String addMansionHouseRoom() {
        return SERVER + "addMansionHouseRoom.html";
    }

    /**
     * 府邸房间计时
     * chatType  1:视频 2:语音
     * mansionRoomId 当前多人房间号Id
     * roomId 用户与主播的单独房间号
     */
    public static String videoMansionChatBeginTiming() {
        return SERVER + "videoMansionChatBeginTiming.html";
    }

    /**
     * 获取当前1V2房间信息
     * 请求参数
     * mansionRoomId 多人房间Id
     */
    public static String getMansionHouseVideoInfo() {
        return SERVER + "getMansionHouseVideoInfo.html";
    }

    /**
     * 府邸房间用户呼叫主播
     * chatType 1:视频 2:语音
     * mansionRoomId 当前多人房间号Id
     * coverLinkUserId 主播ID
     */
    public static String launchMansionVideoChat() {
        return SERVER + "launchMansionVideoChat.html";
    }

    /**
     * 1V2房间 获取府邸主播列表
     * 请求参数
     * mansionRoomId
     * mansionId
     */
    public static String getMansionHouseVideoList() {
        return SERVER + "getMansionHouseVideoList.html";
    }

    /**
     * 府邸挂断
     * breakUserId
     * breakType
     * roomId
     * mansionRoomId
     */
    public static String breakMansionLink() {
        return SERVER + "breakMansionLink.html";
    }

    /**
     * 用户调用--关闭1V2房间,后台挂断
     * mansionRoomId
     */
    public static String closeMansionLink() {
        return SERVER + "closeMansionLink.html";
    }

    /**
     * 弹首充红包
     */
    public static String getFirstChargeInfo() {
        return SERVER + "getFirstChargeInfo.html";
    }

    /**
     * 首充接口排行榜
     */
    public static String GET_FIRST_CHARGE() {
        return SERVER + "getFirstCharge.html";
    }

    /**
     * 获取用户是否绑定推荐人
     * -1 未绑定推广人
     */
    public static String getReferee() {
        return SERVER + "getReferee.html";
    }

    /**
     * 打招呼
     */
    public static String sendIMToUserMes() {
        return SERVER + "sendIMToUserMes.html";
    }

    /**
     * 打招呼消息列表
     */
    public static String getIMToUserMesList() {
        return SERVER + "getIMToUserMesList.html";
    }

    /**
     * 客服列表
     */
    public static String getServiceId() {
        return SERVER + "getServiceId.html";
    }

    /**
     * 获取系统配置参数
     */
    public static String getSystemConfig() {
        return SERVER + "getSystemConfig.html";
    }

    /**
     * 排行榜领取奖励
     */
    public static String receiveRankGold() {
        return SERVER + "receiveRankGold.html";
    }

    /**
     * 奖励排行榜描述
     * rankType	1:女神榜 2:邀请榜
     * queryType	5:昨日 6:上周 7:上月
     */
    public static String getRankConfig() {
        return SERVER + "getRankConfig.html";
    }

    /**
     * 万元红包奖励列表
     */
    public static String getShareRewardConfigList() {
        return SERVER + "getShareRewardConfigList.html";
    }

    /**
     * 领取红包奖励
     */
    public static String receiveShareRewardGold() {
        return SERVER + "receiveShareRewardGold.html";
    }

    /**
     * 获取奖励
     */
    public static String getUserRankInfo() {
        return SERVER + "getUserRankInfo.html";
    }

    /**
     * 守护列表
     */
    public static String getUserGuardGiftList() {
        return SERVER + "getUserGuardGiftList.html";
    }

    /**
     * 视频挂断后,获取视频消费信息
     */
    public static String getVideoComsumerInfo() {
        return SERVER + "getVideoComsumerInfo.html";
    }

    /**
     * 获取认证状态
     */
    public static String getcertifyStatus() {
        return SERVER + "getcertifyStatus.html";
    }

    /**
     * 获取用户评论列表
     */
    public static String getNewEvaluationList() {
        return SERVER + "getNewEvaluationList.html";
    }

    /**
     * 首充奖励配置(侧边栏广告)
     */
    public static String getNewFirstChargeInfo() {
        return SERVER + "getNewFirstChargeInfo.html";
    }

    /**
     * 获取弹窗广告
     */
    public static String getNewBannerList() {
        return SERVER + "getNewBannerList.html";
    }

    /**
     * Qiniu getAuthorization
     */
    public static String getAuthorization() {
        return SERVER + "getAuthorization.html";
    }

    /**
     * 域名
     */
    public static String getProtectAppVersion() {
        return "http://qyspare.chatchat.vip/app/getProtectAppVersion.html";
    }
    /**************************下方为约会相关***************************/


    /**
     * 获取约会礼物
     *
     * @return 约会礼物列表的请求地址
     */
    public static String getDateGiftList() {
        return SERVER + "getAppointmentGiftList.html";
    }

    /**
     * 获取约会礼物
     *
     * @return 约会礼物列表的请求地址
     */
    public static String createDate() {
        return SERVER + "addAppointment.html";
    }

    /**
     * 获取约会列表
     *
     * @return 获取约会列表的请求地址
     */
    public static String getDateList() {
        return SERVER + "appointmentList.html";
    }

    /**
     * 校验约会暗语
     *
     * @return 校验约会暗语的请求地址
     */
    public static String dateCodeVerify() {
        return SERVER + "appointmentVerify.html";
    }

    /**
     * 操作约会
     *
     * @return 操作约会的请求地址
     */
    public static String operateDate() {
        return SERVER + "cormfireAppointment.html";
    }

}