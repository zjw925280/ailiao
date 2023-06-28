package com.lovechatapp.chat.view.gallery;

import android.view.ViewGroup;

import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.ViewHolder;


public abstract class CardAdapter extends AbsRecycleAdapter {

    private CardAdapterHelper mCardAdapterHelper;

    public CardAdapter(Type... mTypes) {
        super(mTypes);
        this.mCardAdapterHelper = new CardAdapterHelper();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
        mCardAdapterHelper.onCreateViewHolder(parent, viewHolder.itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCardAdapterHelper.onBindViewHolder(holder.itemView, position, getItemCount());
        super.onBindViewHolder(holder, position);
    }
}