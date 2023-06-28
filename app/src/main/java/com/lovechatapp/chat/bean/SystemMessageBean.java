package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述: 系统消息bean
 * 作者：
 * 创建时间：2018/8/7
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SystemMessageBean extends BaseBean {

    public int t_id;
    public String t_message_content;//	消息内容
    public int t_is_see;//	是否已读(0.未读1.已读)
    public String t_create_time;//		通知时间
    public String t_message_url;

}
