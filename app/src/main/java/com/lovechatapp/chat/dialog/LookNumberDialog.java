package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.ActorInfoBean;
import com.lovechatapp.chat.bean.ChargeBean;
import com.lovechatapp.chat.bean.CoverUrlBean;
import com.lovechatapp.chat.bean.InfoRoomBean;
import com.lovechatapp.chat.bean.LabelBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.DensityUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 查看number弹窗
 */
public class LookNumberDialog extends Dialog {

    //微信号
    public static final int LookWeixin = 0;

    //手机号
    public static final int LookPhone = 1;

    //QQ号
    public static final int LookQQ = 2;

    private int type;
    private int actorId;
    private ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean> mActorInfoBean;
    private Activity activity;
    private ProgressDialog progressDialog;

    @IntDef({LookWeixin, LookPhone, LookQQ})
    @Retention(RetentionPolicy.SOURCE)
    @interface LOOK_TYPE {
    }

    public LookNumberDialog(@NonNull Activity context,
                            ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean> bean,
                            @LOOK_TYPE int type,
                            int actorId) {
        super(context, R.style.DialogStyle_Dark_Background);
        this.type = type;
        this.mActorInfoBean = bean;
        this.actorId = actorId;
        activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_see_we_chat_number_layout);
        getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().getAttributes().horizontalMargin = DensityUtil.dip2px(getContext(), 20);
        setCanceledOnTouchOutside(false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("请稍候...");

        ChargeBean chargeBean;
        final float cost;
        if (mActorInfoBean != null && mActorInfoBean.anchorSetup != null && mActorInfoBean.anchorSetup.size() > 0) {
            chargeBean = mActorInfoBean.anchorSetup.get(0);
            //描述
            TextView see_des_tv = findViewById(R.id.see_des_tv);
            if (chargeBean != null) {
                switch (type) {
                    case 0:
                        cost = chargeBean.t_weixin_gold;
                        break;
                    case 1:
                        cost = chargeBean.t_phone_gold;
                        break;
                    default:
                        cost = chargeBean.t_qq_gold;
                        break;
                }
                final String typeStr;
                switch (type) {
                    case 0:
                        typeStr = "微信号";
                        break;
                    case 1:
                        typeStr = "手机号码";
                        break;
                    default:
                        typeStr = "QQ号码";
                        break;
                }
                String content = getContext().getString(R.string.see_info_number_des, typeStr, cost);
                see_des_tv.setText(content);
            } else {
                cost = 0;
            }
        } else {
            cost = 0;
        }

        //取消
        TextView cancel_tv = findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(v -> dismiss());

        //确定
        TextView confirm_tv = findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(v -> {
            if (cost > 0) {
                request(type);
            }
        });
    }

    @Override
    public void show() {

        if (activity == null || mActorInfoBean == null)
            return;

        if (!AppManager.getInstance().getUserInfo().isVip()) {
            new VipDialog(activity, "VIP用户才可查看联系方式").show();
            return;
        }

        //是否查看过
        boolean showNumber = false;

        //number值
        String number = null;

        switch (type) {
            case LookWeixin:
                showNumber = mActorInfoBean.isWeixin == 1;
                number = mActorInfoBean.t_weixin;
                break;
            case LookPhone:
                showNumber = mActorInfoBean.isPhone == 1;
                number = mActorInfoBean.t_phone;
                break;
            case LookQQ:
                showNumber = mActorInfoBean.isQQ == 1;
                number = mActorInfoBean.t_qq;
                break;
        }

        //展示number
        if (showNumber) {
            showNumber(getContext(), type, number);
        } else {
            super.show();
        }
    }

    /**
     * 查看号码
     */
    private void request(final int position) {
        progressDialog.show();
        String[] urls = {
                ChatApi.SEE_WEI_XIN_CONSUME(),
                ChatApi.SEE_PHONE_CONSUME(),
                ChatApi.SEE_QQ_CONSUME()
        };
        String url = urls[position];
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id + "");
        paramMap.put("coverConsumeUserId", String.valueOf(actorId));
        OkHttpUtils.post().url(url)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<String>>() {
                    @Override
                    public void onResponse(BaseResponse<String> response, int id) {
                        if (!isShowing()) {
                            return;
                        }
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        if (response != null) {
                            if (response.m_istatus == NetCode.SUCCESS || response.m_istatus == 2) {
                                String message = response.m_strMessage;
                                if (!TextUtils.isEmpty(message)) {
                                    ToastUtil.INSTANCE.showToast(message);
                                } else {
                                    if (response.m_istatus == 2) {
                                        ToastUtil.INSTANCE.showToast(R.string.vip_free);
                                    } else {
                                        ToastUtil.INSTANCE.showToast(R.string.pay_success);
                                    }
                                }
                                //更新数据
                                switch (type) {
                                    case LookWeixin:
                                        mActorInfoBean.isWeixin = 1;
                                        mActorInfoBean.t_weixin = response.m_object;
                                        break;
                                    case LookPhone:
                                        mActorInfoBean.isPhone = 1;
                                        mActorInfoBean.t_phone = response.m_object;
                                        break;
                                    case LookQQ:
                                        mActorInfoBean.isQQ = 1;
                                        mActorInfoBean.t_qq = response.m_object;
                                        break;
                                }
                                looked(type, response.m_object);
                            } else if (response.m_istatus == -1) {//余额不足
                                ToastUtil.INSTANCE.showToast("余额不足");
                            } else {
                                ToastUtil.INSTANCE.showToast(R.string.system_error);
                            }
                        } else {
                            ToastUtil.INSTANCE.showToast(R.string.system_error);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        ToastUtil.INSTANCE.showToast(R.string.system_error);
                        if (progressDialog != null)
                            progressDialog.dismiss();
                    }
                });
    }

    private final int[] titleIds = {
            R.string.we_chat_num_des_one,
            R.string.phone_num_one,
            R.string.qq_num_one
    };

    public void showNumber(Context context, int type, final String number) {
        new AlertDialog.Builder(context)
                .setTitle(titleIds[type])
                .setMessage(number)
                .setNegativeButton(R.string.copy, (dialog, which) -> {
                    dialog.dismiss();
                    boolean b = copy(number);
                    if (b) {
                        ToastUtil.INSTANCE.showToast(R.string.copy_success);
                    }
                })
                .setPositiveButton(R.string.confirm, null)
                .create().show();
    }


    /**
     * 复制内容到剪切板
     */
    private boolean copy(String copyStr) {
        try {
            ClipboardManager cm = (ClipboardManager) AppManager.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("Label", copyStr);
            cm.setPrimaryClip(mClipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void looked(int type, String number) {
        dismiss();
        showNumber(getContext(), type, number);
    }
}