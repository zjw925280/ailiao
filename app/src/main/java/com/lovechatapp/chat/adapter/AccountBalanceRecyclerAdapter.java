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
import com.lovechatapp.chat.bean.AccountBalanceBean;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：账户余额RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/10/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AccountBalanceRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<AccountBalanceBean> mBeans = new ArrayList<>();

    public AccountBalanceRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<AccountBalanceBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_account_balance_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AccountBalanceBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //描述
            if (!TextUtils.isEmpty(bean.detail)) {
                mHolder.mTitleTv.setText(bean.detail);
            }
            //时间
            if (!TextUtils.isEmpty(bean.tTime)) {
                mHolder.mTimeTv.setText(bean.tTime);
            }
            //金币
            //-1:支出 1:收益 VIP为RMB
            int profitAndPay = bean.profitAndPay;
            int t_value = bean.t_value;
            int type = bean.t_change_category;
            if (type == 8) {//VIP
                String gold = mContext.getResources().getString(R.string.sub) + t_value + mContext.getResources().getString(R.string.rmb_des);
                mHolder.mGoldTv.setText(gold);
                mHolder.mGoldTv.setTextColor(mContext.getResources().getColor(R.color.black_3f3b48));
            } else {
                if (profitAndPay == 1) {//1:收益 +
                    String gold = mContext.getResources().getString(R.string.add) + t_value;
                    mHolder.mGoldTv.setText(gold);
                    mHolder.mGoldTv.setTextColor(mContext.getResources().getColor(R.color.red_fe2947));
                } else {
                    String gold = mContext.getResources().getString(R.string.sub) + t_value;
                    mHolder.mGoldTv.setText(gold);
                    mHolder.mGoldTv.setTextColor(mContext.getResources().getColor(R.color.black_3f3b48));
                }
            }
            //头像 0.充值  1.聊天  2.视频  3.私密照片  4.私密视频  5.查看手机  6.查看微信  7.红包
            // 8.VIP   9.礼物  10.提现  11.推荐分成 12.提现失败原路退回  13.注册赠送  14.公会收入
            String headImg = bean.t_handImg;
            if (type == 0 || type == 8 || type == 13) {//显示系统
                if (type == 8) {//VIP
                    mHolder.mHeadIv.setImageResource(R.drawable.vip_account);
                } else {
                    mHolder.mHeadIv.setImageResource(R.drawable.system_account);
                }
            } else {
                if (!TextUtils.isEmpty(headImg)) {
                    int width = DevicesUtil.dp2px(mContext, 34);
                    int height = DevicesUtil.dp2px(mContext, 34);
                    ImageLoadHelper.glideShowCircleImageWithUrl(mContext, headImg, mHolder.mHeadIv, width, height);
                } else {
                    mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
                }
            }

        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mHeadIv;
        TextView mGoldTv;
        TextView mTitleTv;
        TextView mTimeTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mGoldTv = itemView.findViewById(R.id.gold_tv);
            mTitleTv = itemView.findViewById(R.id.title_tv);
            mTimeTv = itemView.findViewById(R.id.time_tv);
        }
    }

}
