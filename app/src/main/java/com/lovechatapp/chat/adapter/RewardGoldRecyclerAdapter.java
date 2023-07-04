package com.lovechatapp.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lovechatapp.chat.R;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：接通视频页面打赏约豆RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/22
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class RewardGoldRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<String> mBeans = new ArrayList<>();
    private List<TextView> mGoldTexts = new ArrayList<>();
    private String mSelectBean;

    public RewardGoldRecyclerAdapter(Context context) {
        mContext = context;
    }

    public void loadData(List<String> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    public String getSelectBean() {
        return mSelectBean;
    }

    public void clearSelect() {
        if (mGoldTexts != null && mGoldTexts.size() > 0) {
            for (TextView textView : mGoldTexts) {
                textView.setSelected(false);
            }
        }
        mSelectBean = null;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_reward_gold_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int mPosition = position;
        final String bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        mGoldTexts.add(mHolder.mGoldTv);
        String content = bean + mContext.getResources().getString(R.string.gold);
        mHolder.mGoldTv.setText(content);

        //设置点击事件
        mHolder.mGoldTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoldTexts.size() > 0) {
                    for (int i = 0; i < mGoldTexts.size(); i++) {
                        TextView textView = mGoldTexts.get(i);
                        if (i == mPosition) {
                            if (textView.isSelected()) {
                                textView.setSelected(false);
                            } else {
                                textView.setSelected(true);
                                mSelectBean = bean;
                            }
                        } else {
                            textView.setSelected(false);
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mGoldTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mGoldTv = itemView.findViewById(R.id.gold_tv);
        }
    }

}
