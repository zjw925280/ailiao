package com.lovechatapp.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


public class CodeTextView extends android.support.v7.widget.AppCompatTextView implements Runnable {

    private int second;

    private String text;

    private String format;

    private OnClickListener clickListener;

    public CodeTextView(Context context) {
        super(context);
    }

    public CodeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CodeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        clickListener = l;
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text == null) {
                    clickListener.onClick(v);
                }
            }
        });
    }

    @Override
    public void run() {
        second -= 1;
        if (second > 0) {
            countdownTime();
        } else {
            stop();
        }
    }

    public final void start(int second, String format) {
        if (format == null || !format.contains("%s")) {
            return;
        }
        stop();
        this.text = getText().toString();
        this.format = format;
        this.second = second;
        countdownTime();
    }

    private void stop() {
        if (text != null) {
            setText(text);
            text = null;
        }
        removeCallbacks(this);
    }

    /**
     * 验证码倒计时
     */
    private void countdownTime() {
        setText(String.format(format, second));
        postDelayed(this, 1000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }
}