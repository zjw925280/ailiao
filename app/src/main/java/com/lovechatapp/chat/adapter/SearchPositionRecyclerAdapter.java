package com.lovechatapp.chat.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.SearchPoiBean;

import java.util.ArrayList;
import java.util.List;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：搜索位置RecyclerView的Adapter
 * 作者：
 * 创建时间：2019/1/15
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SearchPositionRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<SearchPoiBean> mBeans = new ArrayList<>();

    public SearchPositionRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<SearchPoiBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_search_position_recycler_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final SearchPoiBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //标题
            SpannableStringBuilder blackText = dealRedText(bean.name, bean.searchText);
            mHolder.mBlackTv.setText(blackText);
            SpannableStringBuilder grayText = dealRedText(bean.address, bean.searchText);
            mHolder.mGrayTv.setText(grayText);
            mHolder.mContentRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnSearchItemClickListener != null) {
                        String content = "";
                        if (!TextUtils.isEmpty(bean.name)) {
                            if (!TextUtils.isEmpty(bean.addCity)) {
                                content = bean.addCity + mContext.getResources().getString(R.string.middle_point) + bean.name;
                            } else {
                                content = bean.name;
                            }
                        }
                        mOnSearchItemClickListener.onSearchItemClick(content);
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

        RelativeLayout mContentRl;
        TextView mBlackTv;
        TextView mGrayTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentRl = itemView.findViewById(R.id.content_rl);
            mBlackTv = itemView.findViewById(R.id.black_tv);
            mGrayTv = itemView.findViewById(R.id.gray_tv);
        }
    }

    /**
     * 处理红色
     */
    private SpannableStringBuilder dealRedText(String content, String searchText) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(content);
        for (int i = 0; i < content.length(); i++) {
            char ch = content.charAt(i);
            if (judgeContain(ch, searchText)) {
                spannableStringBuilder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.red_fe2947)),
                        i, i + 1, SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableStringBuilder;
    }

    /**
     * 判断是否包含
     */
    private boolean judgeContain(char ch, String searchText) {
        for (int i = 0; i < searchText.length(); i++) {
            char cSearch = searchText.charAt(i);
            if (ch == cSearch) {
                return true;
            }
        }
        return false;
    }

    public interface OnSearchItemClickListener {
        void onSearchItemClick(String address);
    }

    private OnSearchItemClickListener mOnSearchItemClickListener;

    public void setOnSearchItemClickListener(OnSearchItemClickListener onSearchItemClickListener) {
        mOnSearchItemClickListener = onSearchItemClickListener;
    }

}
