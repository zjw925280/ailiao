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

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.BigRoomUserBean;
import com.lovechatapp.chat.helper.ImageLoadHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：大房间顶部用户列表
 * 作者：
 * 创建时间：2018/10/10
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class TopUserRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<BigRoomUserBean> mRecommendBeans = new ArrayList<>();

    public TopUserRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<BigRoomUserBean> beans) {
        mRecommendBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_top_user_recycler_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BigRoomUserBean bean = mRecommendBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        //绑定数据
        if (bean != null) {
            //头像
            String t_handImg = bean.t_handImg;
            if (!TextUtils.isEmpty(t_handImg)) {
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, t_handImg, mHolder.mHeadIv);
            }
            //贡献
            int number = bean.total;
            if (number > 0) {
                String content;
                if (number < 10000) {
                    content = String.valueOf(number);
                } else {
                    BigDecimal old = new BigDecimal(number);
                    BigDecimal ten = new BigDecimal(10000);
                    BigDecimal res = old.divide(ten, 1, RoundingMode.UP);
                    content = res + mContext.getString(R.string.number_ten_thousand);
                }
                mHolder.mTotalTv.setText(content);
            }
            //背景
            if (position == 0) {//第一名
                mHolder.mTotalTv.setBackgroundResource(R.drawable.shape_top_one);
            } else if (position == 1) {//第二名
                mHolder.mTotalTv.setBackgroundResource(R.drawable.shape_top_two);
            } else if (position == 2) {//第三名
                mHolder.mTotalTv.setBackgroundResource(R.drawable.shape_top_three);
            } else {//其他
                mHolder.mTotalTv.setBackgroundResource(R.drawable.shape_top_four);
            }
            //点击事件
            mHolder.mContentFl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(bean);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mRecommendBeans != null ? mRecommendBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mHeadIv;
        TextView mTotalTv;
        FrameLayout mContentFl;

        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mTotalTv = itemView.findViewById(R.id.total_tv);
            mContentFl = itemView.findViewById(R.id.content_fl);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(BigRoomUserBean bean);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}
