package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述:  速配主播bean
 * 作者：
 * 创建时间：2018/6/21
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class QuickAnchorBean extends BaseBean {

    public int t_id;//	主播Id
    public int t_idcard;
    public String t_handImg;
    public String t_nickName;
    public String t_city;
    public int t_age;
    public String t_autograph;
    public String rtmp;
    public int isFollow;//	是否关注 0.为关注 1.已关注
    public int roomId;
    public String t_vocation;//职业

}
