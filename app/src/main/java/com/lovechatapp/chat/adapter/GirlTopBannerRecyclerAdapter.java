package com.lovechatapp.chat.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.HelpCenterActivity;
import com.lovechatapp.chat.activity.InviteEarnActivity;
import com.lovechatapp.chat.activity.PhoneNaviActivity;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.BannerBean;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：推荐页面上方推荐Banner 的 RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/10/10
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class GirlTopBannerRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<BannerBean> mBannerBeans = new ArrayList<>();

    GirlTopBannerRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    void loadBannerData(List<BannerBean> beans) {
        mBannerBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_top_banner_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BannerBean bean = mBannerBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        //绑定数据
        if (bean != null) {
            //图片
            if (!TextUtils.isEmpty(bean.t_img_url)) {
                //计算 图片resize的大小
                int overWidth = DevicesUtil.dp2px(mContext, 160);
                int overHeight = DevicesUtil.dp2px(mContext, 95);
                ImageLoadHelper.glideShowCornerImageWithUrl(mContext, bean.t_img_url, mHolder.mContentIv,
                        5, overWidth, overHeight);
            }
            //点击
            final String url = bean.t_link_url;
            mHolder.mContentIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(url)) {
                        if (url.contains("http")) {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(url);
                            intent.setData(content_url);
                            mContext.startActivity(intent);
                        } else if (url.contains("InviteEarn")) {//跳转内部
                            Intent intent = new Intent(mContext, InviteEarnActivity.class);
                            mContext.startActivity(intent);
                        } else if (url.contains("PhoneNavi")) {//手机指南
                            Intent intent = new Intent(mContext, PhoneNaviActivity.class);
                            mContext.startActivity(intent);
                        } else if (url.contains("HelpCenter")) {//帮助中心
                            Intent intent = new Intent(mContext, HelpCenterActivity.class);
                            mContext.startActivity(intent);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBannerBeans != null ? mBannerBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mContentIv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentIv = itemView.findViewById(R.id.content_iv);
        }
    }


}
