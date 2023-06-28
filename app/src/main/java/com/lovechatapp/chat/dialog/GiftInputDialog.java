package com.lovechatapp.chat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lovechatapp.chat.R;

/**
 * 输入礼物个数
 */
public class GiftInputDialog extends Dialog implements View.OnClickListener {

    public GiftInputDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_input_gift);
        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);

        setCanceledOnTouchOutside(true);
        setCancelable(true);

        findViewById(R.id.confirm_btn).setOnClickListener(this);
        findViewById(R.id.cancel_btn).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.confirm_btn) {
            EditText editText = findViewById(R.id.count_tv);
            String roomName = editText.getText().toString().trim();
            if (TextUtils.isEmpty(roomName)) {
                return;
            }
            try {
                int count = Integer.parseInt(roomName);
                if (count <= 0) {
                    return;
                }
                ok(count);
            } catch (Exception ignore) {
            }
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

    protected void ok(int count) {

    }

}