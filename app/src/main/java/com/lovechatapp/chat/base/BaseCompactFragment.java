package com.lovechatapp.chat.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lovechatapp.chat.bean.ChatUserInfo;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;

/**
 * 懒加载
 */
public abstract class BaseCompactFragment extends LazyFragment {

    public Activity mContext;

    public BaseCompactFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        View view = LayoutInflater.from(getContext()).inflate(initLayout(), container, false);
        initView(view, savedInstanceState);
        mIsViewPrepared = true;
        return view;
    }

    /**
     * 初始化layout
     */
    protected abstract int initLayout();

    /**
     * 初始化view
     */
    protected abstract void initView(View view, Bundle savedInstanceState);

    /**
     * 第一次可见的操作
     */
    protected abstract void onFirstVisible();

    @Override
    protected void onFirstVisibleToUser() {
        onFirstVisible();
        mIsDataLoadCompleted = true;
    }

    /**
     * 获取UserId
     */
    public String getUserId() {
        String sUserId = "";
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                int userId = userInfo.t_id;
                if (userId >= 0) {
                    sUserId = String.valueOf(userId);
                }
            } else {
                int id = SharedPreferenceHelper.getAccountInfo(mContext.getApplicationContext()).t_id;
                sUserId = String.valueOf(id);
            }
        }
        return sUserId;
    }

}
