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
import com.lovechatapp.chat.activity.PersonInfoActivity;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.CallListBean;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：浏览历史RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CallListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<CallListBean> mBeans = new ArrayList<>();

    public CallListRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<CallListBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_call_list_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final CallListBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //昵称
            if (!TextUtils.isEmpty(bean.t_nickName)) {
                mHolder.mNameTv.setText(bean.t_nickName);
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
            //时间
            if (!TextUtils.isEmpty(bean.t_create_time)) {
                mHolder.mTimeTv.setText(bean.t_create_time);
            }
            //描述 呼叫类型：1.呼出 2.呼入
            String callType = bean.callType;
            int time = bean.t_call_time;
            if (time > 0) {//成功
                if (callType.equals("1")) {//呼出
                    mHolder.mDesIv.setImageResource(R.drawable.call_out);
                } else {//呼入
                    mHolder.mDesIv.setImageResource(R.drawable.call_in);
                }
                String content = mContext.getResources().getString(R.string.call_time) + time + mContext.getResources().getString(R.string.minute);
                mHolder.mDesTv.setText(content);
                mHolder.mDesTv.setTextColor(mContext.getResources().getColor(R.color.gray_868686));
            } else {//失败
                if (callType.equals("1")) {//呼出
                    mHolder.mDesIv.setImageResource(R.drawable.call_out_fail);
                } else {//呼入
                    mHolder.mDesIv.setImageResource(R.drawable.call_in_fail);
                }
                mHolder.mDesTv.setText(mContext.getResources().getString(R.string.not_answer));
                mHolder.mDesTv.setTextColor(mContext.getResources().getColor(R.color.red_fe2947));
            }
            //点击
            mHolder.mHeadIv.setOnClickListener(new View.OnClickListener() {
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

        ImageView mHeadIv;
        TextView mNameTv;
        TextView mTimeTv;
        ImageView mDesIv;
        TextView mDesTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNameTv = itemView.findViewById(R.id.name_tv);
            mTimeTv = itemView.findViewById(R.id.time_tv);
            mDesIv = itemView.findViewById(R.id.des_iv);
            mDesTv = itemView.findViewById(R.id.des_tv);
        }

    }

}
