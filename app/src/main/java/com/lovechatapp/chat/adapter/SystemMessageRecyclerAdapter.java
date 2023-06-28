package com.lovechatapp.chat.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.SystemMessageBean;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：系统消息RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/25
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SystemMessageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<SystemMessageBean> mBeans = new ArrayList<>();

    public SystemMessageRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<SystemMessageBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_system_message_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SystemMessageBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {

            //时间
            String time = bean.t_create_time;
            if (!TextUtils.isEmpty(time)) {
                mHolder.mTimeTv.setText(time);
            }

            //内容
            String content = bean.t_message_content;
            if (!TextUtils.isEmpty(content)) {
                mHolder.mContentTv.setText(content);
            }

            //图片
            Glide.with(mHolder.contentIv)
                    .load(bean.t_message_url)
                    .error(0)
                    .into(mHolder.contentIv);
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTimeTv;
        TextView mTitleTv;
        TextView mContentTv;
        ImageView contentIv;

        MyViewHolder(View itemView) {
            super(itemView);
            contentIv = itemView.findViewById(R.id.content_iv);
            mTimeTv = itemView.findViewById(R.id.time_tv);
            mTitleTv = itemView.findViewById(R.id.title_tv);
            mContentTv = itemView.findViewById(R.id.content_tv);
        }

    }

}
