package com.lovechatapp.chat.view.tab;

import android.view.LayoutInflater;
import android.widget.TextView;

import com.lovechatapp.chat.R;


public class MainTabViewHolder extends TabPagerViewHolder {

    private int drawId;

    public MainTabViewHolder(TabPagerLayout viewGroup, int drawId) {
        super(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_tab_holder, viewGroup.getViewGroup(), false));
        this.drawId = drawId;
    }

    protected void init(String title) {
        TextView textView = (TextView) itemView;
        textView.setText(title);
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, drawId, 0, 0);
    }
}