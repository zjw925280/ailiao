package com.lovechatapp.chat.bean;


import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：聊天消息bean
 * 作者：
 * 创建时间：2018/10/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ChatMessageBean extends BaseBean {

    public boolean isSelf;//是否是自己发出的
    public int type = 0;//消息类型  纯文字: 0  自定义消息: 1
    public String textContent;//如果是纯文字  文字内容
    public String custemJson;//自定义消息JSON
    public long time;//时间


}
