package com.lovechatapp.chat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：自定义水波扩散动画View
 * 作者：
 * 创建时间：2018/6/20
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SpreadView extends View {

    private Paint mCenterPaint; //中心圆paint
    private int mRadius = 100; //中心圆半径
    private Paint mSpreadPaint; //扩散圆paint
    private float mCenterX;//圆心x
    private float mCenterY;//圆心y
    private int mDistance = 3; //每次圆递增间距
    private int mMaxRadius = 80; //最大圆半径
    private List<Integer> mSpreadRadius = new ArrayList<>();//扩散圆层级数，元素为扩散的距离
    private List<Integer> mAlphas = new ArrayList<>();//对应每层圆的透明度
    private boolean mNeedAnim = true;

    public SpreadView(Context context) {
        this(context, null);
    }

    public SpreadView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpreadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SpreadView, defStyleAttr, 0);
        mRadius = DevicesUtil.dp2px(context, 30);
        mMaxRadius = DevicesUtil.dp2px(context, 25);
        int centerColor = a.getColor(R.styleable.SpreadView_spread_center_color, ContextCompat.getColor(context, R.color.pink_main));
        int spreadColor = a.getColor(R.styleable.SpreadView_spread_spread_color, ContextCompat.getColor(context, R.color.pink_f4d0df));
        mDistance = a.getInt(R.styleable.SpreadView_spread_distance, mDistance);
        a.recycle();

        mCenterPaint = new Paint();
        mCenterPaint.setColor(centerColor);
        mCenterPaint.setAntiAlias(true);
        //最开始不透明且扩散距离为0
        mAlphas.add(255);
        mSpreadRadius.add(0);
        mSpreadPaint = new Paint();
        mSpreadPaint.setAntiAlias(true);
        mSpreadPaint.setAlpha(255);
        mSpreadPaint.setColor(spreadColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //圆心位置
        mCenterX = w / 2;
        mCenterY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mSpreadRadius.size(); i++) {
            int alpha = mAlphas.get(i);
            mSpreadPaint.setAlpha(alpha);
            int width = mSpreadRadius.get(i);
            //绘制扩散的圆
            canvas.drawCircle(mCenterX, mCenterY, mRadius + width, mSpreadPaint);

            //每次扩散圆半径递增，圆透明度递减
            if (alpha >= 0 && width < 300) {
                alpha = alpha - mDistance > 0 ? alpha - 6 * mDistance : 1;
                mAlphas.set(i, alpha);
                mSpreadRadius.set(i, width + mDistance);
            }
        }
        //当最外层扩散圆半径达到最大半径时添加新扩散圆
        if (mSpreadRadius.get(mSpreadRadius.size() - 1) > mMaxRadius) {
            mSpreadRadius.add(0);
            mAlphas.add(255);
        }
        //超过8个扩散圆，删除最先绘制的圆，即最外层的圆
        if (mSpreadRadius.size() >= 4) {
            mAlphas.remove(0);
            mSpreadRadius.remove(0);
        }
        //中间的圆
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mCenterPaint);
        //延迟更新，达到扩散视觉差效果
        if (mNeedAnim) {
            postInvalidateDelayed(80);
        }
    }

    /**
     * 开始
     */
    public void stopAnim() {
        mNeedAnim = false;
    }

}
