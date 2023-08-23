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
    public float t_text_gold;//	文字聊天约豆
    public float t_video_gold;//  视频聊天约豆
    public float t_weixin_gold;//	查看微信号约豆
    public float t_phone_gold;//	查看手机号约豆
    public float t_voice_gold;//	语音聊天约豆
    public float t_qq_gold;//	查看QQ号约豆

    public float getT_user_id() {
        return t_user_id;
    }

    public void setT_user_id(float t_user_id) {
        this.t_user_id = t_user_id;
    }

    public float getT_text_gold() {
        return t_text_gold;
    }

    public void setT_text_gold(float t_text_gold) {
        this.t_text_gold = t_text_gold;
    }

    public float getT_video_gold() {
        return t_video_gold;
    }

    public void setT_video_gold(float t_video_gold) {
        this.t_video_gold = t_video_gold;
    }

    public float getT_weixin_gold() {
        return t_weixin_gold;
    }

    public void setT_weixin_gold(float t_weixin_gold) {
        this.t_weixin_gold = t_weixin_gold;
    }

    public float getT_phone_gold() {
        return t_phone_gold;
    }

    public void setT_phone_gold(float t_phone_gold) {
        this.t_phone_gold = t_phone_gold;
    }

    public float getT_voice_gold() {
        return t_voice_gold;
    }

    public void setT_voice_gold(float t_voice_gold) {
        this.t_voice_gold = t_voice_gold;
    }

    public float getT_qq_gold() {
        return t_qq_gold;
    }

    public void setT_qq_gold(float t_qq_gold) {
        this.t_qq_gold = t_qq_gold;
    }
}