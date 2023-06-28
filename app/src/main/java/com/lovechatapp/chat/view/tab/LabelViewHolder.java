package com.lovechatapp.chat.view.tab;

import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.lovechatapp.chat.R;


/**
 * 标签view_holder
 */
public class LabelViewHolder extends TabPagerViewHolder {

    public boolean selected;

    public LabelViewHolder(TabPagerLayout viewGroup) {
        super(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_label_holder, viewGroup.getViewGroup(), false));
    }

    protected void init(String title) {
        TextView textView = (TextView) itemView;
        textView.setText(title);
    }

    @Override
    protected void onSelected() {
        selected = true;
        TextView textView = (TextView) itemView;
        //选中改变字体
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
    }

    @Override
    protected void unSelected() {
        selected = false;
        TextView textView = (TextView) itemView;
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setTypeface(Typeface.DEFAULT);
    }
}