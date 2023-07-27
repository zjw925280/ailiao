package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseBean;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

public class FreeDialog extends Dialog implements View.OnClickListener {

        Activity activity;
        FreeImBean freeImBean;

public FreeDialog(@NonNull Activity context) {
        super(context);
        activity = context;
        }

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_free_im);

        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setGravity(Gravity.CENTER);
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.setAttributes(lp);

        setCanceledOnTouchOutside(true);
        setCancelable(true);

        LinearLayout lin_jiangli = findViewById(R.id.lin_jiangli);
        lin_jiangli.setVisibility(View.VISIBLE);
        LinearLayout lin_qiandao = findViewById(R.id.lin_qiandao);
        lin_qiandao.setVisibility(View.GONE);

        TextView textView = findViewById(R.id.info_tv);
        textView.setText("");
        if (freeImBean.isCase) {
        textView.setText(String.format("私信条数+%s", freeImBean.number));
        }
        if (freeImBean.isGold) {
        textView.append(String.format(freeImBean.isCase ? "  金币+%s" : "金币+%s", freeImBean.goldNumber));
        }
        findViewById(R.id.confirm_tv).setOnClickListener(this);
        }

@Override
public void show() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", AppManager.getInstance().getUserInfo().t_id);
        OkHttpUtils
        .post()
        .url(ChatApi.privateLetterNumber())
        .addParams("param", ParamUtil.getParam(params))
        .build()
        .execute(new AjaxCallback<BaseResponse<FreeImBean>>() {
@Override
public void onResponse(BaseResponse<FreeImBean> response, int id) {
        if (activity == null
        || activity.isFinishing()
        || response == null
        || response.m_object == null) {
        return;
        }
        freeImBean = response.m_object;
        if (freeImBean.isCase || freeImBean.isGold) {
            FreeDialog.super.show();

        }
        }
        });
        }

@Override
public void onClick(View v) {
        dismiss();
        }

private static class FreeImBean extends BaseBean {
    /**
     * isCase : true
     * number : 12154545
     */
    public boolean isCase;
    public String number;
    public boolean isGold;
    public String goldNumber;
}

}
