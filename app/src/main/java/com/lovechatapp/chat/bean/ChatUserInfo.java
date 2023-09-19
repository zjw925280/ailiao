package com.lovechatapp.chat.bean;

import android.content.Context;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseBean;
import com.lovechatapp.chat.util.ToastUtil;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：用户信息bean
 * 作者：
 * 创建时间：2018/6/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ChatUserInfo extends BaseBean {

    public int t_id;//用户编号
    public int gold;//	约豆
    public String phone;//电话号码
    public Integer t_sex = 2;//性别：0.女，1.男 2.需要选择性别
    public int t_role;//用户角色  1 主播 0 用户
    public String t_nickName;//用户昵称
    public String headUrl;//用户头像地址
    public String t_token;
    public int t_is_vip = 1;//是否VIP 0.是1.否
    public int t_phone_status;//0未绑定,1已绑定手机号
    public String t_city;
    /**
     * IdCard
     */
    public int getIdCard() {
        return 10000 + t_id;
    }

    /**
     * 是否同性别
     */
    public final boolean isSameSex(int sex) {
        return t_sex == sex;
    }

    /**
     * 是否女主播
     */
    public final boolean isWomenActor() {
        return t_sex == 0 && t_role == 1;
    }

    /**
     * 是否男用户
     */
    public final boolean isSexMan() {
        return t_sex == 1;
    }

    /**
     * Vip男用户
     */
    public final boolean isVipMan() {
        return isVip() && isSexMan();
    }

    /**
     * 是否Vip用户
     */
    public final boolean isVip() {
        return t_is_vip == 0;
    }

    public final boolean isSameSexToast(Context context, int sex) {
        boolean b = t_sex == sex;
        if (b) {
            ToastUtil.INSTANCE.showToast(context, R.string.sex_can_not_communicate);
        }
        return b;
    }

}