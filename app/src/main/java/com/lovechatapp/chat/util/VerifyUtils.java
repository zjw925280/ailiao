package com.lovechatapp.chat.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：验证工具类
 * 作者：
 * 创建时间：2018/6/25
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class VerifyUtils {

    /**
     * 验证手机号是否合法
     */
    public static boolean isPhoneNum(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        //Pattern p = Pattern.compile("^1[3,4,5,7,8]\\d{9}$");
        //扩展到13,14,15,16,17,18,19号段，保证向后兼容
        Pattern p = Pattern.compile("^1[3,4,5,6,7,8,9]\\d{9}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

}
