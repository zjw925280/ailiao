package com.lovechatapp.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class Square4_3FrameLayout extends FrameLayout {

    public Square4_3FrameLayout(Context context) {
        super(context);
    }

    public Square4_3FrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Square4_3FrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        int childHeightSize = (int) (childWidthSize * 4f / 3f);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}