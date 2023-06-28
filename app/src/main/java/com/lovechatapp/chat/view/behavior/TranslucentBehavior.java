package com.lovechatapp.chat.view.behavior;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.lovechatapp.chat.R;

public class TranslucentBehavior extends CoordinatorLayout.Behavior<Toolbar> implements AppBarLayout.OnOffsetChangedListener {

    private View alphaView;
    private AppBarLayout appBarLayout;
    private View bgView;
    private int scrollHeight;

    public TranslucentBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, Toolbar child, View dependency) {
        return dependency.getId() == R.id.my_banner;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, Toolbar child, View dependency) {
        if (appBarLayout == null) {
            appBarLayout = parent.findViewById(R.id.abl_mine);
            appBarLayout.addOnOffsetChangedListener(this);
        }
        if (scrollHeight == 0) {
            scrollHeight = parent.findViewById(R.id.my_banner).getHeight();
        }
        if (alphaView == null) {
            alphaView = parent.findViewById(R.id.title_nick_tv);
        }
        if (bgView == null) {
            bgView = parent.findViewById(R.id.toolbar);
        }
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (scrollHeight > 0 && Math.abs(i) <= scrollHeight) {
            float percent = Math.abs(i) / (float) scrollHeight;
            if (percent >= 1) {
                percent = 1f;
            }
            float alpha = percent * 255;
            if (bgView != null) {
                bgView.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
            }
            if (alphaView != null) {
                alphaView.setAlpha(alpha);
            }
        }
    }
}