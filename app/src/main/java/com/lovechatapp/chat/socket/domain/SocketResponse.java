package com.lovechatapp.chat.socket.domain;

import com.lovechatapp.chat.base.BaseBean;

public class SocketResponse extends BaseBean {

    public int mid;
    public String message;//提示消息
    public int state;//登陆状态
    public String msgContent;
    public int activeUserId;
    public int roomId;//房间号
    public int connectUserId;//链接人用户编号
    public int satisfy;//-1 钱不够  1钱够
    public int userCount;//大房间人数
    public String sendUserName;//大房间人数
    public String content;
    public int breakUserId;

    public transient String sourceData;

}