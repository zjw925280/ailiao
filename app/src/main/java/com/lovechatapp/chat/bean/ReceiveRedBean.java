package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   我的Fragment
 * 作者：
 * 创建时间：2018/8/8
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ReceiveRedBean extends BaseBean {

    public String t_redpacket_content;//	红包内容
    public int t_redpacket_type;//		红包类型 0.赠送红包 1.返利红包
    public int t_redpacket_gold;//		金币数
    public String t_create_time;//	时间
    public String t_handImg;//	头像
    public String t_nickName;//	昵称

}
