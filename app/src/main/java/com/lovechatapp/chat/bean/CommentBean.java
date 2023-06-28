package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：评价bean
 * 作者：
 * 创建时间：2018/7/6
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CommentBean extends BaseBean {

    public int t_user_id;//	评价人的编号
    public String t_user_hand;//	头像
    public String t_user_nick;//	昵称
    public String t_label_name;//评论标签 多个标签以逗号分隔
    public int evaluationCount;

}
