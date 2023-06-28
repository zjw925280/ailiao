package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;


/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：动态评论bean
 * 作者：
 * 创建时间：2019/1/3
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActiveCommentBean extends BaseBean {

    public int t_id;//	用户编号
    public String t_handImg;//
    public String t_nickName;//
    public int t_sex;//	性别
    public long t_create_time;//
    public String t_comment;//		评论内容
    public int comType;//评论人类型 1.自己 显示删除 2.其他人评论
    public int comId;//评论ID

}
