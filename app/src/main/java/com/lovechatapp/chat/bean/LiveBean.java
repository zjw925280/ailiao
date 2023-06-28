package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：大房间直播列表bean
 * 作者：
 * 创建时间：2019/4/22
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class LiveBean extends BaseBean {

    public String t_cover_img;
    public String t_nickName;
    public int viewerCount;//	观看人数(未换算)
    public int t_user_id;//	主播ID
    public int t_is_debut;//	是否开播 0.未开播
    public long t_room_id;//	房间号(未开播 不存在）
    public long t_chat_room_id;//	聊天室Id

}
