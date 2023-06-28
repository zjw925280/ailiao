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
import com.lovechatapp.chat.bean.TudiBean;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：我的徒弟 徒孙RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/8/21
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class TudiRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<TudiBean> mBeans = new ArrayList<>();

    public TudiRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<TudiBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_tudi_recycler_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final TudiBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //主播昵称
            String nick = bean.t_nickName;
            if (!TextUtils.isEmpty(nick)) {
                mHolder.mNameTv.setText(nick);
            }
            //时间
            String time = bean.t_create_time;
            if (!TextUtils.isEmpty(time)) {
                mHolder.mTimeTv.setText(time);
            } else {
                mHolder.mTimeTv.setText(null);
            }
            //头像
            String headImg = bean.t_handImg;
            if (!TextUtils.isEmpty(headImg)) {
                int width = DevicesUtil.dp2px(mContext, 40);
                int high = DevicesUtil.dp2px(mContext, 40);
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, headImg, mHolder.mHeadIv, width, high);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            //贡献
            String contribute = mContext.getString(R.string.earn_gold_des) + bean.spreadMoney;
            mHolder.mContributeTv.setText(contribute);
            //总充值
            String total = mContext.getString(R.string.total_charge) + bean.totalStorageGold;
            mHolder.mTotalTv.setText(total);
            //余额
            String left = mContext.getString(R.string.left_money) + bean.balance;
            mHolder.mLeftTv.setText(left);
            //是否认证 0.普通用户1.主播
            int t_role = bean.t_role;
            if (t_role == 1) {//主播
                mHolder.mHaveVerifyIv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mHaveVerifyIv.setVisibility(View.GONE);
            }
            mHolder.mContentLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int actorId = bean.t_id;
                    if (actorId > 0) {
                        PersonInfoActivity.start(mContext, actorId);
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
        TextView mNameTv;
        TextView mTimeTv;
        ImageView mHaveVerifyIv;
        View mContentLl;
        TextView mTotalTv;
        TextView mLeftTv;
        TextView mContributeTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.header_iv);
            mNameTv = itemView.findViewById(R.id.name_tv);
            mTimeTv = itemView.findViewById(R.id.time_tv);
            mHaveVerifyIv = itemView.findViewById(R.id.have_verify_iv);
            mContentLl = itemView.findViewById(R.id.content_ll);
            mTotalTv = itemView.findViewById(R.id.total_tv);
            mLeftTv = itemView.findViewById(R.id.left_tv);
            mContributeTv = itemView.findViewById(R.id.contribute_tv);
        }
    }

}
