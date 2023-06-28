package com.lovechatapp.chat.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.BankBean;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述:  银行列表RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/9/17
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class BankListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<BankBean> mBeans = new ArrayList<>();

    public BankListRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<BankBean> beans) {
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
        BankBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            mHolder.mContentTv.setText(bean.bankName);
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
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DevicesUtil.getScreenW(mContext),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mContentLl.setLayoutParams(params);
            mContentTv = itemView.findViewById(R.id.content_tv);
        }

    }


}
