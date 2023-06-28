package com.lovechatapp.chat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.ShareArticleBean;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：分享列表RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/8/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ShareArticleRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<ShareArticleBean> mBeans = new ArrayList<>();

    public ShareArticleRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<ShareArticleBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_share_article_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ShareArticleBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //图片
            mHolder.mImageIv.setImageResource(bean.resourceId);
            //标题
            mHolder.mTitleTv.setText(bean.title);
            //描述
            mHolder.mTimeTv.setText(bean.des);
            mHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemShareClickListener != null) {
                        mOnItemShareClickListener.onItemShareClick(bean);
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

        View mContentLl;
        ImageView mImageIv;
        TextView mTitleTv;
        TextView mTimeTv;
        TextView mShareTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentLl = itemView.findViewById(R.id.content_ll);
            mImageIv = itemView.findViewById(R.id.image_iv);
            mTitleTv = itemView.findViewById(R.id.title_tv);
            mTimeTv = itemView.findViewById(R.id.time_tv);
            mShareTv = itemView.findViewById(R.id.share_tv);
        }
    }

    public interface OnItemShareClickListener {
        void onItemShareClick(ShareArticleBean browserBean);
    }

    private OnItemShareClickListener mOnItemShareClickListener;

    public void setOnItemShareClickListener(OnItemShareClickListener onItemShareClickListener) {
        mOnItemShareClickListener = onItemShareClickListener;
    }

}
