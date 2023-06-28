package com.lovechatapp.chat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.AlbumBean;
import com.lovechatapp.chat.glide.GlideRoundTransform;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：首页短视频RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/7/30
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class HomeVideoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<AlbumBean> mBeans = new ArrayList<>();

    protected HomeVideoRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<AlbumBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_home_video_recycler_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AlbumBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        final int mPosition = position;
        if (bean != null) {

            //标题
            mHolder.mTitleTv.setText(bean.t_title);
            mHolder.mTitleTv.setVisibility(!TextUtils.isEmpty(bean.t_title) ? View.VISIBLE : View.GONE);

            //nick
            mHolder.mNickTv.setText(bean.t_nickName);

            mHolder.mLockFl.setVisibility(View.GONE);
            mHolder.mGoldTv.setVisibility(View.GONE);

            //t_is_private 是否私密：0.否1.是
            //is_see 0.未查看1.已查看
            if (!bean.canSee()) {
                mHolder.mLockFl.setVisibility(View.VISIBLE);
                if (bean.t_money > 0) {
                    String content = bean.t_money + mContext.getResources().getString(R.string.gold);
                    mHolder.mGoldTv.setText(content);
                    mHolder.mGoldTv.setVisibility(View.VISIBLE);
                }
                //加载封面
                Glide.with(mContext)
                        .load(bean.t_video_img)
                        .error(R.drawable.default_back)
                        .transform(new CenterCrop(),
                                new BlurTransformation(100, 2),
                                new GlideRoundTransform(2))
                        .into(mHolder.mContentIv);
            } else {
                //加载封面
                Glide.with(mContext)
                        .load(bean.t_video_img)
                        .error(R.drawable.default_back)
                        .transform(new CenterCrop(), new GlideRoundTransform(6))
                        .into(mHolder.mContentIv);
            }

            //点击事件
            mHolder.mContentFl.setOnClickListener(v -> itemClick(mPosition));
        }
    }

    public void itemClick(int position) {

    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        View mContentFl;
        ImageView mContentIv;
        TextView mTitleTv;
        TextView mNickTv;
        TextView mGoldTv;
        FrameLayout mLockFl;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentFl = itemView.findViewById(R.id.content_fl);
            mContentIv = itemView.findViewById(R.id.content_iv);
            mTitleTv = itemView.findViewById(R.id.title_tv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mGoldTv = itemView.findViewById(R.id.gold_tv);
            mLockFl = itemView.findViewById(R.id.lock_fl);
        }
    }

}
