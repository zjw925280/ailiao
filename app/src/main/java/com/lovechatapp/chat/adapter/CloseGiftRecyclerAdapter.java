package com.lovechatapp.chat.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.IntimateDetailBean;
import com.lovechatapp.chat.helper.ImageLoadHelper;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：亲密榜 礼物榜RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CloseGiftRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<IntimateDetailBean> mBeans = new ArrayList<>();
    private int mType;//0 亲密 1 礼物

    public CloseGiftRecyclerAdapter(Activity context, int type) {
        mContext = context;
        mType = type;
    }

    public void loadData(List<IntimateDetailBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_close_gift_recycler_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final IntimateDetailBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            String imageUrl;
            //亲密
            if (mType == 0) {
                imageUrl = bean.t_handImg;
            } else {
                imageUrl = bean.t_gift_still_url;
            }
            if (!TextUtils.isEmpty(imageUrl)) {
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, imageUrl, mHolder.mContentIv);
            } else {
                mHolder.mContentIv.setImageResource(R.drawable.default_head_img);
            }
            mHolder.mContentIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mType);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mContentIv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentIv = itemView.findViewById(R.id.content_iv);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int type);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }


}
