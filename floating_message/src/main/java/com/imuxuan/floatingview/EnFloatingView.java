package com.imuxuan.floatingview;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class EnFloatingView extends FloatingMagnetView {

    private final ImageView mIcon;
    private final TextView nameTv;
    private final TextView msgTv;
    private View view;
    View.OnClickListener onClickListener;

    public EnFloatingView(@NonNull Context context) {
        super(context, null);

        view = inflate(context, R.layout.en_floating_view, this);
        mIcon = findViewById(R.id.head);
        nameTv = findViewById(R.id.name_tv);
        msgTv = findViewById(R.id.msg_tv);
        view.setVisibility(View.GONE);
    }

    public void setVisible(final int visible) {
        view.post(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(visible);
            }
        });
    }

    public void setLookClick(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EnFloatingView.this.onClickListener != null) {
                    EnFloatingView.this.onClickListener.onClick(v);
                }
            }
        });
    }

    public void setIconImage(@DrawableRes int resId) {
        mIcon.setImageResource(resId);
    }

    /**
     * 设置消息
     */
    public void setMessage(String name, String message) {
        nameTv.setText(name);
        msgTv.setText(message);
        setTranslationX(0);
        ObjectAnimator animator = ObjectAnimator.ofFloat(this,
                "translationX", -(getLeft() + getWidth()), 0);
        animator.setDuration(300).start();
    }

    public ImageView getIcon() {
        return mIcon;
    }
}