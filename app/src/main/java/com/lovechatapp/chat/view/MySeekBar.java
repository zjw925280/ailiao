package com.lovechatapp.chat.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.lovechatapp.chat.R;


/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：自定义SeekBar
 * 作者：
 * 创建时间：2018/9/18
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class MySeekBar extends android.support.v7.widget.AppCompatSeekBar {

    private Paint paint;
    private Drawable mThumb;
    private String temp_str = "0";

    public MySeekBar(Context context) {
        this(context, null);
    }

    @SuppressWarnings("deprecation")
    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(getResources().getColor(R.color.pink_fd4aab));
        paint.setTextSize(36);
    }

    @Override
    public void setThumb(Drawable thumb) {
        super.setThumb(thumb);
        this.mThumb = thumb;
    }

    public Drawable getSeekBarThumb() {
        return mThumb;
    }

    //设置thumb的偏移数值
    @Override
    public void setThumbOffset(int thumbOffset) {
        super.setThumbOffset(thumbOffset / 3);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        int data = Integer.parseInt(temp_str);
        Rect rect = getSeekBarThumb().getBounds();
        if (data < 10) {
            canvas.drawText(temp_str, rect.left + (rect.width()), rect.top - paint.ascent() + (rect.height() - (paint.descent() - paint.ascent())) / 2.0F, paint);
        } else {
            canvas.drawText(temp_str, rect.left + (rect.width()) / 2.0F, rect.top - paint.ascent() + (rect.height() - (paint.descent() - paint.ascent())) / 2.0F, paint);
        }
        canvas.restore();
    }

    public void setValue(String value) {
        StringBuffer sb = new StringBuffer();
        sb.append(value);
        temp_str = sb.toString();
        invalidate();
    }

    @SuppressLint("NewApi")
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setOnSeekBarChangeListener(final OnSeekBarChangeListener l) {
        super.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (l != null) {
                    l.onProgressChanged(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (l != null) {
                    l.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (l != null) {
                    l.onStopTrackingTouch(seekBar);
                }
            }
        });
    }

}
