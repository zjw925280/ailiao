package com.lovechatapp.chat.view.tab;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lovechatapp.chat.R;


/**
 * 消息viewholder
 */
public class MainMsgTabViewHolder extends TabPagerViewHolder {

    public MainMsgTabViewHolder(TabPagerLayout viewGroup) {
        super(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_msg_tab_holder, viewGroup.getViewGroup(), false));
    }

    protected void init(String title) {
        TextView textView = itemView.findViewById(R.id.title_tv);
        textView.setText(title);
        setRedCount(0);
    }

    /**
     * 设置未读消息数
     *
     * @param count
     */
    public final void setRedCount(int count) {
        TextView textView = itemView.findViewById(R.id.red_count_tv);
        textView.setVisibility(View.GONE);
        if (count > 0) {
            textView.setBackgroundResource(count <= 99 ?
                    R.drawable.shape_unread_count_text_back : R.drawable.shape_unread_count_nine_nine_text_back);
            textView.setText(count <= 99 ? count + "" : "99+");
            textView.setVisibility(View.VISIBLE);
        }
    }
}