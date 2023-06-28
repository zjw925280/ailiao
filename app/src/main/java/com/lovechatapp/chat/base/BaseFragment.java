package com.lovechatapp.chat.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.view.tab.TabPagerViewHolder;

/**
 * 懒加载
 */
public abstract class BaseFragment extends LazyFragment {

    public BaseActivity mContext;
    protected boolean isSelected;
    protected boolean isParentShowed;
    protected boolean isResume;
    protected TabPagerViewHolder tabPagerViewHolder;

    public BaseFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = (BaseActivity) getActivity();
        View view = LayoutInflater.from(getContext()).inflate(initLayout(), container, false);
        initView(view, savedInstanceState);
        mIsViewPrepared = true;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Fragment fragment = getParentFragment();
        isParentShowed = fragment == null;
        if (!isParentShowed) {
            if (fragment instanceof BaseFragment) {
                isParentShowed = ((BaseFragment) fragment).isShowing();
            } else {
                isParentShowed = fragment.isVisible();
            }
        }
    }

    protected <T extends View> T findViewById(int id) {
        if (getView() == null) {
            return null;
        }
        return getView().findViewById(id);
    }

    /**
     * 初始化layout
     */
    protected abstract int initLayout();

    /**
     * 初始化view
     */
    protected void initView(View view, Bundle savedInstanceState) {

    }

    /**
     * 第一次可见的操作
     */
    protected void onFirstVisible() {

    }

    @Override
    protected void onFirstVisibleToUser() {
        onFirstVisible();
        mIsDataLoadCompleted = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.e("ralph", "fragment name ======" + getClass().getName());
        setShowed(isParentShowed, isSelected, true);
    }

    @Override
    public void onPause() {
        super.onPause();
        setShowed(isParentShowed, isSelected, false);
    }

    public final void setParentSelected(boolean b) {
        setShowed(b, isSelected, isResume);
    }

    public final void setSelected(boolean selected) {
        setShowed(isParentShowed, selected, isResume);
    }

    public final void setShowed(boolean parentShowed, boolean selected, boolean resume) {
        boolean isChanged = (isSelected && isParentShowed && isResume) != (parentShowed && selected && resume);
        isParentShowed = parentShowed;
        isSelected = selected;
        isResume = resume;
        if (isChanged) {
            if (isAdded()) {
                for (Fragment fragment : getChildFragmentManager().getFragments()) {
                    if (fragment instanceof BaseFragment) {
                        BaseFragment childFragment = (BaseFragment) fragment;
                        childFragment.setParentSelected(isShowing());
                    }
                }
            }
            showChanged(isShowing());
        }
    }

    public final boolean isShowing() {
        return isParentShowed && isSelected && isResume;
    }

    protected void showChanged(boolean b) {

    }

    public void bindTab(TabPagerViewHolder viewHolder) {
        tabPagerViewHolder = viewHolder;
    }

}