package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：个人中心个人资料bean
 * 作者：
 * 创建时间：2018/10/25
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UserInfoBean<T> extends BaseBean {

    public int t_role;//用户角色:0.普通用户 1.主播
    public int t_height;//身高
    public String t_autograph;//个性签名
    public String t_phone;//手机号
    public String t_weixin;//微信号
    public String t_constellation;//星座
    public int t_onLine;//是否在线 0.在线1.离线
    public int t_weight;//体重
    public String t_nickName;//昵称
    public String t_city;//城市
    public String t_vocation;//	职业
    public String t_reception;//接听率
    public String t_handImg;//接听率
    public List<T> lable;//	标签数组
    public String t_login_time;//最后登录
    public int t_idcard;//用户号

}
