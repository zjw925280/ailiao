package com.lovechatapp.chat.view.tab;


import android.os.Bundle;
import android.support.v4.app.Fragment;

public class FragmentParam {

    private String name;

    private Class<? extends Fragment> clazz;

    private TabPagerViewHolder viewHolder;

    private Bundle bundle;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<? extends Fragment> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends Fragment> clazz) {
        this.clazz = clazz;
    }

    public TabPagerViewHolder getViewHolder() {
        return viewHolder;
    }

    public void setViewHolder(TabPagerViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}