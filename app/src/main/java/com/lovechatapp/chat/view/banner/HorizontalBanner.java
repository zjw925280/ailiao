package com.lovechatapp.chat.view.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;

public class HorizontalBanner extends ViewGroup {

    private int mLastX;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mMaxVelocity;
    private int dataSize;
    private OnBannerListener onBindView;
    private SparseIntArray sparseArray = new SparseIntArray(3);

    private Handler handler;
    private boolean loop;
    private int currentPosition;
    private int scaledTouchSlop;

    public HorizontalBanner(Context context) {
        super(context);
        init(context);
    }

    public HorizontalBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HorizontalBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("HandlerLeak")
    private void init(Context context) {
        mScroller = new Scroller(context);
        ViewConfiguration config = ViewConfiguration.get(context);
        mMaxVelocity = config.getScaledMinimumFlingVelocity();
        scaledTouchSlop = config.getScaledTouchSlop();

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = sparseArray.get(v.hashCode(), 0);
                if (onBindView != null) {
                    onBindView.onItemClick(position);
                }
            }
        };

        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setOnClickListener(onClickListener);
            sparseArray.put(imageView.hashCode(), 0);
            addView(imageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (loop) {
                    scrollToPage(1);
                    handler.sendEmptyMessageDelayed(0, 3000);
                }
            }
        };
    }

    public final void loop(boolean isLoop) {
        if (loop == isLoop)
            return;
        loop = isLoop;
        handler.removeCallbacksAndMessages(null);
        if (loop) {
            handler.sendEmptyMessageDelayed(0, 3000);
        }
    }

    public final void initDataSize(int dataSize, OnBannerListener bindView) {
        if (dataSize > 0 && bindView != null) {
            this.dataSize = dataSize;
            onBindView = bindView;
            currentPosition = 0;
            preBindDataIndex = dataSize - 1;
            for (int i = 0; i < 3; i++) {
                int position = (dataSize + i - 1) % dataSize;
                ImageView imageView = (ImageView) getChildAt(i);
                sparseArray.put(imageView.hashCode(), position);
                onBindView.bind(imageView, position);
            }
        }
    }

    int preBindDataIndex;

    /**
     * 轮询绑定第一个imagview
     * 0 -> dataSize-1
     * dataSize-1 -> 0
     */
    private void bindNext(int step) {
        if (dataSize > 0 && onBindView != null) {
            preBindDataIndex += step;
            if (preBindDataIndex < 0) {
                preBindDataIndex = dataSize - 1;
            } else if (preBindDataIndex > dataSize - 1) {
                preBindDataIndex = 0;
            }
            ImageView imageView = (ImageView) getChildAt(0);
            sparseArray.put(imageView.hashCode(), preBindDataIndex);
            onBindView.bind(imageView, preBindDataIndex);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.layout((i - 1) * getWidth(), 0, i * getWidth(), getHeight());
        }
    }

    private boolean scrollIng;
    private float lastX;
    private float lastY;
    private float downX;
    private float downY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        requestDisallowInterceptTouchEvent(true);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                scrollIng = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    scrollIng = true;
                }
                downX = ev.getX();
                downY = ev.getY();
                lastX = downX;
                lastY = downY;
                onTouchEvent(ev);
                super.dispatchTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(lastX - ev.getX()) >= scaledTouchSlop
                        || Math.abs(lastY - ev.getY()) >= scaledTouchSlop) {
                    scrollIng = true;
                }
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(downX - ev.getX()) >= 5
                        || Math.abs(downY - ev.getY()) >= 5) {
                    scrollIng = true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                scrollIng = true;
                break;
        }

        if (!scrollIng) {
            super.dispatchTouchEvent(ev);
        } else {
            onTouchEvent(ev);
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(ev);
        int x = (int) ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = mLastX - x;
                scrollBy(dx, 0);
                mLastX = x;
                checkScroll();
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);
                int initVelocity = (int) mVelocityTracker.getXVelocity();
                recycleVelocityTracker();
                if (initVelocity > mMaxVelocity) {
                    scrollToPage(-1);
                } else if (initVelocity < -mMaxVelocity) {
                    scrollToPage(1);
                } else {
                    if (mScroller.getFinalX() - mScroller.getCurrX()
                            > (mScroller.getStartX() + mScroller.getFinalX()) / 2) {
                        scrollToPage(-1);
                    } else {
                        scrollToPage(1);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                scrollToPage(0);
                break;
        }
        return true;
    }


    /**
     * [-1, 0, 1]之间轮询
     * scrollX位移下标
     */
    int scrollIndex = 0;

    /**
     * 滑动到指定屏幕
     */
    private void scrollToPage(int offIndex) {

        if (dataSize <= 0) {
            return;
        }

        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }

        if (offIndex == 0) {
            mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, Math.abs(getScrollX()));
            invalidate();
            return;
        }

        checkScroll();
        scrollIndex += offIndex;
        scrollIndex = Math.min(scrollIndex, 1);
        scrollIndex = Math.max(scrollIndex, -1);

        int dx = scrollIndex * getWidth() - getScrollX();
        mScroller.startScroll(getScrollX(), 0, dx, 0, Math.abs(dx));
        invalidate();

        if (scrollIndex == 1) {
            currentPosition = sparseArray.get(getChildAt(2).hashCode(), -1);
        } else if (scrollIndex == -1) {
            currentPosition = sparseArray.get(getChildAt(0).hashCode(), -1);
        } else {
            currentPosition = sparseArray.get(getChildAt(1).hashCode(), -1);
        }
        if (onBindView != null) {
            onBindView.onSelected(currentPosition);
        }

    }

    private void checkScroll() {
        if (getScrollX() >= getWidth()) {
            getChildAt(0).bringToFront();
            scrollTo(0, 0);
            invalidate();
            bindNext(1);
        } else if (getScrollX() <= -getWidth()) {
            getChildAt(0).bringToFront();
            getChildAt(0).bringToFront();
            scrollTo(0, 0);
            invalidate();
            bindNext(-1);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    public interface OnBannerListener {

        void bind(ImageView imageView, int position);

        void onItemClick(int position);

        void onSelected(int position);

    }

} 