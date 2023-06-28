package com.lovechatapp.chat.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.CommentBean;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播资料下方用户评价RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class InfoCommentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<CommentBean> mBeans = new ArrayList<>();

    public InfoCommentRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<CommentBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_info_comment_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //头像
            String headImg = bean.t_user_hand;
            if (!TextUtils.isEmpty(headImg)) {
                int width = DevicesUtil.dp2px(mContext, 40);
                int high = DevicesUtil.dp2px(mContext, 40);
                ImageLoadHelper.glideShowCircleImageWithUrl(mContext, headImg, mHolder.mHeadIv, width, high);
            } else {
                mHolder.mHeadIv.setBackgroundResource(R.drawable.default_head_img);
            }
            String nick = bean.t_user_nick;
            if (!TextUtils.isEmpty(nick)) {
                mHolder.mNickTv.setText(nick);
            }
            //标签
            String label = bean.t_label_name;
            if (!TextUtils.isEmpty(label)) {
                if (label.contains(",")) {
                    String[] labels = label.split(",");
                    if (labels.length <= 3) {
                        setLabelView(labels, mHolder.mTagLl);
                    } else {
                        String[] tempLabels = new String[3];
                        tempLabels[0] = labels[0];
                        tempLabels[1] = labels[1];
                        tempLabels[2] = labels[2];
                        setLabelView(tempLabels, mHolder.mTagLl);
                    }
                } else {
                    String[] labels = new String[1];
                    labels[0] = label;
                    setLabelView(labels, mHolder.mTagLl);
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
        LinearLayout mTagLl;

        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mTagLl = itemView.findViewById(R.id.tag_ll);
        }
    }

    /**
     * 设置标签View
     */
    private void setLabelView(String[] labelBeans, LinearLayout mTagLl) {
        //形象标签
        mTagLl.removeAllViews();
        int[] backs = {R.drawable.shape_tag_one, R.drawable.shape_tag_two, R.drawable.shape_tag_three};
        if (labelBeans != null && labelBeans.length > 0) {
            for (int i = 0; i < labelBeans.length; i++) {
                @SuppressLint("InflateParams")
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_tag_user_info_layout, null);
                TextView textView = view.findViewById(R.id.content_tv);
                textView.setText(labelBeans[i]);
                Random random = new Random();
                int index = random.nextInt(backs.length);
                textView.setBackgroundResource(backs[index]);
                if (i != 0) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = 20;
                    textView.setLayoutParams(params);
                }
                mTagLl.addView(textView);
            }
            if (mTagLl.getChildCount() > 0) {
                mTagLl.setVisibility(View.VISIBLE);
            }
        }
    }

}
