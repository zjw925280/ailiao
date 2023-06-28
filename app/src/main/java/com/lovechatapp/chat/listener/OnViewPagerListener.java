package com.lovechatapp.chat.listener;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：用于ViewPagerLayoutManager的监听
 * 作者：钉某人
 * 创建时间：2018/6/26
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public interface OnViewPagerListener {

    /*初始化完成*/
    void onInitComplete();

    /*释放的监听*/
    void onPageRelease(boolean isNext, int position);

    /*选中的监听以及判断是否滑动到底部*/
    void onPageSelected(int position, boolean isBottom);


}
