package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：账户余额bean
 * 作者：
 * 创建时间：2018/10/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AccountBalanceBean extends BaseBean {

    public int profitAndPay;//-1:支出 1:收益
    public String tTime;
    public String t_handImg;
    public String detail;
    public int t_value;//消耗值(VIP为RMB)
    public int t_change_category;//消耗值(VIP为RMB)

}
