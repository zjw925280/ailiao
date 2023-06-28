package com.lovechatapp.chat.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述： Fragment基类  懒加载
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public abstract class LazyFragment extends Fragment {

    public boolean mIsVisibleToUser;//是否可见
    public boolean mIsViewPrepared;//判断view是否初始化完成
    protected boolean mIsDataLoadCompleted;//数据是否加载完成
    private boolean mIsFirstVisibleToUser = true;

    // 该方法只有在ViewPager与Fragment结合使用的时候才会执行
    // 该方法在onCreateView之前调用
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            if (mIsFirstVisibleToUser && mIsViewPrepared && !mIsDataLoadCompleted) {
                mIsFirstVisibleToUser = false;
                onFirstVisibleToUser();
            }
        } else {
            if (mIsViewPrepared && mIsDataLoadCompleted) {
                onFragmentNotVisible();
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()) {
            onFirstVisibleToUser();
        }
    }

    /**
     * 做懒加载的地方
     */
    protected abstract void onFirstVisibleToUser();

    /**
     * 当Fragment不可见的时候,view初始化已经完成,数据已经加载完毕
     */
    protected void onFragmentNotVisible() {

    }
}
