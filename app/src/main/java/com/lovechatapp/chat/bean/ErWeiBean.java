package com.lovechatapp.chat.bean;


import com.lovechatapp.chat.base.BaseBean;

import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：二维码bean
 * 作者：
 * 创建时间：2018/10/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ErWeiBean<T> extends BaseBean {

    public List<T> backgroundPath;// 背景图地址
    public String shareUrl;// 分享图地址

}
