package com.lovechatapp.chat.bean;

import com.alibaba.fastjson.JSON;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：IM自定义消息
 * 作者：
 * 创建时间：2018/8/4
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CustomMessageBean extends BaseBean {

    public String type;//礼物类型  0-约豆 1-礼物
    public int gift_id;//礼物id
    public String gift_name;//礼物名称
    public String gift_still_url;//礼物静态图
    public String gift_gif_url;//礼物静态图
    public int gold_number;//约豆数量
    public int gift_number;//礼物数量
    public String nickName;//昵称
    public String headUrl;//头像

    //多人聊天受赠人昵称
    public String otherName;
    //多人聊天受赠人Id
    public int t_id;
    public int otherId;

    public static CustomMessageBean transformGift(int t_id, int otherId, GiftBean giftBean, String otherName) {
        CustomMessageBean bean = new CustomMessageBean();
        bean.type = "1";
        bean.t_id = t_id;
        bean.otherId = otherId;
        bean.gift_id = giftBean.t_gift_id;
        bean.gift_name = giftBean.t_gift_name;
        bean.gift_still_url = giftBean.t_gift_still_url;
        bean.gift_gif_url = giftBean.t_gift_gif_url;
        bean.gold_number = giftBean.t_gift_gold;
        bean.nickName = AppManager.getInstance().getUserInfo().t_nickName;
        bean.headUrl = AppManager.getInstance().getUserInfo().headUrl;
        bean.otherName = otherName;
        return bean;
    }

    public static CustomMessageBean parseBean(String json) {
        try {
            if (json.contains("serverSend&&"))
                json = json.replace("serverSend&&", "{");
            return JSON.parseObject(json, CustomMessageBean.class);
        } catch (Exception ignore) {
            return null;
        }
    }
}
