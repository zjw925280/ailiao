package com.lovechatapp.chat.socket.domain;

import java.io.Serializable;

public class Mid implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer mid;

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    /**
     * socket登录成功
     */
    public static final int LOGIN_SUCCESS = 30002;

    /**
     * 发送模拟消息
     */
    public static final int SEND_VIRTUAL_MESSAGE = 30003;
    /**
     * 通知连线
     */
    public static final int CHAT_LINK = 30004;

    /**
     * 对方已挂断
     */
    public static final int HAVE_HANG_UP = 30005;

    /**
     * 被封号
     */
    public static final int BEAN_SUSPEND = 30006;

    /**
     * 用户收到主播邀请
     */
    public static final int USER_GET_INVITE = 30008;

    /**
     * 用户余额不足一分钟
     */
    public static final int MONEY_NOT_ENOUGH = 30010;

    /**
     * 动态新评论
     */
    public static final int ACTIVE_NEW_COMMENT = 30009;

    /**
     * 主播开启速配的时候,下发提示消息,只提示主播
     */
    public static final int QUICK_START_HINT_ANCHOR = 30013;

    /**
     * 接通视频的时候,下发提示消息,主播用户都要提示
     */
    public static final int VIDEO_CHAT_START_HINT = 30012;

    /**
     * 大房间人数变化
     */
    public static final int BIG_ROOM_COUNT_CHANGE = 30014;


    /**
     * 通知语音用户--->主播连线
     */
    public static final int onLineToVoiceRes = 30015;

    /**
     * 通知语音 主播-->用户语音连线
     */
    public static final int anchorLinkUserToVoiceRes = 30016;


    /**
     * 通知用户挂断语音消息
     */
    public static final int brokenVoiceLineRes = 30018;

    /**
     * 收到礼物、用户充值、用户购买vip
     */
    public static final int RECEIVE_GIFT = 30017;

    /**
     * 通知主播，用户拒接
     */
    public static final int brokenVIPLineRes = 30019;

    /**
     * 通知用户，主播拒接
     */
    public static final int brokenUserLineRes = 30020;

    /**
     * 通知用户，直播违规
     */
    public static final int invalidChat = 30021;

    /**
     * 1V2房间视频通知连线  用户呼叫主播
     */
    public static final int video_user_to_anchor_onLineRes = 30022;

    /**
     * 1V2房间语音通知连线  用户呼叫主播
     */
    public static final int voice_user_to_anchor_onLineRes = 30023;

    /**
     * 1V2房间语音通知连线  视频挂断
     */
    public static final int video_brokenLineRes = 30024;

    /**
     * 1V2房间语音通知连线   语音挂断
     */
    public static final int voice_brokenLineRes = 30025;

}
