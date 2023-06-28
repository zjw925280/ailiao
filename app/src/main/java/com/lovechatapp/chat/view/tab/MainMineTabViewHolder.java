package com.lovechatapp.chat.view.tab;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lovechatapp.chat.R;


/**
 * 我的viewholder
 */
public class MainMineTabViewHolder extends TabPagerViewHolder {

    public MainMineTabViewHolder(TabPagerLayout viewGroup) {
        super(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_mine_tab_holder, viewGroup.getViewGroup(), false));
    }

    protected void init(String title) {
        TextView textView = itemView.findViewById(R.id.title_tv);
        textView.setText(title);
        setRedVisible(false);
    }

    /**
     * 红包图标显示|隐藏
     */
    public final void setRedVisible(boolean b) {
        itemView.findViewById(R.id.red_small_iv).setVisibility(b ? View.VISIBLE : View.GONE);
    }
}