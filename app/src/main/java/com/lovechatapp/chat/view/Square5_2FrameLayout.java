package com.lovechatapp.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class Square5_2FrameLayout extends FrameLayout {

    public Square5_2FrameLayout(Context context) {
        super(context);
    }

    public Square5_2FrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Square5_2FrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        int childHeightSize = (int) (childWidthSize * 2f / 5f);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}