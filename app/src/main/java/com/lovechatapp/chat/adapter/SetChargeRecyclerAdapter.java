package com.lovechatapp.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
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
 * 功能描述:  设置收费项目RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/30
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SetChargeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<String> mBeans = new ArrayList<>();

    public SetChargeRecyclerAdapter(Context context) {
        mContext = context;
    }

    public void loadData(List<String> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_set_charge_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String bean = mBeans.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.mContentTv.setText(bean);
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
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DevicesUtil.getScreenW(mContext),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mContentLl.setLayoutParams(params);
            mContentTv = itemView.findViewById(R.id.content_tv);
        }

    }


}
