package com.lovechatapp.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.VipBean;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：获取Vip的Dialog的每月钱数RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class VipMoneyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<VipBean> mBeans = new ArrayList<>();
    private VipBean mSelectBean;

    public VipMoneyRecyclerAdapter(Context context) {
        mContext = context;
    }

    public void loadData(List<VipBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_vip_money_recyler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VipBean bean = mBeans.get(position);
        final int mPosition = position;
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //月数
            mHolder.mMonthBigTv.setText(String.valueOf(bean.t_duration));
            //套餐名
            mHolder.mMonthSmallTv.setText(bean.t_setmeal_name);
            //钱  ¥ 12/个月
            String money = mContext.getResources().getString(R.string.month_every_one) + bean.t_money;
            //原价
            String old = mContext.getString(R.string.old_price)+ String.valueOf(bean.t_cost_price);
            mHolder.mOldTv.setText(old);
            mHolder.mRealMoneyTv.setText(money);
            if (bean.isSelected) {
                mHolder.mOpenTv.setSelected(true);
                mSelectBean = bean;
            } else {
                mHolder.mOpenTv.setSelected(false);
            }
            mHolder.mContentRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mBeans.size(); i++) {
                        if (i == mPosition) {
                            VipBean bean = mBeans.get(i);
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

        View mContentRl;
        TextView mMonthBigTv;
        TextView mMonthSmallTv;
        TextView mOldTv;
        TextView mRealMoneyTv;
        TextView mOpenTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentRl = itemView.findViewById(R.id.content_rl);
            mMonthBigTv = itemView.findViewById(R.id.month_big_tv);
            mMonthSmallTv = itemView.findViewById(R.id.month_small_tv);
            mOldTv = itemView.findViewById(R.id.old_tv);
            mRealMoneyTv = itemView.findViewById(R.id.real_money_tv);
            mOpenTv = itemView.findViewById(R.id.open_tv);
        }
    }

    public VipBean getSelectBean() {
        return mSelectBean;
    }

}
