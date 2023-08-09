package com.lovechatapp.chat.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.SignInActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseBean;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.SiginBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 非vip用户领取免费消息数
 */
public class FreeImDialog extends Dialog implements View.OnClickListener {

    Activity activity;
    private TextView tv_total;
    private TextView  tv_day;
    public static SiginBean m_object;

    public FreeImDialog(@NonNull Activity context) {
        super(context);
        activity = context;
    }

    @SuppressLint("MissingInflatedId")
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
        findViewById(R.id.imge_qx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        findViewById(R.id.tv_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.startActivity(new Intent(activity, SignInActivity.class));
                dismiss();
            }
        });
        LinearLayout lin_jiangli = findViewById(R.id.lin_jiangli);
        lin_jiangli.setVisibility(View.GONE);
        LinearLayout lin_qiandao = findViewById(R.id.lin_qiandao);
        lin_qiandao.setVisibility(View.VISIBLE);
         tv_total = findViewById(R.id.tv_total);
         tv_day = findViewById(R.id.tv_day);
        tv_total.setText(m_object.getSignInList().size()+"");
        tv_day.setText("第"+m_object.getDay()+"天");
//        TextView textView = findViewById(R.id.info_tv);
//        textView.setText("");
//        if (freeImBean.isCase) {
//            textView.setText(String.format("私信条数+%s", freeImBean.number));
//        }
//        if (freeImBean.isGold) {
//            textView.append(String.format(freeImBean.isCase ? "  约豆+%s" : "约豆+%s", freeImBean.goldNumber));
//        }
//        findViewById(R.id.confirm_tv).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dismiss();
////                activity.startActivity(new Intent(activity, SignInActivity.class));
//            }
//        });
    }

    @Override
    public void show() {
//        super.show();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", AppManager.getInstance().getUserInfo().t_id);
        OkHttpUtils
                .post()
                .url(ChatApi.SIGIN_NOW())
                .addParams("param", ParamUtil.getParam(params))
                .build()
                .execute(new AjaxCallback<BaseResponse<String>>() {
                    @Override
                    public void onResponse(BaseResponse<String> response, int id) {
                        SiginBean siginBean =new Gson().fromJson(response.m_object, SiginBean.class);;
                        m_object=siginBean;
                        if (response.m_istatus==1){
                            FreeImDialog.super.show();
                        }
                    }
                });

//                Map<String, Object> params1 = new HashMap<>();
//        params1.put("userId", AppManager.getInstance().getUserInfo().t_id);
//        OkHttpUtils
//                .post()
//                .url(ChatApi.SIGNIN_LIST())
//                .addParams("param", ParamUtil.getParam(params1))
//                .build()
//                .execute(new AjaxCallback<BaseResponse<SigninDayBean>>() {
//                    @Override
//                    public void onResponse(BaseResponse<SigninDayBean> response, int id) {
//                         m_object = response.m_object;
//                    }
//                });

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