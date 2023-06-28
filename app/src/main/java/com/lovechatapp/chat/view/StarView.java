package com.lovechatapp.chat.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.util.DensityUtil;

public class StarView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap starBitmap;
    private Bitmap starBitmap2;
    private int spacing;
    private int starCount = 5;
    private int selectedCount = 0;

    public StarView(Context context) {
        super(context);
        init();
    }

    public StarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        starBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.small_star_selected);
        starBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.small_star_unselected);
        spacing = DensityUtil.dip2px(getContext(), 8);
    }

    public void setSelected(int count) {
        this.selectedCount = count;
        invalidate();
    }

    public void setStar(int starCount) {
        this.starCount = starCount;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = spacing * 4 + starBitmap.getWidth() * 5;
        int height = starBitmap.getHeight();
        setMeasuredDimension(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < starCount; i++) {
            int left = (spacing + starBitmap.getWidth()) * i;
            canvas.drawBitmap(selectedCount > i ? starBitmap : starBitmap2, left, 0, paint);
        }
    }

}