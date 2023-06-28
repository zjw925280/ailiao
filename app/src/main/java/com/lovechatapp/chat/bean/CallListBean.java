package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：我的通话列表页面bean
 * 作者：
 * 创建时间：2018/10/28
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CallListBean extends BaseBean {

    public int t_id;
    public String t_handImg;
    public String t_create_time;
    public int t_call_time;//通话时间 为null 则电话未接听
    public String t_nickName;
    public String callType;//	呼叫类型：1.呼出 2.呼入
    public int t_role;

}
