package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.LabelRecyclerAdapter;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseListResponse;
import com.lovechatapp.chat.bean.LabelBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 选择标签
 */
public class ChooseLabelDialog extends Dialog {

    private final Activity activity;

    private List<LabelBean> mLabelBeans;

    public ChooseLabelDialog(@NonNull Activity context) {
        super(context, R.style.DialogStyle_Dark_Background);
        activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_label_list_layout);
        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setGravity(Gravity.BOTTOM);
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.setAttributes(lp);

        ImageView close_iv = findViewById(R.id.close_iv);
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        RecyclerView content_rv = findViewById(R.id.content_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        content_rv.setLayoutManager(gridLayoutManager);
        final LabelRecyclerAdapter adapter = new LabelRecyclerAdapter(getContext());
        content_rv.setAdapter(adapter);

        //随机选两个
        Random random = new Random();
        for (LabelBean bean : mLabelBeans) {
            bean.selected = false;
        }
        int r = random.nextInt(mLabelBeans.size());
        int r1 = random.nextInt(mLabelBeans.size());
        mLabelBeans.get(r).selected = true;
        if (r != r1) {
            mLabelBeans.get(r1).selected = true;
        } else {
            if (r1 + 1 == mLabelBeans.size()) {
                mLabelBeans.get(r1 - 1).selected = true;
            } else {
                mLabelBeans.get(r1 + 1).selected = true;
            }
        }
        adapter.loadData(mLabelBeans);

        TextView confirm_tv = findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<LabelBean> beans = adapter.getSelectedLabels();
                if (beans != null) {
                    if (beans.size() == 0) {
                        ToastUtil.INSTANCE.showToast(R.string.tags_not_select);
                    } else if (beans.size() <= 2) {
                        selected(beans);
                        dismiss();
                    }
                }

            }
        });

    }

    /**
     * 选中
     */
    protected void selected(List<LabelBean> beans) {
    }

    @Override
    public void show() {
        getLabelList();
    }

    /**
     * 获取标签列表
     * useType 1:编辑资料  2:评论列表
     */
    private void getLabelList() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("useType", 1);
        OkHttpUtils.post().url(ChatApi.GET_LABEL_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<LabelBean>>() {
            @Override
            public void onResponse(BaseListResponse<LabelBean> response, int id) {
                if (activity.isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<LabelBean> beans = response.m_object;
                    if (beans != null && beans.size() > 0) {
                        mLabelBeans = beans;
                        ChooseLabelDialog.super.show();
                    }
                }
            }
        });
    }
}