package com.lovechatapp.chat.bean;


import com.lovechatapp.chat.base.BaseBean;

import java.math.BigDecimal;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述: 提现明细bean
 * 作者：
 * 创建时间：2018/7/30
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class WithDrawDetailBean extends BaseBean {

    public String tTime;//	日期
    public BigDecimal t_money;//钱
    public int t_type;//	0.支付宝 1.微信
    public int t_order_state;//0.未审核  1.提现成功  2.提现失败

}
