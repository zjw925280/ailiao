package com.lovechatapp.chat.view;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.view.tab.TabPagerLayout;
import com.lovechatapp.chat.view.tab.TabPagerViewHolder;


/**
 * 标签view_holder
 */
public class VipViewHolder extends TabPagerViewHolder {

    public VipViewHolder(TabPagerLayout viewGroup) {
        super(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_label_holder_vip, viewGroup.getViewGroup(), false));
    }

    protected void init(String title) {
        TextView textView = (TextView) itemView;
        textView.setText(title);
    }

    @Override
    protected void onSelected() {
        TextView textView = (TextView) itemView;
        textView.setTypeface(Typeface.DEFAULT_BOLD);
    }

    @Override
    protected void unSelected() {
        TextView textView = (TextView) itemView;
        textView.setTypeface(Typeface.DEFAULT);
    }
}