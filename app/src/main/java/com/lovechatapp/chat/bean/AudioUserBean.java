package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;


/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   其他用户信息bean
 * 作者：
 * 创建时间：2018/7/10
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AudioUserBean extends BaseBean {

    public String nickName;
    public String handImg;
    public String t_autograph;//个性签名
    public int isFollow;//是否关注  0 - 未关注  1 - 关注
    public int t_sex;
    public int t_role;
    public String t_addres_url;//公开视频地址
    public String t_video_img;//公开视频封面
    public int goldLevel;//等级
    public String t_city;
    public String balance;
    public int t_age;
    public String t_cover_img;

}
