package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.CompanyInviteBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 公会邀请
 */
public class CompanyInviteDialog extends Dialog {

    private String des;

    private int guildId;

    public CompanyInviteDialog(@NonNull Context context, String des, int guildId) {
        super(context, R.style.DialogStyle_Dark_Background);
        this.des = des;
        this.guildId = guildId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_join_company_layout);
        setCompanyInviteView(this, des, guildId);
    }

    private void setCompanyInviteView(final Dialog mDialog, String des, final int guildId) {
        //描述
        TextView see_des_tv = findViewById(R.id.des_tv);
        if (!TextUtils.isEmpty(des)) {
            see_des_tv.setText(des);
        }
        //关闭
        ImageView close_iv = findViewById(R.id.close_iv);
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //拒绝
        TextView reject_tv = findViewById(R.id.reject_tv);
        reject_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int reject = 0;
                joinCompany(guildId, reject);
                mDialog.dismiss();
            }
        });
        //接受
        TextView accept_tv = findViewById(R.id.accept_tv);
        accept_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int accept = 1;
                joinCompany(guildId, accept);
                mDialog.dismiss();
            }
        });
    }

    /**
     * 主播确认是否加入公会
     */
    private void joinCompany(int guildId, int isApply) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("guildId", String.valueOf(guildId));
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("isApply", String.valueOf(isApply));//是否加入公会 0.否 1.是
        OkHttpUtils.post().url(ChatApi.IS_APPLY_GUILD())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.INSTANCE.showToast(getContext(), R.string.operate_success);
                } else {
                    ToastUtil.INSTANCE.showToast(getContext(), R.string.operate_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getContext(), R.string.operate_fail);
            }

        });
    }

    /**
     * 拉取是否有人邀请主播加入公会
     */
    public static void getCompanyInvite(final Activity activity) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        OkHttpUtils.post().url(ChatApi.GET_ANCHOR_ADD_GUILD())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<CompanyInviteBean>>() {
            @Override
            public void onResponse(BaseResponse<CompanyInviteBean> response, int id) {
                if (activity.isFinishing())
                    return;
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    CompanyInviteBean inviteBean = response.m_object;
                    if (inviteBean != null && inviteBean.t_id > 0) {
                        String content = inviteBean.t_admin_name + activity.getResources().getString(R.string.invite_you)
                                + inviteBean.t_guild_name + activity.getResources().getString(R.string.company);
                        new CompanyInviteDialog(activity, content, inviteBean.t_id).show();
                    }
                }
            }
        });
    }
}