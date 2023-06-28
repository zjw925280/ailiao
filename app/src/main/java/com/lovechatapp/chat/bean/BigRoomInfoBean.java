package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：大房间信息bean
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class BigRoomInfoBean<T> extends BaseBean {

    public String t_handImg;
    public String t_nickName;
    public int followNumber;//	主播关注人数
    public int isFollow;//	当前用户是否关注主播
    public int viewer;//	观看人数
    public List<T> devoteList;//用户列表
    public String warning;//	提示消息
    public int isDebut;//0.未开播 1.已开播

}
