package com.lovechatapp.chat.bean;


import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主页
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SearchBean extends BaseBean {

    public int t_id;
    public int t_role;//	角色 0.用户 1.主播
    public String t_handImg;
    public int t_online;//状态0.空闲1.忙碌2.离线
    public String t_nickName;
    public int t_is_public;//是否存在公共视频:0.不存在 1.存在
    public int t_idcard;//	id

}
