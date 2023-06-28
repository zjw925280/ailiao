package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;


/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：消息列表bean
 * 作者：
 * 创建时间：2018/7/10
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class MessageBean extends BaseBean {

    public String t_id;//用户编号
    public String lastMessage;//		消息内容
    public long unReadCount;//未读消息数
    public long t_create_time;//	通知时间
    public String nickName;//
    public String headImg;//
    public boolean isTop;

}