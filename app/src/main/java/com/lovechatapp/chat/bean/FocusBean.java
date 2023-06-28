package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：关注列表Bean
 * 作者：
 * 创建时间：2018/6/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class FocusBean extends BaseBean {

    public int t_id;//	关注主播编号
    public int avgScore;//评价成绩
    public String t_handImg;//头像
    public String t_cover_img;//封面图
    public int t_age;//
    public String t_nickName;//昵称
    public String t_city;//	城市
    public String t_autograph;//
    public int t_state = -1;//状态 : 0.空闲 1.忙碌 2.离线

    public int t_sex;
    public int t_role;
    public int isFollow;
    public int isCoverFollow;

}
