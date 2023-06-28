package com.lovechatapp.chat.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.bean.ChatUserInfo;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：SharedPreference帮助类
 * 作者：
 * 创建时间：2018/6/72
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SharedPreferenceHelper {

    //登录相关
    private static final String KEY_CONFIG_LOGIN = "login";
    private static final String PHONE = "phone";
    private static final String GENDER = "t_sex";
    private static final String USER_ID = "t_id";
    private static final String IS_VIP = "t_is_vip";
    private static final String IS_SVIP = "t_is_svip";
    private static final String T_ROLE = "t_role";
    private static final String NICK_NAME = "nickName";
    private static final String HEAD_URL = "headUrl";//头像
    private static final String TOKEN = "t_token";
    private static final String PHONE_BIND = "phone_bind";
    //极光相关
    private static final String KEY_JPUSH = "jpush";
    private static final String JPUSH_ALIAS = "alias";//别名

    //坐标
    private static final String KEY_CODE = "code";
    private static final String CODE_LAT = "code_lat";
    private static final String CODE_LNG = "code_lng";
    private static final String CITY = "city";

    //保存用户自己是否mute
    private static final String KEY_MUTE = "mute";
    private static final String MUTE = "mute_mute";

    //保存消息提示音 震动
    private static final String KEY_TIP = "key_tip";
    private static final String SOUND = "tip_sound";
    private static final String SOUND2 = "tip_sound2";
    private static final String VIBRATE = "tip_vibrate";
    private static final String VIBRATE2 = "tip_vibrate2";

    //客服QQ号
    private static final String KEY_QQ = "key_qq";
    private static final String QQ = "qq";

    //分享
    private static final String KEY_SHARE = "key_share";
    private static final String SHARE_URL = "share_url";
    private static final String SHARE_IMAGE = "share_image";

    //未成年模式
    private static final String YOUNG_MODE = "young_mode";
    private static final String HAVE_SHOW = "have_show";//是否显示过了
    private static final String YOUNG_MODE_PASSWORD = "young_mode_password";//青少年模式密码

    //分享user id
    private static final String KEY_SHARE_ID = "key_share_id";
    private static final String SHARE_USER_ID = "share_user_id";

    //IM过滤词
    private static final String IM_FILTER = "im_filter";
    private static final String FILTER_WORD = "filter_word";

    //置顶
    private static final String TOP = "conversation_top";

    private static final String IS_FIRST_IN_USER_INFO = "first_user_info";

    /**
     * 保存用户登录账户信息
     */
    public static void saveAccountInfo(Context context, ChatUserInfo chatUserInfo) {
        AppManager.getInstance().setToken(chatUserInfo.t_token);
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PHONE, chatUserInfo.phone);
        editor.putInt(USER_ID, chatUserInfo.t_id);
        editor.putInt(IS_VIP, chatUserInfo.t_is_vip);
        editor.putInt(T_ROLE, chatUserInfo.t_role);
        editor.putInt(GENDER, chatUserInfo.t_sex);
        editor.putString(NICK_NAME, chatUserInfo.t_nickName);
        editor.putString(HEAD_URL, chatUserInfo.headUrl);
        editor.putString(TOKEN, chatUserInfo.t_token);
        editor.putInt(PHONE_BIND, chatUserInfo.t_phone_status);
        editor.apply();
        AppManager.getInstance().setToken(chatUserInfo.t_token);
    }

    public static String getToken() {
        SharedPreferences sp = AppManager.getInstance().getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        return sp.getString(TOKEN, null);
    }

    /**
     * 保存用户账户性别信息
     */
    public static void saveGenderInfo(Context context, int gender) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(GENDER, gender);
        editor.apply();
    }

    /**
     * 保存用户账户角色信息
     */
    public static void saveRoleInfo(Context context, int role) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(T_ROLE, role);
        editor.apply();
    }

    /**
     * 获取登录的账户信息
     */
    public static ChatUserInfo getAccountInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        ChatUserInfo chatUserInfo = new ChatUserInfo();
        chatUserInfo.t_id = sp.getInt(USER_ID, 0);
        chatUserInfo.phone = sp.getString(PHONE, "");
        chatUserInfo.t_is_vip = sp.getInt(IS_VIP, 1);
        chatUserInfo.t_sex = sp.getInt(GENDER, 2);
        chatUserInfo.t_role = sp.getInt(T_ROLE, 2);
        chatUserInfo.t_nickName = sp.getString(NICK_NAME, "");
        chatUserInfo.headUrl = sp.getString(HEAD_URL, "");
        chatUserInfo.t_token = sp.getString(TOKEN, "");
        chatUserInfo.t_phone_status = sp.getInt(PHONE_BIND, 1);
        if (TextUtils.isEmpty(AppManager.getInstance().getToken())) {
            AppManager.getInstance().setToken(sp.getString(TOKEN, ""));
        }
        return chatUserInfo;
    }

    /**
     * 保存用户VIP状态
     */
    public static void saveUserVip(Context context, int vip) {
        AppManager.getInstance().getUserInfo().t_is_vip = vip;
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(IS_VIP, vip);
        editor.apply();
    }

    /**
     * 保存用户头像
     */
    public static void saveHeadImgUrl(Context context, String imgUrl) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(HEAD_URL, imgUrl);
        editor.apply();
    }

    /**
     * 保存用户昵称
     */
    public static void saveUserNickName(Context context, String nickName) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(NICK_NAME, nickName);
        editor.apply();
    }

    /**
     * 保存极光别名
     */
    public static void saveJPushAlias(Context context, String alisa) {
        SharedPreferences sp = context.getSharedPreferences(KEY_JPUSH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(JPUSH_ALIAS, alisa);
        editor.apply();
    }

    /**
     * 获取极光别名
     */
    public static String getJPushAlias(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_JPUSH, Context.MODE_PRIVATE);
        return sp.getString(JPUSH_ALIAS, "");
    }

    /**
     * 保存坐标
     */
    public static void saveCode(Context context, String lat, String lng) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CODE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CODE_LAT, lat);
        editor.putString(CODE_LNG, lng);
        editor.apply();
    }

    /**
     * 保存城市
     */
    public static void saveCity(Context context, String city) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CODE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CITY, city);
        editor.apply();
    }

    /**
     * 获取城市
     */
    public static String getCity(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CODE, Context.MODE_PRIVATE);
        return sp.getString(CITY, "");
    }

    /**
     * 获取坐标LAT
     */
    public static String getCodeLat(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CODE, Context.MODE_PRIVATE);
        return sp.getString(CODE_LAT, "");
    }

    /**
     * 获取坐标LNG
     */
    public static String getCodeLng(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CODE, Context.MODE_PRIVATE);
        return sp.getString(CODE_LNG, "");
    }

    /**
     * 保存mute
     */
    public static void saveMute(Context context, boolean mute) {
        SharedPreferences sp = context.getSharedPreferences(KEY_MUTE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(MUTE, mute);
        editor.apply();
    }

    /**
     * 获取MUTE,默认false  表示没有mute,摄像头是开启的
     */
    public static boolean getMute(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_MUTE, Context.MODE_PRIVATE);
        return sp.getBoolean(MUTE, false);
    }

    /**
     * 保存消息提示音
     */
    public static void saveTipSound(Context context, boolean open) {
        SharedPreferences sp = context.getSharedPreferences(KEY_TIP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(SOUND, open);
        editor.apply();
    }

    /**
     * 保存消息提示音
     */
    public static void saveTipSound2(Context context, boolean open) {
        SharedPreferences sp = context.getSharedPreferences(KEY_TIP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(SOUND2, open);
        editor.apply();
    }

    /**
     * 保存消息提示震动
     */
    public static void saveTipVibrate(Context context, boolean open) {
        SharedPreferences sp = context.getSharedPreferences(KEY_TIP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(VIBRATE, open);
        editor.apply();
    }

    /**
     * 保存消息提示震动
     */
    public static void saveTipVibrate2(Context context, boolean open) {
        SharedPreferences sp = context.getSharedPreferences(KEY_TIP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(VIBRATE2, open);
        editor.apply();
    }

    /**
     * 默认开启声音
     */
    public static boolean getTipSound(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_TIP, Context.MODE_PRIVATE);
        return sp.getBoolean(SOUND, false);
    }

    /**
     * 默认开启震动
     */
    public static boolean getTipVibrate(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_TIP, Context.MODE_PRIVATE);
        return sp.getBoolean(VIBRATE, true);
    }

    /**
     * 默认开启声音
     */
    public static boolean getTipSound2(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_TIP, Context.MODE_PRIVATE);
        return sp.getBoolean(SOUND2, false);
    }

    /**
     * 默认开启震动
     */
    public static boolean getTipVibrate2(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_TIP, Context.MODE_PRIVATE);
        return sp.getBoolean(VIBRATE2, true);
    }

    /**
     * 保存客服QQ
     */
    public static void saveQQ(Context context, String qq) {
        SharedPreferences sp = context.getSharedPreferences(KEY_QQ, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(QQ, qq);
        editor.apply();
    }

    /**
     * 获取客服QQ号
     */
    public static String getQQ(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_QQ, Context.MODE_PRIVATE);
        return sp.getString(QQ, "");
    }

    /**
     * 保存分享链接
     */
    public static void saveShareUrl(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(KEY_SHARE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SHARE_URL, url);
        editor.apply();
    }

    /**
     * 获取分享链接
     */
    public static String getShareUrl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_SHARE, Context.MODE_PRIVATE);
        return sp.getString(SHARE_URL, "");
    }

    /**
     * 保存图片地址
     */
    public static void saveShareImage(Context context, String imageUrl) {
        SharedPreferences sp = context.getSharedPreferences(KEY_SHARE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SHARE_IMAGE, imageUrl);
        editor.apply();
    }

    /**
     * 获取分享链接
     */
    public static String getShareImage(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_SHARE, Context.MODE_PRIVATE);
        return sp.getString(SHARE_IMAGE, "");
    }

    /**
     * 保存是否显示过
     */
    public static void setHaveShow(Context context) {
        SharedPreferences sp = context.getSharedPreferences(YOUNG_MODE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(HAVE_SHOW, true);
        editor.apply();
    }

    /**
     * 获取是否显示过
     */
    public static boolean getHaveShow(Context context) {
        SharedPreferences sp = context.getSharedPreferences(YOUNG_MODE, Context.MODE_PRIVATE);
        return sp.getBoolean(HAVE_SHOW, false);
    }

    /**
     * 保存青少年模式密码
     */
    public static void setYoungPassword(Context context, String password) {
        SharedPreferences sp = context.getSharedPreferences(YOUNG_MODE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(YOUNG_MODE_PASSWORD, password);
        editor.apply();
    }

    /**
     * 获取青少年模式密码
     */
    public static String getYoungPassword(Context context) {
        SharedPreferences sp = context.getSharedPreferences(YOUNG_MODE, Context.MODE_PRIVATE);
        return sp.getString(YOUNG_MODE_PASSWORD, "");
    }

    /**
     * 保存分享ID
     */
    public static void saveShareId(Context context, String userId) {
        SharedPreferences sp = context.getSharedPreferences(KEY_SHARE_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SHARE_USER_ID, userId);
        editor.apply();
    }

    /**
     * 获取分享ID
     */
    public static String getShareId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_SHARE_ID, Context.MODE_PRIVATE);
        return sp.getString(SHARE_USER_ID, "");
    }

    /**
     * 保存Im过滤词
     */
    static void setFilterWord(Context context, String filterWord) {
        SharedPreferences sp = context.getSharedPreferences(IM_FILTER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(FILTER_WORD, filterWord);
        editor.apply();
    }

    /**
     * 获取IM过滤词
     */
    static String getFilterWord(Context context) {
        SharedPreferences sp = context.getSharedPreferences(IM_FILTER, Context.MODE_PRIVATE);
        return sp.getString(FILTER_WORD, "");
    }


    /**
     * 置顶
     */
    public static void setTop(Context context, String top, boolean remove) {
        List<String> list = getTop(context);
        if (remove) {
            list.remove(top);
        } else {
            if (list.size() >= 30) {
                list.remove(list.size() - 1);
            }
            if (!list.contains(top)) {
                list.add(top);
            }
        }
        setTop(context, JSON.toJSONString(list));
    }

    /**
     * 置顶
     */
    private static void setTop(Context context, String top) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(TOP, top);
        editor.apply();
    }

    /**
     * 置顶
     */
    public static List<String> getTop(Context context) {
        List<String> list = new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        try {
            String string = sp.getString(TOP, "");
            if (!TextUtils.isEmpty(string)) {
                List<String> local = JSON.parseObject(string, new TypeReference<List<String>>() {
                });
                list.addAll(local);
            }
        } catch (Exception e) {
            setTop(context, null);
        }
        return list;
    }

    public static boolean getIsFirstInUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        return sp.getBoolean(IS_FIRST_IN_USER_INFO, true);
    }

    public static void setUserInfoGet(Context context) {
        SharedPreferences sp = context.getSharedPreferences(KEY_CONFIG_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(IS_FIRST_IN_USER_INFO, false);
        editor.apply();
    }

}