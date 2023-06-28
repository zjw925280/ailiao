package com.lovechatapp.chat.bean;

import com.lovechatapp.chat.base.BaseBean;

import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：分页用base bean 多了一个monthTotal的字段
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PageBeanOne<T> extends BaseBean {

    public int pageCount;//总页码
    public List<T> data;//数据集
    public int monthTotal;//月汇总

}
