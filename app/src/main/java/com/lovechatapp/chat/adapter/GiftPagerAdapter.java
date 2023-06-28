package com.lovechatapp.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.GiftBean;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述:  礼物ViewPager RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/10/26
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class GiftPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<List<GiftBean>> mBeans = new ArrayList<>();

    public GiftPagerAdapter(Context activity) {
        mContext = activity;
    }

    public void loadData(List<List<GiftBean>> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    public GiftBean getSelectBean() {
        for (int i = 0; i < mBeans.size(); i++) {
            for (int j = 0; j < mBeans.get(i).size(); j++) {
                if (mBeans.get(i).get(j).isSelected) {
                    return mBeans.get(i).get(j);
                }
            }
        }
        return null;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_gift_view_pager_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final List<GiftBean> bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 4);
            mHolder.mContentRv.setLayoutManager(gridLayoutManager);
            GiftGridRecyclerAdapter adapter = new GiftGridRecyclerAdapter(mContext, position);
            mHolder.mContentRv.setAdapter(adapter);
            if (bean.size() > 0) {
                adapter.loadData(bean);
                adapter.setOnItemClickListener(new GiftGridRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int gridPosition, int itemPosition) {
                        for (int i = 0; i < mBeans.size(); i++) {
                            for (int j = 0; j < mBeans.get(i).size(); j++) {
                                mBeans.get(i).get(j).isSelected = i == itemPosition && j == gridPosition;
                            }
                        }
                        selectedGift();
                        notifyDataSetChanged();
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    protected void selectedGift(){

    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        RecyclerView mContentRv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentRv = itemView.findViewById(R.id.content_rv);
        }

    }
}
