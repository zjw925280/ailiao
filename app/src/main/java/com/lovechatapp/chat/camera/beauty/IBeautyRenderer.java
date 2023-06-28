package com.lovechatapp.chat.camera.beauty;

import android.view.View;
import android.view.ViewGroup;

public interface IBeautyRenderer {

    void init();

    IBeautyRenderer create();

    void onDrawFrame();

    void onDestroy();

    void onSurfaceCreated();

    void onSurfaceDestroyed();

    View getControlView(ViewGroup viewGroup);

}