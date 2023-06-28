package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播视频播放页面信息bean
 * 作者：
 * 创建时间：2018/7/16
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActorPlayBean<T,M> extends BaseBean {

    public String t_weixin;//微信号
    public String t_handImg;//用户头像
    public int t_score;//用户评分
    public String t_addres_url;//视频地址
    public int t_age;//		年龄
    public int t_weixin_gold;//	查看微信金币数
    public String t_nickName;//	用户昵称
    public String t_city;//	所在城市
    public String t_video_img;//视频封面地址
    public int isSee;//是否查看过微信 0.未查看 1.已查看
    public List<T> labels;//标签集
    public int isLaud;//当前用户是否给查看人点赞 0:未点赞 1.已点赞
    public int laudtotal;//	总的点赞数
    public String t_title;//视频标题
    public int videoGold;//视频金币
    public int t_see_count;//视频查看次数
    public int t_onLine = 3;//在线状态 0.空闲1.忙碌2.离线
    public int isFollow;//是否关注 0:未关注 1：已关注
    public M bigRoomData;//大房间直播

}
