package com.tencent.qcloud.tim.uikit.modules.chat.layout.message;

import com.google.gson.Gson;
import com.tencent.imsdk.TIMCustomElem;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义消息类
 */
public class ImCustomMessage {

    /**
     * 视频非法
     */
    public static final String Type_pulp = "pulp";

    /**
     * 被踢出房间
     */
    public static final String Type_kickUser = "kickUser";

    /**
     * 加入房间
     */
    public static final String Type_joined = "joined";

    /**
     * 离开房间
     */
    public static final String Type_leaved = "leaved";

    /**
     * 自定义赠送约豆消息
     */
    public static final String Type_gold = "0";

    /**
     * 自定义赠送礼物消息
     */
    public static final String Type_gift = "1";

    /**
     * 自定义图片消息
     */
    public static final String Type_picture = "picture";

    /**
     * 自定义语音消息
     */
    public static final String Type_voice = "voice";

    /**
     * 用户拨打无人接听
     */
    public static final String Type_video_unconnect_user = "video_unconnect_user";

    /**
     * 主播拨打无人接听
     */
    public static final String Type_video_unconnect_anchor = "video_unconnect_anchor";

    /**
     * 视频接听成功
     */
    public static final String Type_video_connect = "video_connect";

    /**
     * 视频呼叫
     */
    public static final String Call_Type_Video = "video";

    /**
     * 音频呼叫
     */
    public static final String Call_Type_Audio = "audio";
    /**
     * 约会消息
     */
    public static final String Type_Date = "date";

    //消息类型
    public String type;

    public String otherName;

    public int t_id;
    public int inviteeId;
    public int inviterId;
    //----------礼物部分----------
    //type 礼物类型  0-约豆 1-礼物
    public int gift_id;//礼物id
    public int giftId;//礼物id
    public String gift_name;//礼物名称
    public String gift_still_url;//礼物静态图
    public String gift_gif_url;//礼物静态图
    public int gold_number;//约豆数量
    public int giftGold;
    public int  invitationID;
    public int gift_number;//礼物数量
    //    otherName //受赠人
    //----------礼物部分----------

    //----------通话记录部分----------
    public String call_type;
    public int video_time;
    //----------通话记录部分----------

    //----------自定义语音消息部分----------
    public String fileUrl;
    public String duration;
    //----------自定义语音消息部分----------

    //----------自定义房间消息部分----------
    public int otherId;
    //----------自定义房间消息部分----------
    //----------自定义约会消息部分----------
    public int appointmentId;//约会id
    public String inviterName;//发起人姓名
    public String inviterPhone;//发起人手机
    public int sex;//发起人性别
    public int inviterAge;//发起人年龄
    public String appointmentAddress;//约会城市
    public String appointmentTime;//约会时间
    public String remarks;//约会备注
    public String giftImg;//约会礼物图片地址
    public int appointmentStatus;//约会邀请状态
    public boolean isCharge;//付款方\

    //----------自定义约会消息部分----------

    /**
     * 获取会话消息类型text
     */
    public final String getImType() {
        if ("0".equals(type) || "1".equals(type)) {
            return "[" + gift_name + "]";
        } else if (Type_picture.equals(type)) {
            return "[图片]";
        } else if (Type_voice.equals(type)) {
            return "[语音]";
        }else if (Type_Date.equals(type)) {
            return "[约会]";
        } else {
            return "[通话]";
        }
    }

    public final boolean isVideo() {
        return Call_Type_Video.equals(call_type);
    }

    public final String getCallTypeText() {
        return Call_Type_Video.equals(call_type) ? "视频" : "语音";
    }

    public final String getVideoTime() {
        return String.format("通话时间: %s分钟", video_time);
    }

    @Override
    public String toString() {
        return "--不支持的消息--";
    }

    /**
     * 构建房间消息
     */
    public static TIMCustomElem buildRoomMessage(String type, int sendId, int otherId, String otherName) {
        Map<String, Object> map = new HashMap<>();
        map.put("t_id", sendId);
        map.put("otherName", otherName);
        map.put("otherId", otherId);
        map.put("type", type);
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(new Gson().toJson(map).getBytes());
        return elem;
    }

    /**
     * 构建非法视频消息
     */
    public static TIMCustomElem buildIllegalVideo() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", Type_pulp);
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(new Gson().toJson(map).getBytes());
        return elem;
    }

}