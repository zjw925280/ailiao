package com.lovechatapp.chat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.ActorEarnDetailListBean;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播贡献详情RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActorEarnDetailRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<ActorEarnDetailListBean> mBeans = new ArrayList<>();

    public ActorEarnDetailRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<ActorEarnDetailListBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_actor_earn_detail_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ActorEarnDetailListBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //约豆
            String gold = bean.totalGold + mContext.getResources().getString(R.string.gold);
            mHolder.mGoldTv.setText(gold);
            //时间
            String time = bean.t_change_time;
            if (!TextUtils.isEmpty(time)) {
                mHolder.mTimeTv.setText(time);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mGoldTv;
        TextView mTimeTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mGoldTv = itemView.findViewById(R.id.gold_tv);
            mTimeTv = itemView.findViewById(R.id.time_tv);
        }
    }

}
