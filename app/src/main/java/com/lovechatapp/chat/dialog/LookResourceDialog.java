package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.ActorVideoPlayActivity;
import com.lovechatapp.chat.activity.PhotoViewActivity;
import com.lovechatapp.chat.activity.VipCenterActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.ActiveFileBean;
import com.lovechatapp.chat.bean.AlbumBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.helper.ChargeHelper;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 查看资源
 */
public class LookResourceDialog extends Dialog {

    private Activity mContext;
    public static Map<String, Object> paramMap;
    public static String method;
    private SpannableString description;
    private OnCommonListener<Boolean> listener;

    private LookResourceDialog(Activity context,
                               Map<String, Object> paramMap,
                               String method,
                               SpannableString description,
                               OnCommonListener<Boolean> listener) {
        super(context);
        this.mContext = context;
        this.paramMap = paramMap;
        this.paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        this.method = method;
        this.description = description;
        this.listener = listener;
    }

    /**
     * 查看私密视频或者照片
     *
     */
    public static void showAlbum(Activity activity, final AlbumBean bean, int otherId, final OnCommonListener<Boolean> commonListener) {
        if (bean != null) {
            OnCommonListener<Boolean> listener = new OnCommonListener<Boolean>() {
                @Override
                public void execute(Boolean aBoolean) {
                    if (aBoolean) {
                        bean.is_see = 1;
                    }
                    if (commonListener != null) {
                        commonListener.execute(aBoolean);
                    }
                }
            };

            String method1 = bean.t_file_type == 1 ? ChatApi.SEE_VIDEO_CONSUME() : ChatApi.SEE_IMAGE_CONSUME();
            Map<String, Object> paramMap1 = new HashMap<>();
            paramMap1.put(bean.t_file_type == 1 ? "videoId" : "photoId", bean.t_id);
            paramMap1.put("coverConsumeUserId", otherId);
            if (bean.t_file_type == 1 ){
                paramMap1.put("userId", AppManager.getInstance().getUserInfo().t_id);
                OkHttpUtils.post()
                        .url(method1)
                        .addParams("param", ParamUtil.getParam(paramMap1))
                        .build().execute(new AjaxCallback<BaseResponse>() {
                            @Override
                            public void onResponse(BaseResponse response, int id) {
                                Log.e("支付是否成功","成功="+new Gson().toJson(response));
                                if (activity == null || activity.isFinishing())
                                    return;
                                boolean ok = false;
                                if (response != null) {
                                    if (response.m_istatus == NetCode.SUCCESS || response.m_istatus == 2) {
                                        ok = true;
                                        if (response.m_istatus == 2) {
                                            ToastUtil.INSTANCE.showToast(activity, "无需支付");
                                        }
                                    } else if (response.m_istatus == -1) {//余额不足
                                        ChargeHelper.showSetCoverDialog(activity);
                                    } else {
                                        ToastUtil.INSTANCE.showToast(activity, response.m_strMessage);
                                    }
                                } else {
                                    ToastUtil.INSTANCE.showToast(activity, R.string.system_error);
                                }
                                notify(ok);
                            }

                            @Override
                            public void onError(Call call, Exception e, int id) {
                                if (activity == null || activity.isFinishing())
                                    return;
                                super.onError(call, e, id);
                                Log.e("支付是否成功","错误="+e.getMessage());
                                listener.execute(false);
                                ToastUtil.INSTANCE.showToast(activity, R.string.system_error);
                            }

                            private void notify(boolean ok) {
                                if (listener != null) {
                                    listener.execute(ok);
                                }
                            }
                        });

            }else {
                new LookResourceDialog(
                        activity,
                        paramMap1,
                        method1,
                        buildDescription(bean.t_file_type == 1, bean.t_money),
                        listener).show();
            }

        }
    }

    /**
     * 查看动态
     */
    public static void showActive(final Activity activity,
                                  final List<ActiveFileBean> fileBeans,
                                  final int otherId,
                                  final int index) {
        showActive(activity, fileBeans, otherId, index, null);
    }

