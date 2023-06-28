package com.lovechatapp.chat.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.util.AttributeSet;

import com.lovechatapp.chat.R;

import java.util.ArrayList;
import java.util.List;

public class PwdEditText extends AppCompatEditText {

    private Paint backPaint, textPaint;
    private Context mC;
    private int spzceX;
    private int wide;//每个的宽
    private String mText;
    private int textLength;
    private List<RectF> rectFS;

    public PwdEditText(Context context) {
        super(context);
        mC = context;
        init();
    }

    public PwdEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mC = context;
        init();
    }

    public PwdEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mC = context;
        init();
    }

    /**
     * 输入监听
     */
    public interface OnTextChangeListeven {
        void onTextChange(String pwd);
    }

    private OnTextChangeListeven onTextChangeListeven;

    public void setOnTextChangeListeven(OnTextChangeListeven onTextChangeListeven) {
        this.onTextChangeListeven = onTextChangeListeven;
    }

    public void clearText() {
        setText("");
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
    }

    private void init() {
        setTextColor(0X00ffffff);
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        spzceX = dp2px(4);
        wide = dp2px(52);
        //背景色画笔
        backPaint = new Paint();
        backPaint.setAntiAlias(true);
        backPaint.setStyle(Paint.Style.FILL);
        backPaint.setColor(getResources().getColor(R.color.pink_FFD4DB));
        //文字的画笔
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(18);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(getResources().getColor(R.color.black_333333));

        rectFS = new ArrayList<>();

        mText = "";
        textLength = 4;

        this.setBackgroundDrawable(null);
        setLongClickable(false);
        setTextIsSelectable(false);
        setCursorVisible(false);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (mText == null) {
            return;
        }
        //如果字数不超过用户设置的总字数，就赋值给成员变量mText；
        // 如果字数大于用户设置的总字数，就只保留用户设置的那几位数字，并把光标制动到最后，让用户可以删除；
        if (text.toString().length() <= textLength) {
            mText = text.toString();
        } else {
            setText(mText);
            setSelection(getText().toString().length());  //光标制动到最后
            //setText(mText)之后键盘会还原，再次把键盘设置为数字键盘；
            setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        }
        if (onTextChangeListeven != null) {
            onTextChangeListeven.onTextChange(mText);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = wide + 20;
        int widthSize = 4 * (wide + spzceX);
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < textLength; i++) {
            //RectF的参数(left,  top,  right,  bottom); 画出每个矩形框并设置间距，间距其实是增加左边框距离，缩小上下右边框距离；
            //四个值，分别代表4条线，距离起点位置的线
            @SuppressLint("DrawAllocation")
            RectF rect = new RectF(i * wide + spzceX, 0, i * wide + wide, wide);
            canvas.drawRoundRect(rect, 9, 9, backPaint); //绘制背景色
            rectFS.add(rect);
        }
        //画密码圆点
        for (int j = 0; j < mText.length(); j++) {
            canvas.drawCircle(rectFS.get(j).centerX(), rectFS.get(j).centerY(), dp2px(7), textPaint);
        }
    }

    private int dp2px(float dpValue) {
        float scale = mC.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
