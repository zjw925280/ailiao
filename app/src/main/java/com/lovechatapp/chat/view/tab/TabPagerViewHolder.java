package com.lovechatapp.chat.view.tab;

import android.view.View;

public class TabPagerViewHolder {

    public View itemView;
    private View.OnClickListener onClickListener;

    public TabPagerViewHolder(View itemView) {
        this.itemView = itemView;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    protected void init(String title) {

    }

    /**
     * 选中改变
     */
    protected void onSelected() {
    }

    /**
     * 未选中改变
     */
    protected void unSelected() {
    }

    protected void onClick() {
        if (onClickListener != null) {
            onClickListener.onClick(itemView);
        }
    }
}