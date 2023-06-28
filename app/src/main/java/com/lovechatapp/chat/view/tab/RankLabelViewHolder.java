package com.lovechatapp.chat.view.tab;

import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.TextView;
import com.lovechatapp.chat.R;

/**
 * 排行榜标签view_holder
 */
public class RankLabelViewHolder extends TabPagerViewHolder {

    public RankLabelViewHolder(TabPagerLayout viewGroup) {
        super(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_rank_label_holder, viewGroup.getViewGroup(), false));
    }

    protected void init(String title) {
        TextView textView = (TextView) itemView;
        textView.setText(title);
    }

    @Override
    protected void onSelected() {
        TextView textView = (TextView) itemView;
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
    }

    @Override
    protected void unSelected() {
        TextView textView = (TextView) itemView;
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setTypeface(Typeface.DEFAULT);
    }
}