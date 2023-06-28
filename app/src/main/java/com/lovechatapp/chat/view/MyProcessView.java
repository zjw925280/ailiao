package com.lovechatapp.chat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.util.DevicesUtil;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   自定义有进度条动画view
 * 作者：
 * 创建时间：2018/7/13
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class MyProcessView extends View {

    private Context mContext;
    private float process = 0;//要设置的进度值
    private int strikeWidth = 5;
    private int color;
    private float startAngle = 0;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        mPaint.setColor(color);
        invalidate();
    }

    public float getProcess() {
        return process;
    }

    public void setProcess(float process) {
        this.process = process;
        if (process < 100) {
            invalidate();
        }
    }

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mRectF = new RectF();

    public MyProcessView(Context context) {
        this(context, null);
    }

    public MyProcessView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyProcessView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context);
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        mPaint.setColor(context.getResources().getColor(R.color.pink_main));
        mPaint.setStrokeWidth(strikeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);//让画笔画出的线条两头是圆的
        mPaint.setTextSize(DevicesUtil.dp2px(context, 14));
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int mWidth;
        int mHeight;

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {//当画布的大小为明确值MATCH_CONTENT
            mWidth = widthSize;
        } else {
            mWidth = DevicesUtil.dp2px(mContext, 200);
        }
        if (heightMode == MeasureSpec.EXACTLY) {//
            mHeight = heightSize;
        } else {
            mHeight = DevicesUtil.dp2px(mContext, 200);
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        //找到中心点,然后左右上下加减半径

        //1.画圆弧
        mPaint.setStyle(Paint.Style.STROKE);
        mRectF.set(strikeWidth, strikeWidth, getWidth() - strikeWidth, getHeight() - strikeWidth);
        canvas.drawArc(mRectF, startAngle, process * 3.6f, false, mPaint);

        //2.画字
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText((int) process + "%", centerX, centerY - (mPaint.ascent() + mPaint.descent()) / 2, mPaint);
    }

}
