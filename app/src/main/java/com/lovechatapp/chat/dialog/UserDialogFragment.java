package com.lovechatapp.chat.dialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.SendBean;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.fragment.OnLineFragment;
import com.lovechatapp.chat.fragment.TotalSendFragment;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：大房间列表Dialog
 * 作者：
 * 创建时间：2018/10/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UserDialogFragment extends DialogFragment implements View.OnClickListener {

    public UserDialogFragment() {

    }

    //贡献值
    TextView mTotalTv;
    TextView mTotalBigTv;
    View mTotalV;
    //在线观众
    TextView mOnlineTv;
    TextView mOnlineBigTv;
    View mOnlineV;
    //ViewPager
    ViewPager mContentVp;

    private final int TOTAL = 0;
    private final int ONLINE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_online_user_layout, container, false);
        initView(view);
        return view;
    }

    /**
     * 初始化
     */
    private void initView(View view) {
        //贡献值
        FrameLayout mTotalFl = view.findViewById(R.id.total_fl);
        mTotalTv = view.findViewById(R.id.total_tv);
        mTotalBigTv = view.findViewById(R.id.total_big_tv);
        mTotalV = view.findViewById(R.id.total_v);
        //在线观众
        FrameLayout mOnlineFl = view.findViewById(R.id.online_fl);
        mOnlineTv = view.findViewById(R.id.online_tv);
        mOnlineBigTv = view.findViewById(R.id.online_big_tv);
        mOnlineV = view.findViewById(R.id.online_v);
        //ViewPager
        mContentVp = view.findViewById(R.id.content_vp);

        mTotalFl.setOnClickListener(this);
        mOnlineFl.setOnClickListener(this);

        //数据
        Bundle bundle = getArguments();
        TotalSendFragment totalSendFragment = new TotalSendFragment();
        totalSendFragment.setArguments(bundle);
        OnLineFragment onLineFragment = new OnLineFragment();
        onLineFragment.setArguments(bundle);

        totalSendFragment.setOnItemClickListener(new TotalSendFragment.OnItemClickListener() {
            @Override
            public void onItemClick(SendBean bean) {
                Bundle bundleClick = new Bundle();
                bundleClick.putInt(Constant.ACTOR_ID, bean.t_id);
                UserInfoDialogFragment userDialog = new UserInfoDialogFragment();
                userDialog.setArguments(bundleClick);
                if (getFragmentManager() != null) {
                    userDialog.show(getFragmentManager(), "tag");
                }
                dismiss();
            }
        });
        onLineFragment.setOnItemClickListener(new OnLineFragment.OnItemClickListener() {
            @Override
            public void onItemClick(SendBean bean) {
                Bundle bundleClick = new Bundle();
                bundleClick.putInt(Constant.ACTOR_ID, bean.t_id);
                UserInfoDialogFragment userDialog = new UserInfoDialogFragment();
                userDialog.setArguments(bundleClick);
                if (getFragmentManager() != null) {
                    userDialog.show(getFragmentManager(), "tag");
                }
                dismiss();
            }
        });

        final List<Fragment> list = new ArrayList<>();
        list.add(0, totalSendFragment);
        list.add(1, onLineFragment);

        mContentVp.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        });
        mContentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchSelect(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mContentVp.setOffscreenPageLimit(2);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            // 一定要设置Background，如果不设置，window属性设置无效
            window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
            DisplayMetrics dm = new DisplayMetrics();
            if (getActivity() != null) {
                WindowManager windowManager = getActivity().getWindowManager();
                if (windowManager != null) {
                    windowManager.getDefaultDisplay().getMetrics(dm);
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.gravity = Gravity.BOTTOM;
                    // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    window.setAttributes(params);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.total_fl: { //贡献值
                switchSelect(TOTAL, false);
                break;
            }
            case R.id.online_fl: { //在线观众
                switchSelect(ONLINE, false);
                break;
            }
        }
    }

    /**
     * 切换
     */
    private void switchSelect(int position, boolean fromViewPager) {
        if (position == TOTAL) {//贡献值
            if (mTotalV.getVisibility() == VISIBLE) {
                return;
            }

            if (!fromViewPager) {
                mContentVp.setCurrentItem(TOTAL);
            }

            mTotalTv.setVisibility(GONE);
            mTotalBigTv.setVisibility(VISIBLE);
            mTotalV.setVisibility(VISIBLE);

            mOnlineTv.setVisibility(VISIBLE);
            mOnlineBigTv.setVisibility(GONE);
            mOnlineV.setVisibility(GONE);
        } else if (position == ONLINE) {//在线
            if (mOnlineV.getVisibility() == VISIBLE) {
                return;
            }

            if (!fromViewPager) {
                mContentVp.setCurrentItem(ONLINE);
            }

            mOnlineTv.setVisibility(GONE);
            mOnlineBigTv.setVisibility(VISIBLE);
            mOnlineV.setVisibility(VISIBLE);

            mTotalTv.setVisibility(VISIBLE);
            mTotalBigTv.setVisibility(GONE);
            mTotalV.setVisibility(GONE);
        }
    }

}
