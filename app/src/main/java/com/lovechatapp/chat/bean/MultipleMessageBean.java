package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/**
 * 多人聊天室消息bean
 */
public class MultipleMessageBean extends BaseBean {

    public String nickName;
    public String content;
    public int type;//文字类型: 1 文字  2  用户进入  3  warning提示

}