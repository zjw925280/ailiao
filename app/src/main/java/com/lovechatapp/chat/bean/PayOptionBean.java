package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   充值页面充值方式bean
 * 作者：
 * 创建时间：2018/6/15
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PayOptionBean extends BaseBean {

    public int t_id;
    public String payIcon;//	支付渠道icon
    public String payName;
    public int payType;//	支付类型: -2:微信支付 -1:支付宝支付
    public int isdefault;//	是否默认: -1:非默认 1:默认
    public boolean isSelected = false;

}
