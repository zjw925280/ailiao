package com.lovechatapp.chat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.CommonWebViewActivity;
import com.lovechatapp.chat.util.DensityUtil;
import com.lovechatapp.chat.view.AdView;
import com.tencent.qcloud.tim.uikit.utils.ThreadHelper;

import java.util.concurrent.ExecutionException;

/**
 * 严禁条约弹窗
 */
public class AdmonitionDialog extends Dialog implements View.OnClickListener {

    private final AdView.BannerInfo bannerInfo;
    private Drawable drawable;

    public AdmonitionDialog(@NonNull Context context, AdView.BannerInfo bannerInfo) {
        super(context);
        this.bannerInfo = bannerInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_admonition);

        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.setAttributes(lp);

        findViewById(R.id.detail_btn).setOnClickListener(this);
        findViewById(R.id.confirm_btn).setOnClickListener(this);

        setCancelable(false);

        ImageView contentIv = findViewById(R.id.content_iv);
        contentIv.setImageDrawable(drawable);

        View root = findViewById(R.id.root);
        root.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                Rect rect = new Rect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                outline.setRoundRect(rect, DensityUtil.dip2px(getContext(), 5));
            }
        });
        root.setClipToOutline(true);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.detail_btn) {
            CommonWebViewActivity.start(getContext(), "甛觅Mru", bannerInfo.t_link_url);
        }
        dismiss();
    }

    @Override
    public void show() {
        ThreadHelper.INST.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    drawable = Glide.with(getContext()).load(bannerInfo.t_img_url).submit().get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                if (drawable != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            AdmonitionDialog.super.show();
                        }
                    });
                }
            }
        });
    }
}