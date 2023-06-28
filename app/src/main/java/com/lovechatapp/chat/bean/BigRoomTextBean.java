package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：大房间页面文字消息bean
 * 作者：
 * 创建时间：2018/8/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class BigRoomTextBean extends BaseBean {

    public String nickName;
    public String content;
    public int type;//文字类型: 1 文字  2  用户进入  3  warning提示

}
