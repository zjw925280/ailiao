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
import com.lovechatapp.chat.bean.ReceiveListBean;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：收到礼物RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ReceiveListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<ReceiveListBean> mBeans = new ArrayList<>();

    public ReceiveListRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<ReceiveListBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_receive_gift_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ReceiveListBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //主播昵称
            String nick = bean.t_nickName;
            if (!TextUtils.isEmpty(nick)) {
                mHolder.mNickTv.setText(nick);
            }
            //时间
            String time = bean.t_create_time;
            if (!TextUtils.isEmpty(time)) {
                mHolder.mTimeTv.setText(time);
            }
            //头像
            String headImg = bean.t_handImg;
            if (!TextUtils.isEmpty(headImg)) {
                int width = DevicesUtil.dp2px(mContext, 50);
                int high = DevicesUtil.dp2px(mContext, 50);
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, headImg, mHolder.mHeadIv, width, high);
            } else {
                mHolder.mHeadIv.setImageResource(R.drawable.default_head_img);
            }
            //图片
            String giftUrl = bean.t_gift_still_url;
            //礼物类型 7.红包 9.图片礼物
            int type = bean.t_consume_type;
            int amount = bean.t_amount;
            if (type == 7) {
                mHolder.mGiftIv.setImageResource(R.drawable.image_red_pack);
                if (amount > 0) {
                    mHolder.mAmountTv.setText(String.valueOf(amount));
                    mHolder.mAmountTv.setVisibility(View.VISIBLE);
                } else {
                    mHolder.mAmountTv.setVisibility(View.GONE);
                }
            } else if (type == 9) {
                mHolder.mAmountTv.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(giftUrl)) {
                    int width = DevicesUtil.dp2px(mContext, 44);
                    int high = DevicesUtil.dp2px(mContext, 38);
                    ImageLoadHelper.glideShowImageWithUrl(mContext, giftUrl, mHolder.mGiftIv, width, high);
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
        TextView mNickTv;
        TextView mTimeTv;
        ImageView mGiftIv;
        TextView mAmountTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mTimeTv = itemView.findViewById(R.id.time_tv);
            mGiftIv = itemView.findViewById(R.id.gift_iv);
            mAmountTv = itemView.findViewById(R.id.amount_tv);
        }

    }

}
