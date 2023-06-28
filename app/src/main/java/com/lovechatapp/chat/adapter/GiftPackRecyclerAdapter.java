package com.lovechatapp.chat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.GiftPackBean;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：礼物柜RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/10/26
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class GiftPackRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<GiftPackBean> mBeans = new ArrayList<>();

    public GiftPackRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<GiftPackBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_gift_pack_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final GiftPackBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //图片
            String imgUrl = bean.t_gift_still_url;
            if (!TextUtils.isEmpty(imgUrl)) {
                int width = DevicesUtil.dp2px(mContext, 65);
                int height = DevicesUtil.dp2px(mContext, 65);
                ImageLoadHelper.glideShowImageWithUrl(mContext, imgUrl, mHolder.mGiftIv, width, height);
            }
            //名
            if (!TextUtils.isEmpty(bean.t_gift_name)) {
                mHolder.mNameTv.setText(bean.t_gift_name);
            }
            //数量
            int number = bean.totalCount;
            String content = mContext.getResources().getString(R.string.multi) + number;
            mHolder.mNumberTv.setText(content);
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView mGiftIv;
        private TextView mNameTv;
        private TextView mNumberTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mGiftIv = itemView.findViewById(R.id.gift_iv);
            mNameTv = itemView.findViewById(R.id.name_tv);
            mNumberTv = itemView.findViewById(R.id.number_tv);
        }
    }

}
