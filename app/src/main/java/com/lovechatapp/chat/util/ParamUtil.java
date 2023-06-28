package com.lovechatapp.chat.util;

import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.bean.ChatUserInfo;
import java.util.Map;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：通用工具
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ParamUtil {

    /**
     * 获取加密后的参数
     */
    public static String getParam(Map paramMap) {
        try {
            if (!TextUtils.isEmpty(AppManager.getInstance().getToken())) {
                paramMap.put("t_token", AppManager.getInstance().getToken());
            }
            paramMap.put("tokenId", getUserId());
            String json = JSON.toJSONString(paramMap);
            return RUtil.publicEncrypt(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取加密后的参数
     */
    public static String getParam(Map<String, Object> paramMap, boolean res) {
        try {
            if (!TextUtils.isEmpty(AppManager.getInstance().getToken())) {
                paramMap.put("t_token", AppManager.getInstance().getToken());
            }
            paramMap.put("tokenId", getUserId());
            String json = JSON.toJSONString(paramMap);
            return RUtil.publicEncrypt(json);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("res==: " + res);
        }
        return "";
    }

    /**
     * 获取UserId
     */
    private static String getUserId() {
        String sUserId = "0";
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                int userId = userInfo.t_id;
                if (userId >= 0) {
                    sUserId = String.valueOf(userId);
                }
            }
        }
        return sUserId;
    }

}
