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
import com.lovechatapp.chat.activity.PersonInfoActivity;
import com.lovechatapp.chat.bean.FocusBean;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：关注RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/20
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class FocusRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<FocusBean> mBeans = new ArrayList<>();

    public FocusRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<FocusBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_focus_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final FocusBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            if (!TextUtils.isEmpty(bean.t_nickName)) {
                mHolder.mNameTv.setText(bean.t_nickName);
            }
            //头像
            int smallOverWidth = DevicesUtil.dp2px(mContext, 61);
            int smallOverHeight = DevicesUtil.dp2px(mContext, 61);
            if (!TextUtils.isEmpty(bean.t_handImg)) {
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, bean.t_handImg, mHolder.mHeadIv,
                        smallOverWidth, smallOverHeight);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            //个性签名
            if (!TextUtils.isEmpty(bean.t_autograph)) {
                mHolder.mSignTv.setText(bean.t_autograph);
            } else {
                mHolder.mSignTv.setText(mContext.getResources().getString(R.string.lazy));
            }
            //状态  0.空闲 1.忙碌 2.离线
            int state = bean.t_state;
            if (state == 0) {
                mHolder.mOnlineTv.setVisibility(View.VISIBLE);
                mHolder.mOfflineTv.setVisibility(View.GONE);
                mHolder.mBusyTv.setVisibility(View.GONE);
            } else if (state == 1) {
                mHolder.mBusyTv.setVisibility(View.VISIBLE);
                mHolder.mOnlineTv.setVisibility(View.GONE);
                mHolder.mOfflineTv.setVisibility(View.GONE);
            } else if (state == 2) {
                mHolder.mOfflineTv.setVisibility(View.VISIBLE);
                mHolder.mBusyTv.setVisibility(View.GONE);
                mHolder.mOnlineTv.setVisibility(View.GONE);
            } else {
                mHolder.mOfflineTv.setVisibility(View.GONE);
                mHolder.mBusyTv.setVisibility(View.GONE);
                mHolder.mOnlineTv.setVisibility(View.GONE);
            }
            //点击事件
            mHolder.mContentRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PersonInfoActivity.start(mContext, bean.t_id);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        View mContentRl;
        ImageView mHeadIv;
        TextView mNameTv;
        TextView mSignTv;
        TextView mOfflineTv;
        TextView mOnlineTv;
        TextView mBusyTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentRl = itemView.findViewById(R.id.content_rl);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNameTv = itemView.findViewById(R.id.name_tv);
            mSignTv = itemView.findViewById(R.id.sign_tv);
            mOfflineTv = itemView.findViewById(R.id.offline_tv);
            mOnlineTv = itemView.findViewById(R.id.online_tv);
            mBusyTv = itemView.findViewById(R.id.busy_tv);
        }

    }

}
