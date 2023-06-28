package com.lovechatapp.chat.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述  可展开的TextView
 * 作者：
 * 创建时间：2018/8/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ExpandTextView extends android.support.v7.widget.AppCompatTextView {

    /**
     * true：展开，false：收起
     */
    boolean mExpanded;

    /**
     * 状态回调
     */
    Callback mCallback;

    /**
     * 源文字内容
     */
    String mText = "";

    /**
     * 最多展示的行数
     */
    final int maxLineCount = 4;

    private int labelLength;

    private int realLine;

    public ExpandTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setEllipsize(TextUtils.TruncateAt.END);
    }

    /**
     * 设置要显示的文字以及状态
     *
     * @param expanded true：展开，false：收起
     */
    public final void setText(String topic, String text, boolean expanded, Callback callback) {

        int topicLength = 0;
        String updateText = text;
        if (!TextUtils.isEmpty(topic)) {
            updateText = topic + updateText;
            topicLength = topic.length();
        }

        if (!TextUtils.isEmpty(updateText) && !TextUtils.isEmpty(mText) && mText.equals(updateText)) {
            return;
        }

        mText = updateText;

        labelLength = topicLength;

        mCallback = callback;

        setText(text);

        setSpan();

        mExpanded = expanded;

        if (getMeasuredWidth() != 0) {
            runnable.run();
        } else {
            post(runnable);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            realLine = getTextViewLines(ExpandTextView.this, getMeasuredWidth());
            setChanged(mExpanded);
        }
    };


    public final void setText(String text, boolean expanded, Callback callback) {
        setText(null, text, expanded, callback);
    }

    private void setSpan() {

        if (labelLength == 0)
            return;

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#fe2947"));
        SpannableString spannableString = new SpannableString(mText);
        spannableString.setSpan(colorSpan, 0, labelLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (mCallback != null) {
//                    mCallback.onClickLabel();
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, 0, labelLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        setText(spannableString);

        setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 展开收起状态变化
     */
    public void setChanged(boolean expanded) {
        mExpanded = expanded;
        if (expanded) {
            setMaxLines(Integer.MAX_VALUE);
        } else {
            setMaxLines(maxLineCount);
        }
        if (mCallback != null) {
            if (realLine <= maxLineCount) {
                mCallback.onLoss();
            } else if (mExpanded) {
                mCallback.onExpand();
            } else {
                mCallback.onCollapse();
            }
        }
    }

    public int getTextViewLines(TextView textView, int textViewWidth) {
        int width = textViewWidth - textView.getCompoundPaddingLeft() - textView.getCompoundPaddingRight();
        StaticLayout staticLayout;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            staticLayout = getStaticLayout23(textView, width);
        } else {
            staticLayout = getStaticLayout(textView, width);
        }
        return staticLayout.getLineCount();
    }

    /**
     * sdk>=23
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private StaticLayout getStaticLayout23(TextView textView, int width) {
        StaticLayout.Builder builder = StaticLayout.Builder.obtain(textView.getText(),
                0, TextUtils.isEmpty(mText) ? 0 : mText.length(), textView.getPaint(), width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setTextDirection(TextDirectionHeuristics.FIRSTSTRONG_LTR)
                .setLineSpacing(textView.getLineSpacingExtra(), textView.getLineSpacingMultiplier())
                .setIncludePad(textView.getIncludeFontPadding())
                .setBreakStrategy(textView.getBreakStrategy())
                .setHyphenationFrequency(textView.getHyphenationFrequency())
                .setMaxLines(Integer.MAX_VALUE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setJustificationMode(textView.getJustificationMode());
        }
        if (textView.getEllipsize() != null && textView.getKeyListener() == null) {
            builder.setEllipsize(textView.getEllipsize())
                    .setEllipsizedWidth(width);
        }
        return builder.build();
    }

    /**
     * sdk<23
     */
    private StaticLayout getStaticLayout(TextView textView, int width) {
        return new StaticLayout(textView.getText(),
                0, TextUtils.isEmpty(mText) ? 0 : mText.length(),
                textView.getPaint(), width, Layout.Alignment.ALIGN_NORMAL,
                textView.getLineSpacingMultiplier(),
                textView.getLineSpacingExtra(), textView.getIncludeFontPadding(), textView.getEllipsize(),
                width);
    }

    public interface Callback {

        /**
         * 展开状态
         */
        void onExpand();

        /**
         * 收起状态
         */
        void onCollapse();

        /**
         * 行数小于最小行数，不满足展开或者收起条件
         */
        void onLoss();

//        void onClickLabel();
    }

}