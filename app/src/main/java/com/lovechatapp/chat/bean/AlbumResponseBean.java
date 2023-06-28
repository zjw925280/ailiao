package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：个人资料相册列表页面外部bean
 * 作者：
 * 创建时间：2018/10/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AlbumResponseBean<T> extends BaseBean {

    public int pageCount;
    public List<T> data;
    public int imgTotal;//照片总数
    public int videoTotal;//视频总数

}