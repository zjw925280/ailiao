package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播资料bean
 * 作者：
 * 创建时间：2018/7/5
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActorInfoBean<T, K, L, M> extends BaseBean {

    public int t_is_not_disturb;
    public int t_height;//身高
    public String t_autograph;//个性签名
    public String t_phone;//手机号
    public String t_weixin;//微信号
    public String t_qq;//qq号
    public String t_constellation;//星座
    public String t_marriage;//婚姻
    public int t_onLine = 3;//是否在线 0.在线1.离线
    public int t_weight;//	体重
    public int totalCount;//	粉丝数
    public String t_nickName;// 昵称
    public String t_city;//	城市
    public String t_vocation;//		职业
    public int isFollow;//		是否关注 0.未关注 1.已关注
    public String t_reception;//		接听率
    public List<T> lunbotu;//			轮播图数组
    public String t_addres_url;//	轮播图地址
    public String t_handImg;//		头像
    public List<K> lable;//标签
    public String t_login_time;//登录时间
    public List<L> anchorSetup;//	主播设置
    public int isWeixin;//是否查看过微信 0.未查看 1.已查看
    public int isPhone;//是否查看过手机号0.未查看1.已查看
    public int isQQ;//是否查看过QQ号0.未查看1.已查看
    public int t_idcard;//用户号
    public int t_sex;//性别：0.女，1.男
    public int t_role;
    public int t_age;
    public String t_called_video;//接单量
    public int score;//星级
    public M bigRoomData;//大房间直播
    public int isGreet;//是否打过招呼
    public String hobby;//兴趣爱好
    public int house;//房子
    public int car;//车子

    public int videoIdentity;// 视频认证状态 0未认证  1认证通过
    public int phoneIdentity;// 手机认证状态 0未认证  1认证通过（通过有无手机号判断）
    public int idcardIdentity;// 身份认证状态0未认证  1认证通过
    public int t_is_vip = 1;//	是否VIP 0.是1.否
    public float t_score;//分数

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public int getHouse() {
        return house;
    }

    public void setHouse(int house) {
        this.house = house;
    }

    public int getCar() {
        return car;
    }

    public void setCar(int car) {
        this.car = car;
    }

    public int getT_is_not_disturb() {
        return t_is_not_disturb;
    }

    public void setT_is_not_disturb(int t_is_not_disturb) {
        this.t_is_not_disturb = t_is_not_disturb;
    }

    public int getT_height() {
        return t_height;
    }

    public void setT_height(int t_height) {
        this.t_height = t_height;
    }

    public String getT_autograph() {
        return t_autograph;
    }

    public void setT_autograph(String t_autograph) {
        this.t_autograph = t_autograph;
    }

    public String getT_phone() {
        return t_phone;
    }

    public void setT_phone(String t_phone) {
        this.t_phone = t_phone;
    }

    public String getT_weixin() {
        return t_weixin;
    }

    public void setT_weixin(String t_weixin) {
        this.t_weixin = t_weixin;
    }

    public String getT_qq() {
        return t_qq;
    }

    public void setT_qq(String t_qq) {
        this.t_qq = t_qq;
    }

    public String getT_constellation() {
        return t_constellation;
    }

    public void setT_constellation(String t_constellation) {
        this.t_constellation = t_constellation;
    }

    public String getT_marriage() {
        return t_marriage;
    }

    public void setT_marriage(String t_marriage) {
        this.t_marriage = t_marriage;
    }

    public int getT_onLine() {
        return t_onLine;
    }

    public void setT_onLine(int t_onLine) {
        this.t_onLine = t_onLine;
    }

    public int getT_weight() {
        return t_weight;
    }

    public void setT_weight(int t_weight) {
        this.t_weight = t_weight;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getT_nickName() {
        return t_nickName;
    }

    public void setT_nickName(String t_nickName) {
        this.t_nickName = t_nickName;
    }

    public String getT_city() {
        return t_city;
    }

    public void setT_city(String t_city) {
        this.t_city = t_city;
    }

    public String getT_vocation() {
        return t_vocation;
    }

    public void setT_vocation(String t_vocation) {
        this.t_vocation = t_vocation;
    }

    public int getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(int isFollow) {
        this.isFollow = isFollow;
    }

    public String getT_reception() {
        return t_reception;
    }

    public void setT_reception(String t_reception) {
        this.t_reception = t_reception;
    }

    public List<T> getLunbotu() {
        return lunbotu;
    }

    public void setLunbotu(List<T> lunbotu) {
        this.lunbotu = lunbotu;
    }

    public String getT_addres_url() {
        return t_addres_url;
    }

    public void setT_addres_url(String t_addres_url) {
        this.t_addres_url = t_addres_url;
    }

    public String getT_handImg() {
        return t_handImg;
    }

    public void setT_handImg(String t_handImg) {
        this.t_handImg = t_handImg;
    }

    public List<K> getLable() {
        return lable;
    }

    public void setLable(List<K> lable) {
        this.lable = lable;
    }

    public String getT_login_time() {
        return t_login_time;
    }

    public void setT_login_time(String t_login_time) {
        this.t_login_time = t_login_time;
    }

    public List<L> getAnchorSetup() {
        return anchorSetup;
    }

    public void setAnchorSetup(List<L> anchorSetup) {
        this.anchorSetup = anchorSetup;
    }

    public int getIsWeixin() {
        return isWeixin;
    }

    public void setIsWeixin(int isWeixin) {
        this.isWeixin = isWeixin;
    }

    public int getIsPhone() {
        return isPhone;
    }

    public void setIsPhone(int isPhone) {
        this.isPhone = isPhone;
    }

    public int getIsQQ() {
        return isQQ;
    }

    public void setIsQQ(int isQQ) {
        this.isQQ = isQQ;
    }

    public int getT_idcard() {
        return t_idcard;
    }

    public void setT_idcard(int t_idcard) {
        this.t_idcard = t_idcard;
    }

    public int getT_sex() {
        return t_sex;
    }

    public void setT_sex(int t_sex) {
        this.t_sex = t_sex;
    }

    public int getT_role() {
        return t_role;
    }

    public void setT_role(int t_role) {
        this.t_role = t_role;
    }

    public int getT_age() {
        return t_age;
    }

    public void setT_age(int t_age) {
        this.t_age = t_age;
    }

    public String getT_called_video() {
        return t_called_video;
    }

    public void setT_called_video(String t_called_video) {
        this.t_called_video = t_called_video;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public M getBigRoomData() {
        return bigRoomData;
    }

    public void setBigRoomData(M bigRoomData) {
        this.bigRoomData = bigRoomData;
    }

    public int getIsGreet() {
        return isGreet;
    }

    public void setIsGreet(int isGreet) {
        this.isGreet = isGreet;
    }

    public int getVideoIdentity() {
        return videoIdentity;
    }

    public void setVideoIdentity(int videoIdentity) {
        this.videoIdentity = videoIdentity;
    }

    public int getPhoneIdentity() {
        return phoneIdentity;
    }

    public void setPhoneIdentity(int phoneIdentity) {
        this.phoneIdentity = phoneIdentity;
    }

    public int getIdcardIdentity() {
        return idcardIdentity;
    }

    public void setIdcardIdentity(int idcardIdentity) {
        this.idcardIdentity = idcardIdentity;
    }

    public int getT_is_vip() {
        return t_is_vip;
    }

    public void setT_is_vip(int t_is_vip) {
        this.t_is_vip = t_is_vip;
    }

    public float getT_score() {
        return t_score;
    }

    public void setT_score(float t_score) {
        this.t_score = t_score;
    }
}