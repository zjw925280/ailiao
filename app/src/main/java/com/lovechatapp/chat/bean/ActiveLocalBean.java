package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：发布动态本地文件页面
 * 作者：
 * 创建时间：2018/12/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActiveLocalBean extends BaseBean {

    public String localPath;
    public int gold = 0;
    public int type = 0;//文件类型 0: 图片 1: 视频
    public String imageUrl;//文件地址  图片地址

}
