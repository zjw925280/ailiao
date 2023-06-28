package com.imuxuan.floatingview;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.imuxuan.floatingview.utils.EnContext;

public class FloatingView implements IFloatingView {

    private EnFloatingView mEnFloatingView;
    private static volatile FloatingView mInstance;
    private Activity activity;

    private View.OnClickListener hideListener;
    private View.OnClickListener lookListener;

    private Handler handler;

    private boolean ennable = true;

    private boolean visible = false;

    private Object tag;

    /**
     * 每条消息弹窗停留时长
     */
    private final int delayTime = 3 * 1000;

    private FloatingView() {
        initHandler();
        if (mEnFloatingView == null) {
            mEnFloatingView = new EnFloatingView(EnContext.get());
            mEnFloatingView.setLayoutParams(getParams());
            mEnFloatingView.findViewById(R.id.delete_iv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hide();
                }
            });
        }
    }

    public static FloatingView get() {
        if (mInstance == null) {
            synchronized (FloatingView.class) {
                if (mInstance == null) {
                    mInstance = new FloatingView();
                }
            }
        }
        return mInstance;
    }

    private void initHandler() {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (visible && ennable) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(
                            mEnFloatingView,
                            "translationX",
                            0,
                            -(mEnFloatingView.getLeft() + mEnFloatingView.getWidth()));
                    animator.setDuration(300);
                    animator.start();
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setVisible(false);
                            //nextMessage
                            if (hideListener != null) {
                                hideListener.onClick(null);
                            }
                        }
                    }, animator.getDuration());
                } else {
                    setVisible(false);
                }
            }
        };
    }

    public final void setTag(Object tag) {
        this.tag = tag;
    }

    public final Object getTag() {
        return tag;
    }

    public final void setMessage(String name, String message) {
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessageDelayed(0, delayTime);
        visible = true;
        getView().setVisible(View.VISIBLE);
        getView().setMessage(name, message);
    }

    public void setEnnable(boolean enable) {
        this.ennable = enable;
        setVisible(visible);
    }

    public final void setVisible(boolean b) {
        visible = b;
        if (!visible || !ennable) {
            getView().setVisible(View.GONE);
        }
    }

    public final boolean isEnnable() {
        return ennable;
    }

    public final boolean isVisible() {
        return ennable && visible;
    }

    public void setLookClick(View.OnClickListener onClickListener) {
        lookListener = onClickListener;
        getView().setLookClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                if (lookListener != null)
                    lookListener.onClick(v);
            }
        });
    }

    public void setHideListener(View.OnClickListener hideListener) {
        this.hideListener = hideListener;
    }

    private void hide() {
        handler.removeCallbacksAndMessages(null);
        setVisible(false);
    }

    @Override
    public FloatingView attach(Activity activity) {
        FrameLayout container = getActivityRoot(activity);
        if (mEnFloatingView.getParent() != null)
            ((ViewGroup) mEnFloatingView.getParent()).removeView(mEnFloatingView);
        container.addView(mEnFloatingView);
        setLookClick(lookListener);
        setVisible(visible);
        this.activity = activity;
        return this;
    }

    @Override
    public FloatingView detach(Activity activity) {
        if (mEnFloatingView.getParent() != null) {
            ((ViewGroup) mEnFloatingView.getParent()).removeView(mEnFloatingView);
        }
        return this;
    }

    @Override
    public EnFloatingView getView() {
        return mEnFloatingView;
    }

    @Override
    public FloatingView icon(@DrawableRes int resId) {
        if (mEnFloatingView != null) {
            mEnFloatingView.setIconImage(resId);
        }
        return this;
    }

    @Override
    public FloatingView layoutParams(ViewGroup.LayoutParams params) {
        if (mEnFloatingView != null) {
            mEnFloatingView.setLayoutParams(params);
        }
        return this;
    }

    @Override
    public FloatingView listener(MagnetViewListener magnetViewListener) {
        if (mEnFloatingView != null) {
            mEnFloatingView.setMagnetViewListener(magnetViewListener);
        }
        return this;
    }

    public Activity getActivity() {
        return activity;
    }

    private FrameLayout.LayoutParams getParams() {
        int marginBottom = (int) (0.5F + 60 * mEnFloatingView.getContext().getResources().getDisplayMetrics().density);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.START;
        params.setMargins(13, params.topMargin, params.rightMargin, marginBottom);
        return params;
    }

    private FrameLayout getActivityRoot(Activity activity) {
        if (activity == null) {
            return null;
        }
        try {
            return (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}