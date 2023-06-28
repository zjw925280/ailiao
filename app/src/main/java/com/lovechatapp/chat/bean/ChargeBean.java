package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   设置收费bean
 * 作者：
 * 创建时间：2018/7/13
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ChargeBean extends BaseBean {

    public float t_user_id;
    public float t_text_gold;//	文字聊天金币
    public float t_video_gold;//  视频聊天金币
    public float t_weixin_gold;//	查看微信号金币
    public float t_phone_gold;//	查看手机号金币
    public float t_voice_gold;//	语音聊天金币
    public float t_qq_gold;//	查看QQ号金币
}