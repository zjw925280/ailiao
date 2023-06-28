package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/**
 * 多人聊天通话信息封装
 */
public class MultipleChatInfo extends BaseBean {

    /**
     * 通话类型 视频、语音
     */
    public int chatType;

    /**
     * 房间Id
     */
    public int roomId;

    /**
     * 府邸房间Id
     */
    public int mansionRoomId;

    /**
     * 府邸Id
     */
    public int mansionId;

    /**
     * roomType 1:1V1 2:1V2房间类型
     */
    public int roomType;

    /**
     * 拨打方Id
     */
    public int connectUserId;

    public String sign;

    public String roomName;

    public boolean isAudioChat() {
        return chatType == 2;
    }

    public String getChatTypeText(){
        return isAudioChat() ? "语音" : "视频";
    }

}