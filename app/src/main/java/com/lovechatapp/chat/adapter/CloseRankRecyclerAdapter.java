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

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.CloseBean;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：亲密榜RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/10/26
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CloseRankRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<CloseBean> mBeans = new ArrayList<>();

    public CloseRankRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<CloseBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_close_rank_recycler_layout,
                parent, false);
        return new ContentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final CloseBean bean = mBeans.get(position);
        ContentViewHolder mHolder = (ContentViewHolder) holder;
        if (bean != null) {
            //排名
            if (position == 0) {//第一名
                mHolder.mRankIv.setVisibility(View.VISIBLE);
                mHolder.mRankTv.setVisibility(View.GONE);
                mHolder.mRankIv.setBackgroundResource(R.drawable.close_rank_first);
            } else if (position == 1) {//第二名
                mHolder.mRankIv.setVisibility(View.VISIBLE);
                mHolder.mRankTv.setVisibility(View.GONE);
                mHolder.mRankIv.setBackgroundResource(R.drawable.close_rank_second);
            } else if (position == 2) {//第三名
                mHolder.mRankIv.setVisibility(View.VISIBLE);
                mHolder.mRankTv.setVisibility(View.GONE);
                mHolder.mRankIv.setBackgroundResource(R.drawable.close_rank_third);
            } else {//其他
                mHolder.mRankIv.setVisibility(View.GONE);
                mHolder.mRankTv.setVisibility(View.VISIBLE);
                mHolder.mRankTv.setText(String.valueOf(position + 1));
            }
            //头像
            if (!TextUtils.isEmpty(bean.t_handImg)) {
                int width = DevicesUtil.dp2px(mContext, 40);
                int high = DevicesUtil.dp2px(mContext, 40);
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, bean.t_handImg,
                        mHolder.mHeadIv, width, high);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            //昵称
            if (!TextUtils.isEmpty(bean.t_nickName)) {
                mHolder.mNickTv.setText(bean.t_nickName);
            }
            //金币档
            int goldLevel = bean.grade;
            if (goldLevel == 1) {
                mHolder.mLevelIv.setBackgroundResource(R.drawable.gold_one);
            } else if (goldLevel == 2) {
                mHolder.mLevelIv.setBackgroundResource(R.drawable.gold_two);
            } else if (goldLevel == 3) {
                mHolder.mLevelIv.setBackgroundResource(R.drawable.gold_three);
            } else if (goldLevel == 4) {
                mHolder.mLevelIv.setBackgroundResource(R.drawable.gold_four);
            } else if (goldLevel == 5) {
                mHolder.mLevelIv.setBackgroundResource(R.drawable.gold_five);
            }
            //亲密度
            if (bean.totalGold > 0) {
                String content = mContext.getResources().getString(R.string.close_des) + bean.totalGold;
                mHolder.mCloseTv.setText(content);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    private static class ContentViewHolder extends RecyclerView.ViewHolder {

        private ImageView mRankIv;//1 2 3名
        private TextView mRankTv;
        private ImageView mHeadIv;
        private TextView mNickTv;
        private ImageView mLevelIv;
        private TextView mCloseTv;//亲密度

        ContentViewHolder(View itemView) {
            super(itemView);
            mRankIv = itemView.findViewById(R.id.rank_iv);
            mRankTv = itemView.findViewById(R.id.rank_tv);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mLevelIv = itemView.findViewById(R.id.level_iv);
            mCloseTv = itemView.findViewById(R.id.close_tv);
        }
    }

}
