package com.lovechatapp.chat.bean;


import com.lovechatapp.chat.base.BaseBean;

public class DynamicPreBean extends BaseBean {

    public ActiveBean activeBean;
    public int index;
    public String imgUrl;
    public boolean isVideo;
    public boolean isLock;

    public DynamicPreBean(ActiveBean activeBean, int index, String imgUrl, boolean isVideo) {
        this.activeBean = activeBean;
        this.index = index;
        this.imgUrl = imgUrl;
        this.isVideo = isVideo;
    }

}