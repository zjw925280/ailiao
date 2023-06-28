package com.lovechatapp.chat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   渐变色TexView
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */

public class MultiplTextView extends AppCompatTextView {

    int width = 0;
    int height = 0;
    private Paint mPaint = getPaint();
    private Rect mBounds = new Rect();
    private LinearGradient mLinearGradient;

    public MultiplTextView(Context context) {
        super(context);
    }

    public MultiplTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiplTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        init();
        mPaint.setShader(mLinearGradient);
        drawText(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        calculateTextParams();
        setMeasuredDimension(mBounds.right - mBounds.left,
                -mBounds.top + mBounds.bottom);
    }

    private void init() {
        mLinearGradient = new LinearGradient(0, 0, 0, getHeight(),
                Color.parseColor("#ff7689"), Color.parseColor("#fe4f67"),
                Shader.TileMode.CLAMP);
    }

    private String calculateTextParams() {
        String text = getText().toString();
        int textLength = text.length();
        mPaint.getTextBounds(text, 0, textLength, mBounds);
        if (textLength == 0) {
            mBounds.right = mBounds.left;
        }
        return text;
    }

    private void drawText(Canvas canvas) {
        String text = calculateTextParams();
        int left = mBounds.left;
        int bottom = mBounds.bottom;
        mBounds.offset(-mBounds.left, -mBounds.top);
        mPaint.setAntiAlias(true);
        mPaint.setColor(getCurrentTextColor());
        canvas.drawText(text, (float) (-left), (float) (mBounds.bottom - bottom), mPaint);
    }

}
