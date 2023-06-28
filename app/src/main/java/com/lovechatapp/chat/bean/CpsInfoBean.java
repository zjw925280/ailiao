package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

import java.math.BigDecimal;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：我的CPS信息bean
 * 作者：
 * 创建时间：2018/9/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CpsInfoBean extends BaseBean {

    public BigDecimal setMoney;//已提现
    public int t_sex;//性别 0.女 1.男
    public String t_handImg;//头像
    public int t_is_vip;
    public int totalUser;//	总推广用户
    public BigDecimal totalMoney;//		总贡献
    public String t_real_name;//		真名
    public String realPhone;//		电话
    public String t_nickName;//

}
