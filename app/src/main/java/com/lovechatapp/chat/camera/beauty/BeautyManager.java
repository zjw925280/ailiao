package com.lovechatapp.chat.camera.beauty;

import android.view.View;
import android.view.ViewGroup;

/**
 * 美颜管理器
 */
public class BeautyManager implements IBeautyRenderer {

    /**
     * 美颜开关
     * 美颜版本查询：initstatus
     */
    public static final boolean BeautyEnable = true;

    private static BeautyManager beautyManager;

    private IBeautyRenderer beautyRenderer;

    private BeautyManager() {
        beautyManager = this;
    }

    public static BeautyManager get() {
        if (beautyManager == null) {
            beautyManager = new BeautyManager();
        }
        return beautyManager;
    }

    public IBeautyRenderer create() {
        onDestroy();
        beautyRenderer = createRenderer();
        beautyRenderer.create();
        return beautyRenderer;
    }

    @Override
    public void onDrawFrame() {
        
    }

    /**
     * 相芯美颜
     */
    private IBeautyRenderer createRenderer() {
        return new XiangxinRender();
    }

    @Override
    public void init() {
        createRenderer().init();
    }

    @Override
    public void onDestroy() {
        if (beautyRenderer != null) {
            beautyRenderer.onDestroy();
            beautyRenderer = null;
        }
    }

    @Override
    public void onSurfaceCreated() {
        if (beautyRenderer != null) {
            beautyRenderer.onSurfaceCreated();
        }
    }

    @Override
    public void onSurfaceDestroyed() {
        if (beautyRenderer != null) {
            beautyRenderer.onSurfaceDestroyed();
        }
    }

    @Override
    public View getControlView(ViewGroup viewGroup) {
        if (beautyRenderer != null) {
            return beautyRenderer.getControlView(viewGroup);
        }
        return null;
    }
}