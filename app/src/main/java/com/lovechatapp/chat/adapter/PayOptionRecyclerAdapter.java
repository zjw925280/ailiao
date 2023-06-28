package com.lovechatapp.chat.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.PayOptionBean;
import com.lovechatapp.chat.helper.ImageLoadHelper;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：其余充值方式RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/10/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PayOptionRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<PayOptionBean> mBeans = new ArrayList<>();

    public PayOptionRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<PayOptionBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_pay_option_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final PayOptionBean bean = mBeans.get(position);
        final int mPosition = position;
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //图标
            ImageLoadHelper.glideShowImageWithUrl(mContext, bean.payIcon, mHolder.mIconIv);
            //支付名称
            mHolder.mNameTv.setText(bean.payName);
            //选中
            if (bean.isSelected) {
                mHolder.mCheckIv.setSelected(true);
                if (mOnItemSelectListner != null) {
                    mOnItemSelectListner.onItemSelected(bean);
                }
            } else {
                mHolder.mCheckIv.setSelected(false);
            }
            //点击事件
            mHolder.mContentRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.isSelected) {
                        return;
                    }
                    for (int i = 0; i < mBeans.size(); i++) {
                        mBeans.get(i).isSelected = i == mPosition;
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        //内容
        RelativeLayout mContentRl;
        //图标
        ImageView mIconIv;
        //支付名
        TextView mNameTv;
        //选中
        ImageView mCheckIv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentRl = itemView.findViewById(R.id.content_rl);
            mIconIv = itemView.findViewById(R.id.icon_iv);
            mNameTv = itemView.findViewById(R.id.name_tv);
            mCheckIv = itemView.findViewById(R.id.check_iv);
        }
    }

    public interface OnItemSelectListner {
        void onItemSelected(PayOptionBean bean);
    }

    private OnItemSelectListner mOnItemSelectListner;

    public void setOnItemSelectListner(OnItemSelectListner onItemSelectListner) {
        mOnItemSelectListner = onItemSelectListner;
    }

}
