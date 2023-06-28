package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   我的Fragment
 * 作者：
 * 创建时间：2018/7/2
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UserCenterBean extends BaseBean {

    //用户金币
    public int amount;

    //用户昵称
    public String nickName;

    //头像地址
    public String handImg;

    //用户角色:0.普通用户1.主播
    public int t_role;

    //是否VIP 0.是1.否
    public int t_is_vip = 1;

    //是否勿扰 0.否 1.是
    public int t_is_not_disturb;

    //	ID号
    public int t_idcard;

    //性别:0.女 1.男
    public int t_sex = 2;

    //是否申请公会 0.未申请 1.审核中 2.已通过
    public int isGuild;

    //	是否加入公会 0.未加入 1.已加入
    public int isApplyGuild = 1;

    //	公会名称(用户申请了公会或者加入了公会才会存在)
    public String guildName;

    //	 cps推广 -1:未申请 1:审核中 2:已通过 3:已下架
    public int isCps = -1;

    //可提现金币
    public int extractGold;

    //	个性签名
    public String t_autograph;

    //		相册数量
    public int albumCount;

    //		动态数量
    public int dynamCount;

    //		关注数量
    public int followCount;

    public int t_age;

    //		用户年龄
    public int spprentice;

    //	徒弟数
    public String endTime;

    //vip到期时间
    public int browerCount;

    //最近来访
    public String t_phone;

    //语音聊天开关
    public int t_voice_switch;

    //文字聊天开关
    public int t_text_switch;

    //视频认证状态 0未认证  1认证通过  2认证中
    public int videoIdentity;

    //手机认证状态 0未认证  1认证通过
    public int phoneIdentity;

    //身份认证状态 0未认证  1认证通过  2认证中
    public int idcardIdentity;

    //  谁喜欢我
    public int likeMeCount;

    //  我喜欢谁
    public int myLikeCount;

    // 相互喜欢
    public int eachLikeCount;

    public String t_city;

    public int t_float_switch;//屏蔽飘屏（屏蔽所有飘屏） 0:不屏蔽   1:屏蔽

    public int t_rank_switch;//	榜单隐身（排行榜中隐身）0:不隐身  1:隐身

    public int getVip() {
        return t_is_vip;
    }

    public final boolean isVip() {
        return t_is_vip == 0;
    }

    public final boolean isVipOrSVip() {
        return isVip() || isVip();
    }
}