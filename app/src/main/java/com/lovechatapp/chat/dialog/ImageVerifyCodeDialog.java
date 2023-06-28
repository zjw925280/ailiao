package com.lovechatapp.chat.dialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 图形验证码dialog
 */
public class ImageVerifyCodeDialog extends DialogFragment {

    private String mPassPhone = "";

    //传过来的电话
    public static final String PHONE = "phone";

    public static final String TYPE = "type";

    private int mType = 1;//发送短信类型:1.注册验证码 2.修改手机号 3.忘记密码(修改密码)

    private BaseActivity mActivity;

    private OnCommonListener onCommonListener;

    public static ImageVerifyCodeDialog showDialog(FragmentActivity activity, String phone, int sendSmsType) {
        Bundle bundle = new Bundle();
        bundle.putString(PHONE, phone);
        bundle.putInt(TYPE, sendSmsType);
        ImageVerifyCodeDialog userDialog = new ImageVerifyCodeDialog();
        userDialog.setArguments(bundle);
        userDialog.show(activity.getSupportFragmentManager(), "ImageVerifyCodeDialog");
        return userDialog;
    }

    public final void setOnOptionSuccessListener(OnCommonListener onCommonListener) {
        this.onCommonListener = onCommonListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_image_verify_code_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActivity = (BaseActivity) getActivity();

        //电话
        Bundle bundle = getArguments();
        if (bundle != null) {
            mPassPhone = bundle.getString(PHONE);
            mType = bundle.getInt(TYPE);
        }

        //关闭
        ImageView cancel_iv = view.findViewById(R.id.cancel_iv);
        cancel_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //图片
        final ImageView code_iv = view.findViewById(R.id.code_iv);
        final String url = ChatApi.GET_VERIFY() + mPassPhone;

        //加载
        Glide.with(mActivity)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(code_iv);

        //点击换一张
        TextView change_tv = view.findViewById(R.id.change_tv);
        change_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(mActivity)
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(code_iv);
            }
        });

        //验证码
        final EditText code_et = view.findViewById(R.id.code_et);

        //确认
        view.findViewById(R.id.confirm_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = code_et.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.INSTANCE.showToast(mActivity, R.string.please_input_image_code);
                    return;
                }
                checkVerifyCode(code, mPassPhone);
                dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            // 一定要设置Background，如果不设置，window属性设置无效
            window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
            DisplayMetrics dm = new DisplayMetrics();
            if (getActivity() != null) {
                WindowManager windowManager = getActivity().getWindowManager();
                if (windowManager != null) {
                    windowManager.getDefaultDisplay().getMetrics(dm);
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.gravity = Gravity.CENTER;
                    // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    window.setAttributes(params);
                }
            }
        }
    }

    /**
     * 验证图形验证码是否正确
     */
    private void checkVerifyCode(final String code, String phone) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("phone", phone);
        paramMap.put("verifyCode", code);
        OkHttpUtils.post()
                .url(ChatApi.GET_VERIFY_CODE_IS_CORRECT())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build()
                .execute(new AjaxCallback<BaseResponse>() {

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        mActivity.showLoadingDialog();
                    }

                    @Override
                    public void onResponse(BaseResponse response, int id) {
                        if (response.m_istatus == NetCode.SUCCESS) {
                            sendSmsVerifyCode(code);
                        } else {
                            mActivity.dismissLoadingDialog();
                            ToastUtil.INSTANCE.showToast(mActivity, R.string.wrong_image_code);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                        ToastUtil.INSTANCE.showToast(mActivity, R.string.wrong_image_code);
                        mActivity.dismissLoadingDialog();
                    }
                });
    }

    /**
     * 发送短信验证码
     */
    private void sendSmsVerifyCode(String imageCode) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", mActivity.getUserId());
        paramMap.put("phone", mPassPhone);
        paramMap.put("resType", mType);//发送类型:1.注册验证码 2.修改手机号 3.忘记密码(修改密码)
        paramMap.put("verifyCode", imageCode);
        OkHttpUtils.post()
                .url(ChatApi.SEND_SMS_CODE())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build()
                .execute(new AjaxCallback<BaseResponse>() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.INSTANCE.showToast(mActivity, R.string.send_code_fail);
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        mActivity.dismissLoadingDialog();
                    }

                    @Override
                    public void onResponse(BaseResponse response, int id) {
                        if (response.m_istatus == NetCode.SUCCESS) {
                            if (onCommonListener != null) {
                                onCommonListener.execute(null);
                            }
                        } else {
                            String message = response.m_strMessage;
                            if (!TextUtils.isEmpty(message)) {
                                ToastUtil.INSTANCE.showToast(mActivity, message);
                            } else {
                                ToastUtil.INSTANCE.showToast(mActivity, R.string.send_code_fail);
                            }
                        }
                    }
                });
    }

}
