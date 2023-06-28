package com.lovechatapp.chat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.ActiveCommentBean;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：动态评论RecyclerView的Adapter
 * 作者：
 * 创建时间：2019/1/3
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActiveCommentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<ActiveCommentBean> mBeans = new ArrayList<>();

    public ActiveCommentRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<ActiveCommentBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_active_comment_recycler_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ActiveCommentBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //头像
            String headImg = bean.t_handImg;
            if (!TextUtils.isEmpty(headImg)) {
                int width = DevicesUtil.dp2px(mContext, 40);
                int high = DevicesUtil.dp2px(mContext, 40);
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, headImg, mHolder.mHeadIv, width, high);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            //昵称
            String nick = bean.t_nickName;
            if (!TextUtils.isEmpty(nick)) {
                mHolder.mNickTv.setText(nick);
            }
            //性别 0 女 1 男
            if (bean.t_sex == 0) {
                mHolder.mSexIv.setImageResource(R.drawable.active_sex_women);
            } else if (bean.t_sex == 1) {
                mHolder.mSexIv.setImageResource(R.drawable.active_sex_man);
            }
            //内容
            String content = bean.t_comment;
            if (!TextUtils.isEmpty(content)) {
                mHolder.mContentTv.setText(content);
            }
            //时间
            long time = bean.t_create_time;
            if (time > 0) {
                mHolder.mTimeTv.setText(TimeUtil.getTimeStr(time));
                mHolder.mTimeTv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mTimeTv.setVisibility(View.GONE);
            }
            //删除 评论人类型 1.自己 显示删除 2.其他人评论
            if (bean.comType == 1) {//自己的评论
                mHolder.mDeleteTv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mDeleteTv.setVisibility(View.GONE);
            }
            mHolder.mDeleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnDeleteClickListener != null && bean.comId > 0) {
                        mOnDeleteClickListener.onDeleteClick(String.valueOf(bean.comId));
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

        ImageView mHeadIv;
        TextView mNickTv;
        ImageView mSexIv;
        TextView mContentTv;
        TextView mTimeTv;
        TextView mDeleteTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mSexIv = itemView.findViewById(R.id.sex_iv);
            mContentTv = itemView.findViewById(R.id.content_tv);
            mTimeTv = itemView.findViewById(R.id.time_tv);
            mDeleteTv = itemView.findViewById(R.id.delete_tv);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(String commentId);
    }

    private OnDeleteClickListener mOnDeleteClickListener;

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        mOnDeleteClickListener = onDeleteClickListener;
    }

}
