package com.lovechatapp.chat.view.tab;

import android.view.LayoutInflater;
import android.widget.TextView;

import com.lovechatapp.chat.R;

public class MainTabViewHolderCenter extends TabPagerViewHolder {

    public MainTabViewHolderCenter(TabPagerLayout viewGroup) {
        super(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_tab_holder_center, viewGroup.getViewGroup(), false));
    }

    protected void init(String title) {
        TextView textView = (TextView) itemView;
        textView.setText(title);
    }
}