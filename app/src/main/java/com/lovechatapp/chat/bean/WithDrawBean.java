package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：提现页面bean
 * 作者：
 * 创建时间：2018/8/16
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class WithDrawBean<T> extends BaseBean {

    public List<T> data;//绑定信息
    public int totalMoney;//可提现约豆

}
