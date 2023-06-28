package com.lovechatapp.chat.camera.beauty;

import android.view.View;
import android.view.ViewGroup;

import com.faceunity.FURenderer;
import com.faceunity.RenderConfig;
import com.faceunity.fulivedemo.database.DatabaseOpenHelper;
import com.faceunity.fulivedemo.entity.BeautyParameterModel;
import com.faceunity.fulivedemo.ui.control.BeautyControlView;
import com.faceunity.fulivedemo.utils.ThreadHelper;
import com.faceunity.utils.FileUtils;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;

public class XiangxinRender implements IBeautyRenderer {

    private FURenderer mFURenderer;
    private boolean control;
    private BeautyControlView controlView;

    @Override
    public void init() {
        DatabaseOpenHelper.register(AppManager.getInstance());
        ThreadHelper.getInstance().execute(() -> {
            FileUtils.copyAssetsLivePhoto(AppManager.getInstance());
            FileUtils.copyAssetsTemplate(AppManager.getInstance());
            FURenderer.initFURenderer(AppManager.getInstance());
        });
    }

    @Override
    public IBeautyRenderer create() {
        control = false;
        mFURenderer = new FURenderer
                .Builder(AppManager.getInstance())
                .build();
        mFURenderer.config(RenderConfig.get(AppManager.getInstance()));
        return this;
    }

    @Override
    public void onDrawFrame() {

    }

    @Override
    public void onDestroy() {
        if (control) {
            mFURenderer.generateConfig().save(AppManager.getInstance());
            BeautyParameterModel
                    .get(AppManager.getInstance())
                    .save(AppManager.getInstance());
        }
        controlView = null;
        control = false;
        onSurfaceDestroyed();
        mFURenderer = null;
    }

    @Override
    public void onSurfaceCreated() {
        if (mFURenderer != null)
            mFURenderer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceDestroyed() {
        if (mFURenderer != null) {
            mFURenderer.onSurfaceDestroyed();
        }
    }

    @Override
    public View getControlView(ViewGroup viewGroup) {
        control = true;
        controlView = View.inflate(viewGroup.getContext(), R.layout.lib_fu_beauty, viewGroup)
                .findViewById(R.id.fu_beauty_control);
        controlView.setOnFUControlListener(mFURenderer);
        controlView.onResume();
        controlView.post(() -> controlView.setCheck());
        return controlView;
    }

}