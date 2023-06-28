package com.lovechatapp.chat.bean;


import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：地图弹窗bean
 * 作者：
 * 创建时间：2018/11/21
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class InfoBean extends BaseBean {

    public int t_idcard;//	平台短号
    public int t_role;//0.普通用户 1.主播
    public String t_autograph;
    public double distance;//	距离 (公里)
    public int t_onLine;//	是否在线 0.在线1.离线
    public String t_nickName;
    public int t_id;
    public String t_vocation;//	职业
    public int isFollow;//	是否关注 0.未关注 1.已关注
    public String t_handImg;
    public int t_age;
    public int t_sex;//性别 0.女 1.男

}
