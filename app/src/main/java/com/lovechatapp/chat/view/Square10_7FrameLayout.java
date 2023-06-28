package com.lovechatapp.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class Square10_7FrameLayout extends FrameLayout {

    public Square10_7FrameLayout(Context context) {
        super(context);
    }

    public Square10_7FrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Square10_7FrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        int childHeightSize = (int) (childWidthSize * 10f / 7f);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}