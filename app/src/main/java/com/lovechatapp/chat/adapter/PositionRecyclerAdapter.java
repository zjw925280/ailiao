package com.lovechatapp.chat.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.PoiBean;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：位置RecyclerView的Adapter
 * 作者：
 * 创建时间：2019/1/8
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PositionRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<PoiBean> mBeans = new ArrayList<>();

    public PositionRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<PoiBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    public String getSelectData() {
        for (PoiBean bean : mBeans) {
            if (bean.isSelected) {
                String content = "";
                if (!TextUtils.isEmpty(bean.title)) {
                    if (!TextUtils.isEmpty(bean.addCity)) {
                        content = bean.addCity + mContext.getResources().getString(R.string.middle_point) + bean.title;
                    } else {
                        content = bean.title;
                    }
                }
                return content;
            }
        }
        return "";
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_position_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final PoiBean bean = mBeans.get(position);
        final int mPosition = position;
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //黑字
            mHolder.mBlackTv.setText(bean.title);
            if (!TextUtils.isEmpty(bean.detail)) {
                mHolder.mGrayTv.setText(bean.detail);
                mHolder.mGrayTv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mGrayTv.setVisibility(View.GONE);
            }
            if (bean.isSelected) {
                mHolder.mBlackTv.setTextColor(mContext.getResources().getColor(R.color.blue_4d00fb));
                mHolder.mGrayTv.setTextColor(mContext.getResources().getColor(R.color.blue_4d00fb));
                mHolder.mGouIv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mBlackTv.setTextColor(mContext.getResources().getColor(R.color.black_333333));
                mHolder.mGrayTv.setTextColor(mContext.getResources().getColor(R.color.gray_868686));
                mHolder.mGouIv.setVisibility(View.GONE);
            }
            mHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.isSelected) {
                        return;
                    }
                    for (int i = 0; i < mBeans.size(); i++) {
                        PoiBean itemBean = mBeans.get(i);
                        itemBean.isSelected = i == mPosition;
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mContentLl;
        TextView mBlackTv;
        TextView mGrayTv;
        ImageView mGouIv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentLl = itemView.findViewById(R.id.content_ll);
            mBlackTv = itemView.findViewById(R.id.black_tv);
            mGrayTv = itemView.findViewById(R.id.gray_tv);
            mGouIv = itemView.findViewById(R.id.gou_iv);
        }
    }

}
