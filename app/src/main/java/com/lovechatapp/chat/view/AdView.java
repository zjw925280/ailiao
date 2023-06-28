package com.lovechatapp.chat.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseBean;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.dialog.AdmonitionDialog;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.view.recycle.BannerHolder;
import com.tencent.qcloud.tim.uikit.utils.ThreadHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class AdView extends FrameLayout {

    public AdView(Context context) {
        super(context);
        init();
    }

    public AdView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.ad_banner, this);
        setVisibility(View.GONE);

        findViewById(R.id.close_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup viewGroup = (ViewGroup) getParent();
                viewGroup.removeView(AdView.this);
            }
        });
    }

    public final void request(Activity activity) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", AppManager.getInstance().getUserInfo().t_id);
        params.put("t_banner_type", 0);
        OkHttpUtils
                .post()
                .url(ChatApi.getNewBannerList())
                .addParams("param", ParamUtil.getParam(params))
                .build()
                .execute(new AjaxCallback<BaseResponse<Response>>() {
                    @Override
                    public void onResponse(BaseResponse<Response> response, int id) {
                        if (activity.isFinishing() || response == null || response.m_object == null) {
                            return;
                        }
                        ImageView imageView = findViewById(R.id.content_iv);
                        if (imageView != null) {

                            //左侧悬浮
                            Banner suspensionMap = response.m_object.suspensionMap;
                            if (suspensionMap != null && suspensionMap.bannerStatus == 1) {
                                ThreadHelper.INST.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Drawable drawable = Glide.with(getContext())
                                                    .load(suspensionMap.bannerInfo.t_img_url)
                                                    .submit()
                                                    .get();
                                            if (drawable == null) {
                                                return;
                                            }
                                            post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    imageView.setImageDrawable(drawable);
                                                    setVisibility(View.VISIBLE);
                                                    imageView.setOnClickListener(new OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            BannerHolder.toIntent(getContext(), suspensionMap.bannerInfo.t_link_url);
                                                        }
                                                    });
                                                }
                                            });
                                        } catch (ExecutionException | InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }

                            //弹窗
                            Banner popupMap = response.m_object.popupMap;
                            if (popupMap != null && popupMap.bannerStatus == 1) {
                                new AdmonitionDialog(activity, popupMap.bannerInfo).show();
                            }
                        }
                    }
                });
    }

    public static class BannerInfo extends BaseBean {
        public String t_img_url;
        public String t_link_url;
    }

    static class Banner extends BaseBean {
        public int bannerStatus;
        public BannerInfo bannerInfo;
    }

    static class Response extends BaseBean {
        public Banner popupMap;
        public Banner suspensionMap;
    }

}