package com.lovechatapp.chat.activity;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.ErWeiBean;
import com.lovechatapp.chat.bean.PosterBean;
import com.lovechatapp.chat.dialog.ShareDialog;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.helper.ShareUrlHelper;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.ZXingUtils;
import com.lovechatapp.chat.util.share.ShareCopyUrl;
import com.lovechatapp.chat.util.share.ShareQQ;
import com.lovechatapp.chat.util.share.ShareQQPic;
import com.lovechatapp.chat.util.share.ShareQZone;
import com.lovechatapp.chat.util.share.ShareWechatCircle;
import com.lovechatapp.chat.util.share.ShareWechatGraphic;
import com.lovechatapp.chat.util.share.ShareWechatPic;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：二维码页面
 * 作者：
 * 创建时间：2018/10/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ErWeiCodeActivity extends BaseActivity {

    @BindView(R.id.content_fl)
    FrameLayout mContentFl;

    @BindView(R.id.content_iv)
    ImageView mContentIv;

    @BindView(R.id.code_iv)
    ImageView mCodeIv;

    private String mShareUrl;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_er_wei_code_layout);
    }

    @Override
    protected void onContentAdded() {

        setTitle(R.string.my_code);

        //右上角
        setRightText(R.string.share_now);
        initShare();
    }

    /**
     * 初始化分享
     */
    private void initShare() {
        String saveUrl = SharedPreferenceHelper.getShareUrl(getApplicationContext());
        String savePath = SharedPreferenceHelper.getShareImage(getApplicationContext());
        if (!TextUtils.isEmpty(saveUrl) && !TextUtils.isEmpty(savePath)) {
            mShareUrl = saveUrl;
            createCode(mShareUrl);
            ImageLoadHelper.glideShowImageWithUrl(ErWeiCodeActivity.this, savePath, mContentIv);
        }
        getShareUrl();
    }

    /**
     * 获取分享链接
     */
    private void getShareUrl() {
        ShareUrlHelper.getShareUrl(new OnCommonListener<ErWeiBean<PosterBean>>() {
            @Override
            public void execute(ErWeiBean<PosterBean> bean) {
                if (!isFinishing() && bean != null) {
                    mShareUrl = bean.shareUrl;
                    createCode(mShareUrl);
                    SharedPreferenceHelper.saveShareUrl(getApplicationContext(), mShareUrl);
                }
            }
        });
    }

    /**
     * 生成二维码
     */
    private void createCode(String codeUrl) {
        try {
            int width = DevicesUtil.dp2px(getApplicationContext(), 160);
            int height = DevicesUtil.dp2px(getApplicationContext(), 160);
            final Bitmap codeBitmap = ZXingUtils.createQRImage(codeUrl, width, height);
            if (codeBitmap != null) {
                mCodeIv.setImageBitmap(codeBitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.right_text})
    public void onClick(View v) {
        //分享
        if (v.getId() == R.id.right_text) {
            new ShareDialog(mContext, Arrays.asList(
                    new ShareDialog.ShareInfo(R.drawable.share_we_chat, "微信好友", new ShareWechatGraphic()),
                    new ShareDialog.ShareInfo(R.drawable.share_we_circle, "微信朋友圈", new ShareWechatCircle()),
                    new ShareDialog.ShareInfo(R.drawable.share_qq, "QQ", new ShareQQ()),
                    new ShareDialog.ShareInfo(R.drawable.share_qq_zone, "QQ空间", new ShareQZone()),
                    new ShareDialog.ShareInfo(R.drawable.share_we_chat, "分享图片", new ShareWechatPic(mContentFl)),
                    new ShareDialog.ShareInfo(R.drawable.share_qq, "分享图片", new ShareQQPic(mContentFl)),
                    new ShareDialog.ShareInfo(R.drawable.share_copy, "复制链接", new ShareCopyUrl())
            )).show();
        }
    }

}