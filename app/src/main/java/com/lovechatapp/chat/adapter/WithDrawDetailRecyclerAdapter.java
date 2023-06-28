package com.lovechatapp.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.WithDrawDetailBean;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：提现明细RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class WithDrawDetailRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<WithDrawDetailBean> mBeans = new ArrayList<>();

    public WithDrawDetailRecyclerAdapter(Context context) {
        mContext = context;
    }

    public void loadData(List<WithDrawDetailBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_withdraw_detail_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        WithDrawDetailBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //时间
            if (!TextUtils.isEmpty(bean.tTime)) {
                mHolder.mTimeTv.setText(bean.tTime);
            }
            //钱
            String gold = mContext.getResources().getString(R.string.month_every_one) + bean.t_money;
            mHolder.mMoneyTv.setText(gold);
            //方式  0.支付宝 1.微信
            int type = bean.t_type;
            if (type == 0) {
                mHolder.mTypeIv.setBackgroundResource(R.drawable.alipay_round);
                mHolder.mTypeTv.setText(mContext.getResources().getString(R.string.ali_pay_des));
            } else {
                mHolder.mTypeIv.setBackgroundResource(R.drawable.we_chat_round);
                mHolder.mTypeTv.setText(mContext.getResources().getString(R.string.we_chat_des));
            }

            //状态  0.待审核1.已审核待打款 2.已打款，3.打款失败
            int state = bean.t_order_state;
            if (state == 0 || state == 1) {
                mHolder.mStatusTv.setText(mContext.getResources().getString(R.string.withdraw_ing));
                mHolder.mStatusTv.setBackgroundResource(R.drawable.shape_with_draw_state_ing);
                mHolder.mMoneyTv.setTextColor(mContext.getResources().getColor(R.color.black_3f3b48));
            } else if (state == 2) {
                mHolder.mStatusTv.setText(mContext.getResources().getString(R.string.withdraw_successful));
                mHolder.mStatusTv.setBackgroundResource(R.drawable.shape_with_draw_state_success);
                mHolder.mMoneyTv.setTextColor(mContext.getResources().getColor(R.color.red_fe2947));
            } else if (state == 3) {
                mHolder.mStatusTv.setText(mContext.getResources().getString(R.string.withdraw_fail));
                mHolder.mStatusTv.setBackgroundResource(R.drawable.shape_with_draw_state_fail);
                mHolder.mMoneyTv.setTextColor(mContext.getResources().getColor(R.color.black_3f3b48));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTimeTv;
        TextView mMoneyTv;
        TextView mStatusTv;
        TextView mTypeTv;
        ImageView mTypeIv;

        MyViewHolder(View itemView) {
            super(itemView);
            mTimeTv = itemView.findViewById(R.id.time_tv);
            mMoneyTv = itemView.findViewById(R.id.money_tv);
            mStatusTv = itemView.findViewById(R.id.state_tv);
            mTypeTv = itemView.findViewById(R.id.type_tv);
            mTypeIv = itemView.findViewById(R.id.type_iv);
        }

    }

}
