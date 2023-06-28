package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：动态List Bean
 * 作者：
 * 创建时间：2018/12/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActiveBean<T> extends BaseBean {

    public int t_id;
    public String t_handImg;
    public String t_nickName;
    public int t_sex;
    public int t_age;
    public int dynamicId;//动态编号
    public long t_create_time;//动态时间戳  秒
    public String t_content;//	动态内容
    public int praiseCount;//点赞数
    public int isPraise;//当前用户是否点赞
    public int commentCount;//	评论数
    public int isFollow;//	是否关注
    public List<T> dynamicFiles;//文件列表
    public String t_address;//位置

}
