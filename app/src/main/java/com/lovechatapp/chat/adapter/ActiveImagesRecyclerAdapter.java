package com.lovechatapp.chat.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.ActiveFileBean;
import com.lovechatapp.chat.glide.GlideRoundTransform;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：粉丝RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/8/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActiveImagesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;

    private List<ActiveFileBean> mBeans = new ArrayList<>();

    private int actorId;

    ActiveImagesRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<ActiveFileBean> beans, int actorId) {
        mBeans = beans;
        this.actorId = actorId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_active_image_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ActiveFileBean bean = mBeans.get(position);

        MyViewHolder mHolder = (MyViewHolder) holder;

        if (bean != null) {

            boolean needPay = bean.judgePrivate(actorId);
            mHolder.mLockIv.setVisibility(needPay ? View.VISIBLE : View.GONE);
            loadImg(needPay,
                    bean.t_file_url,
                    DevicesUtil.dp2px(mContext, 83),
                    DevicesUtil.dp2px(mContext, 83),
                    mHolder.mContentIv);

            mHolder.mContentFl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnImageItemClickListener != null) {
                        mOnImageItemClickListener.onImageItemClick(position, bean);
                    }
                }
            });
        }
    }

    private void loadImg(boolean fade, String url, int overWidth, int overHeight, ImageView imageView) {
        if (fade) {
            Glide.with(mContext)
                    .load(url)
                    .error(R.drawable.default_back)
                    .override(overWidth, overHeight)
                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                    .transform(
                            new GlideRoundTransform(6),
                            new CenterCrop(),
                            new BlurTransformation(100, 2))
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(url)
                    .error(R.drawable.default_back)
                    .override(overWidth, overHeight)
                    .transform(
                            new GlideRoundTransform(6),
                            new CenterCrop())
                    .centerCrop()
                    .into(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mContentIv;
        ImageView mLockIv;
        FrameLayout mContentFl;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentIv = itemView.findViewById(R.id.content_iv);
            mLockIv = itemView.findViewById(R.id.lock_iv);
            mContentFl = itemView.findViewById(R.id.content_fl);
        }
    }

    public interface OnImageItemClickListener {
        void onImageItemClick(int position, ActiveFileBean bean);
    }

    private OnImageItemClickListener mOnImageItemClickListener;

    void setOnImageItemClickListener(OnImageItemClickListener onImageItemClickListener) {
        mOnImageItemClickListener = onImageItemClickListener;
    }

}