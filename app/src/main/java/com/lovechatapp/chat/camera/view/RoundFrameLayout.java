package com.lovechatapp.chat.camera.view;

import android.content.Context;
import android.graphics.Outline;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

import com.lovechatapp.chat.util.DensityUtil;

public class RoundFrameLayout extends FrameLayout {

    public RoundFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public RoundFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                Rect rect = new Rect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                outline.setRoundRect(rect, DensityUtil.dip2px(getContext(), 6));
            }
        });
        setClipToOutline(true);
    }

}