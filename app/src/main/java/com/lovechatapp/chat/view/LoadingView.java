package com.lovechatapp.chat.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.lovechatapp.chat.R;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：加载中动画view
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class LoadingView extends View {
    private final int MSG_ANIM = 100;

    private int mFrameCount = 8;  //该帧数是根据view的背景图片确定，不可随意变更
    private int mFrameInterval = 150; //每帧的时间差
    private int DURATION = mFrameCount * mFrameInterval;

    private int mCurrFrame = 1;

    private Handler mHandler;
    private int mCurrDegree;
    private int mAnimInterval = 1;
    private int mDeltaDegree = 1;

    public LoadingView(Context context) {
        super(context);
        init(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mCurrDegree = 0;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(mCurrDegree);
        mCurrDegree = mCurrDegree % 360;
        setRotation(mCurrDegree);
        if (mHandler == null) {
            createHandler();
        }
        mHandler.sendEmptyMessageDelayed(MSG_ANIM, mAnimInterval);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
            mFrameCount = a.getInt(R.styleable.LoadingView_frame_count, mFrameCount);
            mFrameInterval = a.getInt(R.styleable.LoadingView_frame_interval, mFrameInterval);
            a.recycle();
        }

        createHandler();

        DURATION = mFrameCount * mFrameInterval;
        mAnimInterval = DURATION / mFrameCount;
        mDeltaDegree = 360 / mFrameCount;
    }

    public int getFrameCount() {
        return mFrameCount;
    }

    /**
     * 帧数量
     *
     * @param frameCount
     */
    public void setFrameCount(int frameCount) {
        mFrameCount = frameCount;
    }

    public int getFrameInterval() {
        return mFrameInterval;
    }

    /**
     * 每帧的时间间隔
     *
     * @param frameInterval
     */
    public void setFrameInterval(int frameInterval) {
        mFrameInterval = frameInterval;
    }

    private void createHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_ANIM:
                        mCurrFrame++;
                        mCurrDegree += mDeltaDegree;
                        if (mCurrFrame > mFrameCount) {
                            mCurrFrame = 1;
                        }
                        postInvalidate();
                }
            }
        };
    }

}
