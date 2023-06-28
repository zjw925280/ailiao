package com.lovechatapp.chat.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.view.FullScreenVideoView;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：速配用户RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/20
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UserRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<String> mBeans = new ArrayList<>();
    private int[] mImgs = {};
    private int[] mVideos = {};

    public UserRecyclerAdapter(Context context) {
        mContext = context;
    }

    public void loadData(List<String> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_quick_user_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String bean = mBeans.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.mTitleTv.setText(bean);
        ((MyViewHolder) holder).mThumbIv.setImageResource(mImgs[position % 2]);
        ((MyViewHolder) holder).mVideoView.setVideoURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + mVideos[position % 2]));
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        FullScreenVideoView mVideoView;
        ImageView mThumbIv;
        TextView mTitleTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mVideoView = itemView.findViewById(R.id.video_view);
            mThumbIv = itemView.findViewById(R.id.thumb_iv);
            mTitleTv = itemView.findViewById(R.id.title_tv);
        }

    }

}
