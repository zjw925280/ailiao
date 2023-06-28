package com.lovechatapp.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述:  年月选择器RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/7/9
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class YearPickerRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<String> mBeans = new ArrayList<>();

    public YearPickerRecyclerAdapter(Context context) {
        mContext = context;
    }

    public void loadData(List<String> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_year_picker_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String bean = mBeans.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        if (myViewHolder != null) {
            myViewHolder.mContentTv.setText(bean);
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mContentLl;
        TextView mContentTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentLl = itemView.findViewById(R.id.content_ll);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DevicesUtil.getScreenW(mContext) / 2,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mContentLl.setLayoutParams(params);
            mContentTv = itemView.findViewById(R.id.content_tv);
        }

    }


}
