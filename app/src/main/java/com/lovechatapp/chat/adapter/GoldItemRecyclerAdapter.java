package com.lovechatapp.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.ChargeListBean;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：约豆不足RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/9/30
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class GoldItemRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ChargeListBean> mBeans = new ArrayList<>();
    private ChargeListBean mSelectBean;

    public GoldItemRecyclerAdapter(Context context) {
        mContext = context;
    }

    public void loadData(List<ChargeListBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_gold_not_enough_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChargeListBean bean = mBeans.get(position);
        final int mPosition = position;
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //约豆
            String gold = bean.t_gold + mContext.getResources().getString(R.string.gold);
            mHolder.mGoldTv.setText(gold);
            //钱  ¥ 12/个月
            String money = bean.t_money + mContext.getResources().getString(R.string.rmb);
            mHolder.mMoneyTv.setText(money);
            if (bean.isSelected) {
                mHolder.mContentLl.setSelected(true);
                mHolder.mMoneyTv.setSelected(true);
                mHolder.mGoldTv.setSelected(true);
                mSelectBean = bean;
            } else {
                mHolder.mContentLl.setSelected(false);
                mHolder.mMoneyTv.setSelected(false);
                mHolder.mGoldTv.setSelected(false);
            }
            mHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mBeans.size(); i++) {
                        if (i == mPosition) {
                            ChargeListBean bean = mBeans.get(i);
                            bean.isSelected = true;
                        } else {
                            mBeans.get(i).isSelected = false;
                        }
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

        View mContentLl;
        TextView mGoldTv;
        TextView mMoneyTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentLl = itemView.findViewById(R.id.content_ll);
            mGoldTv = itemView.findViewById(R.id.gold_tv);
            mMoneyTv = itemView.findViewById(R.id.money_tv);
        }
    }

    public ChargeListBean getSelectBean() {
        return mSelectBean;
    }

}
