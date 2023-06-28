package com.lovechatapp.chat.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.ActorEarnDetailActivity;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.CompanyBean;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：我的主播RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class MyActorRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<CompanyBean> mBeans = new ArrayList<>();

    public MyActorRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<CompanyBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_my_actor_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final CompanyBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //头像
            String handImg = bean.t_handImg;
            if (!TextUtils.isEmpty(handImg)) {
                int width = DevicesUtil.dp2px(mContext, 51);
                int high = DevicesUtil.dp2px(mContext, 51);
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, handImg, mHolder.mHeaderIv, width, high);
            } else {
                mHolder.mHeaderIv.setImageResource(R.drawable.default_head_img);
            }
            //nick
            String nick = bean.t_nickName;
            if (!TextUtils.isEmpty(nick)) {
                mHolder.mNickTv.setText(nick);
            }
            //贡献
            String content = mContext.getResources().getString(R.string.earn_gold_des) + bean.totalGold;
            mHolder.mGoldTv.setText(content);
            //点击事件
            mHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ActorEarnDetailActivity.class);
                    intent.putExtra(Constant.ACTOR_ID, bean.t_anchor_id);
                    mContext.startActivity(intent);
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
        ImageView mHeaderIv;
        TextView mNickTv;
        TextView mGoldTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentLl = itemView.findViewById(R.id.content_ll);
            mHeaderIv = itemView.findViewById(R.id.header_iv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mGoldTv = itemView.findViewById(R.id.gold_tv);
        }
    }

}
