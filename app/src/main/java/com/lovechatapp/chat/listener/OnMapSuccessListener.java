package com.lovechatapp.chat.listener;

import android.view.View;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：地图 Marker加载成功回调
 * 作者：
 * 创建时间：2018/11/21
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public interface OnMapSuccessListener {

    void onResourceReady(View view);

    void onLocalReady(View view);

}
