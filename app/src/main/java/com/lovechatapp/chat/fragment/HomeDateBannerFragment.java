package com.lovechatapp.chat.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.view.banner.HeaderBanner;
import com.lovechatapp.chat.view.recycle.ListTypeAdapter;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 首页推荐
 */
public class HomeDateBannerFragment extends HomeDateContentFragment {

    Unbinder unbinder;

    /**
     * 轮播广告
     */
    private HeaderBanner headerBanner;

    @Override
    protected int initLayout() {
        return R.layout.fragment_home_banner;
    }

    @Override
    protected void showChanged(boolean b) {
        headerBanner.loop(getActivity(), b);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected ListTypeAdapter.BindViewHolder createHeader() {
        return headerBanner = new HeaderBanner(getActivity());
    }

}