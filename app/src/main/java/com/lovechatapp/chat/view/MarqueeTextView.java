package com.lovechatapp.chat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class MarqueeTextView extends android.support.v7.widget.AppCompatTextView {

    private float textWidth;
    private float realWidth;

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean isReal = realWidth == 0;
        textWidth = getPaint().measureText(getText().toString());
        realWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) textWidth, View.MeasureSpec.EXACTLY), heightMeasureSpec);
        if (isReal && realWidth != 0) {
            setTranslationX(realWidth / 5);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float x = getTranslationX() - 2;
        if (x < -textWidth - 30) {
            x = realWidth;
        }
        setTranslationX(x);
        invalidate();
    }

    @Override
    public void setText(CharSequence text, TextView.BufferType type) {
        super.setText(text, type);
        requestLayout();
    }
}