package com.lovechatapp.chat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.HelpCenterBean;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述  帮助中心RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/8/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class HelpCenterRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<HelpCenterBean> mBeans = new ArrayList<>();

    public HelpCenterRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<HelpCenterBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_help_center_recycler_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final HelpCenterBean bean = mBeans.get(position);
        final MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //标题
            mHolder.mTitleTv.setText(bean.t_title);
            //内容
            mHolder.mContentTv.setText(bean.t_content);
            //点击事件
            mHolder.mTitleRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mHolder.mContentTv.getVisibility() != View.VISIBLE) {
                        mHolder.mArrowIv.setSelected(true);
                        mHolder.mContentTv.setVisibility(View.VISIBLE);
                    } else {
                        mHolder.mArrowIv.setSelected(false);
                        mHolder.mContentTv.setVisibility(View.GONE);
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

        ImageView mArrowIv;
        TextView mTitleTv;
        TextView mContentTv;
        RelativeLayout mTitleRl;

        MyViewHolder(View itemView) {
            super(itemView);
            mArrowIv = itemView.findViewById(R.id.arrow_iv);
            mTitleTv = itemView.findViewById(R.id.title_tv);
            mContentTv = itemView.findViewById(R.id.content_tv);
            mTitleRl = itemView.findViewById(R.id.title_rl);
        }
    }

}
