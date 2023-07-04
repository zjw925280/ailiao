package com.lovechatapp.chat.gift;

import com.lovechatapp.chat.base.BaseBean;

/**
 * Created by yuhengyi on 2016/11/9.
 * Describe:
 */

public class AnimMessage extends BaseBean {

    public String userName;
    public String giftName;
    public String headUrl;
    public int userId;
    public int giftNum;
    boolean isComboAnimationOver;
    long updateTime;
    public String giftImgUrl;
    public String giftType;//0-约豆 1-礼物

}
