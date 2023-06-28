package com.lovechatapp.chat.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：系统工具类
 * 作者：
 * 创建时间：2018/8/20
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SystemUtil {

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    public static String getOnlyOneId(Context context) {
        String content = getUniqueId(context);
        if (!TextUtils.isEmpty(content)) {
            return content;
        } else {
            try {
                content = toMD5(String.valueOf(System.currentTimeMillis()));
                if (TextUtils.isEmpty(content)) {
                    content = String.valueOf(System.currentTimeMillis());
                }
                return content;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return String.valueOf(System.currentTimeMillis());
            }
        }
    }

    private static String getUniqueId(Context context) {
        try {
            String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            String id = androidID + Build.SERIAL;
            return toMD5(id);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String toMD5(String text) throws NoSuchAlgorithmException {
        //获取摘要器 MessageDigest
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        //通过摘要器对字符串的二进制字节数组进行hash计算
        byte[] digest = messageDigest.digest(text.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte aDigest : digest) {
            //循环每个字符 将计算结果转化为正整数;
            int digestInt = aDigest & 0xff;
            //将10进制转化为较短的16进制
            String hexString = Integer.toHexString(digestInt);
            //转化结果如果是个位数会省略0,因此判断并补0
            if (hexString.length() < 2) {
                sb.append(0);
            }
            //将循环结果添加到缓冲区
            sb.append(hexString);
        }
        //返回整个结果
        return sb.toString();
    }

}
