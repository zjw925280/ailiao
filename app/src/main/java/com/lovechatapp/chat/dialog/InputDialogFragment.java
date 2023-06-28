package com.lovechatapp.chat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.lovechatapp.chat.R;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：大房间输入文字Dialog
 * 作者：
 * 创建时间：2018/10/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class InputDialogFragment extends DialogFragment {

    private EditText input_et;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_input_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setCancelable(false);

        input_et = view.findViewById(R.id.input_et);
        TextView btn_send = view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnTextSendListener != null) {
                    String inputText = input_et.getText().toString().trim();
                    mOnTextSendListener.onTextSend(inputText);
                }
            }
        });

        input_et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mOnTextSendListener != null) {
                        String inputText = input_et.getText().toString().trim();
                        mOnTextSendListener.onTextSend(inputText);
                    }
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });


        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (input_et.requestFocus()) {
                    if (getContext() != null) {
                        InputMethodManager imm = (InputMethodManager) getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.showSoftInput(input_et, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                }
            }
        }, 100);

    }

    @Override
    public void dismiss() {
        hideInput();
        super.dismiss();
    }

    private void hideInput() {
        ((InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(input_et.getWindowToken(), 0);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(requireContext(), getTheme()) {
            @Override
            public boolean onTouchEvent(@NonNull MotionEvent event) {
                if (isOutOfBounds(getContext(), event)) {
                    onTouchOutside();
                    return true;
                }
                return super.onTouchEvent(event);
            }
        };
    }

    protected void onTouchOutside() {
        dismiss();
    }

    private boolean isOutOfBounds(Context context, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        final View decorView = getView();
        if (decorView == null) {
            return false;
        }
        return (x < -slop) || (y < -slop)
                || (x > (decorView.getWidth() + slop))
                || (y > (decorView.getHeight() + slop));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
            if (getActivity() != null) {
                WindowManager windowManager = getActivity().getWindowManager();
                if (windowManager != null) {
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.gravity = Gravity.BOTTOM;
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    window.getDecorView().setPadding(0, 0, 0, 0);
                    window.setAttributes(params);
                }
            }
        }
    }

    /**
     * 发送回调
     */
    public interface OnTextSendListener {
        void onTextSend(String text);
    }

    private OnTextSendListener mOnTextSendListener;

    public void setOnTextSendListener(OnTextSendListener onTextSendListener) {
        mOnTextSendListener = onTextSendListener;
    }

}