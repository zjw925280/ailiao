package com.lovechatapp.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.GiftBean;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：礼物8个item的recyclerView的adapter
 * 作者：
 * 创建时间：2018/10/26
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class GiftGridRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<GiftBean> mBeans = new ArrayList<>();
    private int mItemPosition;//是哪一个item

    GiftGridRecyclerAdapter(Context context, int itemPosition) {
        mContext = context;
        mItemPosition = itemPosition;
    }

    public void loadData(List<GiftBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_gift_grid_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final GiftBean bean = mBeans.get(position);
        final int mPosition = position;
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //图片
            String imgUrl = bean.t_gift_still_url;
            if (!TextUtils.isEmpty(imgUrl)) {
                int width = DevicesUtil.dp2px(mContext, 46);
                int height = DevicesUtil.dp2px(mContext, 46);
                ImageLoadHelper.glideShowImageWithUrl(mContext, imgUrl, mHolder.mGiftIv, width, height);
            }
            //名
            if (!TextUtils.isEmpty(bean.t_gift_name)) {
                mHolder.mNameTv.setText(bean.t_gift_name);
            }
            //数量
            int number = bean.t_gift_gold;
            String content = number + mContext.getResources().getString(R.string.gold);
            mHolder.mGoldTv.setText(content);
            //是否选中
            if (bean.isSelected) {
                mHolder.mRectV.setVisibility(View.VISIBLE);
            } else {
                mHolder.mRectV.setVisibility(View.GONE);
            }
            //点击事件
            mHolder.mContentRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!bean.isSelected) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(mPosition, mItemPosition);
                        }
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

        View mContentRl;
        ImageView mGiftIv;
        TextView mNameTv;
        TextView mGoldTv;
        View mRectV;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentRl = itemView.findViewById(R.id.content_rl);
            mGiftIv = itemView.findViewById(R.id.gift_iv);
            mNameTv = itemView.findViewById(R.id.name_tv);
            mGoldTv = itemView.findViewById(R.id.gold_tv);
            mRectV = itemView.findViewById(R.id.rect_v);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, int itemPosition);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}
