package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：分享布局的bean
 * 作者：
 * 创建时间：2018/8/18
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ShareLayoutBean extends BaseBean {

    public int resId;//图片资源id
    public String name;

    public ShareLayoutBean(String name, int resId) {
        this.name = name;
        this.resId = resId;
    }

}
