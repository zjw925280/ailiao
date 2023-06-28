package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：未读消息bean
 * 作者：
 * 创建时间：2019/1/9
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UnReadBean<T> extends BaseBean {

    public T data;
    public int totalCount;
    public int mansionCount;
    public int groupCount;

}
