package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

import java.math.BigDecimal;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：我的CPS用户bean
 * 作者：
 * 创建时间：2018/9/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CpsUserBean extends BaseBean {

    public String t_handImg;
    public BigDecimal recharge_money;//	充值金额
    public BigDecimal t_devote_value;//	贡献值
    public int t_ratio;//贡献比例
    public String t_nickName;

}