    /**
     * 查看动态
     */
    public static void showActive(final Activity activity,
                                  final List<ActiveFileBean> fileBeans,
                                  final int otherId,
                                  final int index,
                                  final OnCommonListener<ActiveFileBean> onCommonListener) {

        if (fileBeans == null || fileBeans.size() == 0 && index >= fileBeans.size()) {
            return;
        }

        final ActiveFileBean bean = fileBeans.get(index);

        LookBack listener = new LookBack() {

            @Override
            public void execute(Boolean aBoolean) {

                if (activity.isFinishing()) {
                    return;
                }

                if (aBoolean) {
                    bean.isConsume = 1;
                }

                if (onCommonListener != null) {
                    if (aBoolean) {
                        onCommonListener.execute(bean);
                    }
                } else if ((localExecute || aBoolean)) {
                    if (bean.t_file_type == 1) {
                        ActorVideoPlayActivity.start(activity, otherId, bean.t_file_url);
                    } else {
                        PhotoViewActivity.start(activity, fileBeans, index, otherId);
                    }
                }
            }
        };

        if (bean.judgePrivate(otherId)) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("fileId", bean.t_id);
            new LookResourceDialog(
                    activity,
                    paramMap,
                    ChatApi.DYNAMIC_PAY(),
                    buildDescription(bean.t_file_type == 1, bean.t_gold),
                    listener).show();
        } else {
            listener.localExecute = true;
            listener.execute(true);
        }
    }

    private static SpannableString buildDescription(boolean isVideo, int gold) {
        String desc = isVideo ? "查看本视频需要支付 %s约豆 哦!" : "查看本图片需要支付 %s约豆 哦!";
        int index = desc.indexOf("%s");
        desc = String.format(desc, gold);
        SpannableString description = new SpannableString(desc);
        description.setSpan(new ForegroundColorSpan(AppManager.getInstance().getResources().getColor(R.color.main)),
                index,
                index + String.valueOf(gold).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return description;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pay_video_layout);
        if (getWindow() == null) {
            return;
        }
        getWindow().setGravity(Gravity.CENTER);

        //描述
        TextView desTv = findViewById(R.id.des_tv);
        desTv.setText(description);

        //升级
        findViewById(R.id.vip_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), VipCenterActivity.class);
                mContext.startActivity(intent);
                dismiss();
            }
        });

        //取消
        TextView cancel_tv = findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //确定
        TextView confirm_tv = findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpUtils.post()
                        .url(method)
                        .addParams("param", ParamUtil.getParam(paramMap))
                        .build().execute(ajaxCallback);
                dismiss();
            }
        });
    }

    private AjaxCallback<BaseResponse> ajaxCallback = new AjaxCallback<BaseResponse>() {
        @Override
        public void onResponse(BaseResponse response, int id) {
            if (mContext == null || mContext.isFinishing())
                return;
            boolean ok = false;
            if (response != null) {
                if (response.m_istatus == NetCode.SUCCESS || response.m_istatus == 2) {
                    ok = true;
                    if (response.m_istatus == 2) {
                        ToastUtil.INSTANCE.showToast(mContext, "无需支付");
                    }
                } else if (response.m_istatus == -1) {//余额不足
                    ChargeHelper.showSetCoverDialog(mContext);
                } else {
                    ToastUtil.INSTANCE.showToast(mContext, response.m_strMessage);
                }
            } else {
                ToastUtil.INSTANCE.showToast(mContext, R.string.system_error);
            }
            notify(ok);
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            if (mContext == null || mContext.isFinishing())
                return;
            super.onError(call, e, id);
            listener.execute(false);
            ToastUtil.INSTANCE.showToast(mContext, R.string.system_error);
        }

        private void notify(boolean ok) {
            if (listener != null) {
                listener.execute(ok);
            }
        }
    };


    private static abstract class LookBack implements OnCommonListener<Boolean> {
        boolean localExecute;
    }
}