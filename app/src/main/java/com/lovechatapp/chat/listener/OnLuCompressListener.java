package com.lovechatapp.chat.listener;

import java.io.File;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：鲁班压缩后的回调
 * 作者：
 * 创建时间：2018/6/26
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public interface OnLuCompressListener {

    void onStart();

    void onSuccess(File file);

    void onError(Throwable e);

}
