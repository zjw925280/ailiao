package com.lovechatapp.chat.base;

import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述： 基本列表返回数据类型
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class BaseListResponse<T> extends BaseBean {

    public int m_istatus;
    public String m_strMessage;
    public List<T> m_object;

}
