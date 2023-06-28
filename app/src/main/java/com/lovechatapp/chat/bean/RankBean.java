package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

import java.text.DecimalFormat;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：榜单页面bean
 * 作者：
 * 创建时间：2018/9/26
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class RankBean extends BaseBean {

    static DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public int t_id;
    public int t_idcard;
    public int gold;
    public String t_handImg;
    public String t_nickName;
    public int t_role;
    public int t_rank_gold;
    public int t_is_receive;
    public int rankRewardId;
    public int t_rank_switch;//神秘人 1:隐藏排行榜  0:不隐藏
    public transient int off_gold;

    public String getOffGold() {
        if (off_gold > 10000) {
            return decimalFormat.format(off_gold / 10000f) + "w";
        } else if (off_gold > 1000) {
            return decimalFormat.format(off_gold / 1000f) + "k";
        }
        return off_gold + "";
    }
}