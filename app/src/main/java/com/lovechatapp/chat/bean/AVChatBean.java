package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：视频聊天bean
 * 作者：
 * 创建时间：2018/7/17
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AVChatBean extends BaseBean {

    public int onlineState;//用户是否有钱 -1:余额不足不能聊天   0.余额刚刚住够   1.余额大于主播设置的约豆数
    public int coverRole;//	对方角色
    public int roomId;//	房间号
    public int otherId;
    public int chatType;
    public String sign;
    public boolean countdown;
    public boolean isRequest;
    public boolean closeVideo;

    /**
     * 己方是否主播
     **/
    public boolean isActor() {
        return getActorId() == AppManager.getInstance().getUserInfo().t_id;
    }

    public int getUserId() {
        if (coverRole == 1 && AppManager.getInstance().getUserInfo().t_role == 1) {
            return isRequest ? AppManager.getInstance().getUserInfo().t_id : otherId;
        } else if (coverRole == 1) {
            return AppManager.getInstance().getUserInfo().t_id;
        } else if (AppManager.getInstance().getUserInfo().t_role == 1) {
            return otherId;
        } else {
            return isRequest ? AppManager.getInstance().getUserInfo().t_id : otherId;
        }
    }

    public int getActorId() {
        if (coverRole == 1 && AppManager.getInstance().getUserInfo().t_role == 1) {
            return isRequest ? otherId : AppManager.getInstance().getUserInfo().t_id;
        } else if (coverRole == 1) {
            return otherId;
        } else if (AppManager.getInstance().getUserInfo().t_role == 1) {
            return AppManager.getInstance().getUserInfo().t_id;
        } else {
            return isRequest ? otherId : AppManager.getInstance().getUserInfo().t_id;
        }
    }

}