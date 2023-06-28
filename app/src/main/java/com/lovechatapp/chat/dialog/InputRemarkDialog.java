package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.lovechatapp.chat.R;

/**
 * 输入备注
 */
public class InputRemarkDialog extends Dialog implements View.OnClickListener {

    public InputRemarkDialog(@NonNull Activity context) {
        super(context);
    }

    public InputRemarkDialog(@NonNull Activity context, String title, String hint) {
        super(context);
        userTitle = title;
        userHint = hint;
    }

    public InputRemarkDialog(@NonNull Activity context, String title, String hint, int type) {
        super(context);
        userTitle = title;
        userHint = hint;
        inputType = type;
    }

    private String userTitle = "";
    private String userHint = "";
    private int inputType = InputType.TYPE_CLASS_TEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_input_remark);
        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.setAttributes(lp);
        if (!userTitle.isEmpty()) {
            ((TextView) findViewById(R.id.title_tv)).setText(userTitle);
        }
        EditText inputView = (EditText) findViewById(R.id.name_tv);
        if (!userHint.isEmpty()) {
            inputView.setHint(userHint);
        }
        if (inputType == InputType.TYPE_CLASS_PHONE) {
            inputType = InputType.TYPE_CLASS_NUMBER;
        }
        inputView.setInputType(inputType);

        findViewById(R.id.confirm_btn).setOnClickListener(this);
        findViewById(R.id.cancel_btn).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.confirm_btn) {
            EditText editText = findViewById(R.id.name_tv);
            String text = editText.getText().toString().trim();
            if (TextUtils.isEmpty(text)) {
                return;
            }
            remark(text);
        }
        dismiss();
    }

    @Override
    public void dismiss() {
        if (getWindow() != null && getWindow().getCurrentFocus() != null) {
            ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        super.dismiss();
    }

    protected void remark(String text) {

    }

}