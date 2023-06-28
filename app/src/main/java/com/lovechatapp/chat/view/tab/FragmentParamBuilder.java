package com.lovechatapp.chat.view.tab;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public final class FragmentParamBuilder {

    private String name;
    private Class<? extends Fragment> clazz;
    private TabPagerViewHolder viewHolder;
    private Bundle bundle;

    private FragmentParamBuilder() {
    }

    public static FragmentParamBuilder create() {
        return new FragmentParamBuilder();
    }

    public FragmentParamBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public FragmentParamBuilder withClazz(Class<? extends Fragment> clazz) {
        this.clazz = clazz;
        return this;
    }

    public FragmentParamBuilder withArgument(String key, int param) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putInt(key, param);
        return this;
    }

    public FragmentParamBuilder withArgument(String key, String param) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(key, param);
        return this;
    }

    public FragmentParamBuilder withViewHolder(TabPagerViewHolder viewHolder) {
        this.viewHolder = viewHolder;
        return this;
    }

    public FragmentParamBuilder withBundle(Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    public final boolean isAvailable() {
        return clazz != null;
    }

    public FragmentParam build() {
        FragmentParam fragmentParam = new FragmentParam();
        fragmentParam.setName(name);
        fragmentParam.setClazz(clazz);
        fragmentParam.setViewHolder(viewHolder);
        fragmentParam.setBundle(bundle);
        return fragmentParam;
    }
}
