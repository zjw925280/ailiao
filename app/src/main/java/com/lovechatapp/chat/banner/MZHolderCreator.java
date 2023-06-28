package com.lovechatapp.chat.banner;

public interface MZHolderCreator<VH extends MZViewHolder> {
    /**
     * 创建ViewHolder
     */
    VH createViewHolder();
}
