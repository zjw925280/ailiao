package com.lovechatapp.chat.adapter;

import android.net.Uri;
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
import com.lovechatapp.chat.bean.ActiveLocalBean;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：发布动态图片RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/12/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PostActiveRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<ActiveLocalBean> mBeans = new ArrayList<>();

    public PostActiveRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<ActiveLocalBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_post_active_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ActiveLocalBean bean = mBeans.get(position);
        final int mPosition = position;
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            String localPath = bean.localPath;
            if (!TextUtils.isEmpty(localPath)) {
                mHolder.mDeleteIv.setVisibility(View.VISIBLE);

                //显示图片
                File file = new File(localPath);
                if (file.exists()) {
                    Uri pictureUri = FileUtil.getUriAdjust24(mContext, file);
                    int resizeWidth = DevicesUtil.dp2px(mContext, 105);
                    ImageLoadHelper.glideShowImageWithUri(mContext, pictureUri,
                            mHolder.mContentIv, resizeWidth, resizeWidth);
                }
                //设置约豆
//                int gold = bean.gold;
//                if (gold > 0) {
//                    mHolder.mChargeTv.setText(String.format("%s约豆", gold));
//                } else {
//                    mHolder.mChargeTv.setText(mContext.getResources().getString(R.string.free_one));
//                }
//                mHolder.mChargeTv.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (mOnPostItemClickListener != null) {
//                            mOnPostItemClickListener.onFreeClick(bean, mPosition);
//                        }
//                    }
//                });
                mHolder.mDeleteIv.setOnClickListener(v -> {
                    if (mOnPostItemClickListener != null) {
                        mOnPostItemClickListener.onDeleteClick(bean, mPosition);
                    }
                });
                mHolder.mContentFl.setOnClickListener(null);
            } else {//localPath如果为空 显示加图片
                mHolder.mDeleteIv.setVisibility(View.GONE);
                mHolder.mChargeTv.setVisibility(View.GONE);
                mHolder.mContentIv.setImageResource(R.drawable.add_post);
                mHolder.mContentFl.setOnClickListener(v -> {
                    if (mOnPostItemClickListener != null) {
                        mOnPostItemClickListener.onAddClick(mPosition);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mContentIv;
        ImageView mDeleteIv;
        TextView mChargeTv;
        View mContentFl;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentIv = itemView.findViewById(R.id.content_iv);
            mDeleteIv = itemView.findViewById(R.id.delete_iv);
            mChargeTv = itemView.findViewById(R.id.charge_tv);
            mContentFl = itemView.findViewById(R.id.content_fl);
        }
    }

    public interface OnPostItemClickListener {
        /**
         * 点击是添加
         */
        void onAddClick(int position);

        /**
         * 点击删除
         */
        void onDeleteClick(ActiveLocalBean bean, int position);

        /**
         * 点击免费
         */
        void onFreeClick(ActiveLocalBean bean, int position);
    }

    private OnPostItemClickListener mOnPostItemClickListener;

    public void setOnPostItemClickListener(OnPostItemClickListener onPostItemClickListener) {
        mOnPostItemClickListener = onPostItemClickListener;
    }

}