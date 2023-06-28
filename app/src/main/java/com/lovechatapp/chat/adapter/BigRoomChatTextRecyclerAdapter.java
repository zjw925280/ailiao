package com.lovechatapp.chat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.BigRoomTextBean;

import java.util.ArrayList;
import java.util.List;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：大房间页面文字消息RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/8/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class BigRoomChatTextRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<BigRoomTextBean> mBeans = new ArrayList<>();

    public BigRoomChatTextRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void addData(BigRoomTextBean message) {
        mBeans.add(message);
        notifyItemChanged(mBeans.size() - 1);
    }

    public int getPosition() {
        return mBeans.size() - 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_big_room_chat_text_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BigRoomTextBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            String nickName = bean.nickName;
            //文字类型: 1 文字  2  用户进入  3  warning提示
            if (bean.type == 1) {
                if (!TextUtils.isEmpty(nickName)) {
                    nickName = nickName + mContext.getString(R.string.sign_one);
                }
                mHolder.mContentTv.setTextColor(mContext.getResources().getColor(R.color.white));
                //昵称蓝色  内容白色
                String content = nickName + bean.content;
                SpannableStringBuilder builder = new SpannableStringBuilder(content);
                if (!TextUtils.isEmpty(nickName)) {
                    builder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.green_00ffd8)),
                            0, nickName.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                mHolder.mContentTv.setText(builder);
            } else if (bean.type == 2) {//用户进入
                mHolder.mContentTv.setTextColor(mContext.getResources().getColor(R.color.yellow_f9fb44));
                String content = nickName + mContext.getString(R.string.come_welcome);
                SpannableStringBuilder builder = new SpannableStringBuilder(content);
                builder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.green_00ffd8)),
                        0, nickName.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                mHolder.mContentTv.setText(builder);
            } else {//文字提示
                mHolder.mContentTv.setTextColor(mContext.getResources().getColor(R.color.green_aaffc4));
                mHolder.mContentTv.setText(bean.content);
            }

        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mContentTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentTv = itemView.findViewById(R.id.content_tv);
        }
    }

}
