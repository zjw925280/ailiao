package com.lovechatapp.chat.util.share;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.ShareUrlHelper;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;

import java.util.ArrayList;

/**
 * QQ空间分享
 */
public class ShareQZone implements IShare, IUiListener {

    @Override
    public void share(Activity activity) {
        String mShareUrl = ShareUrlHelper.getShareUrl(true);
        if (TextUtils.isEmpty(mShareUrl)) {
            return;
        }
        Tencent tencent = Tencent.createInstance(Constant.QQ_APP_ID, activity);
        String title = activity.getString(R.string.chat_title);
        String des = activity.getString(R.string.chat_des);
        String mineUrl = SharedPreferenceHelper.getAccountInfo(activity).headUrl;
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);// 标题
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, des);// 摘要
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, mShareUrl);// 内容地址
        ArrayList<String> images = new ArrayList<>();
        images.add(mineUrl);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, images);// 图片地址
        tencent.shareToQzone(activity, params, this);
    }

    @Override
    public void onComplete(Object o) {

    }

    @Override
    public void onError(UiError uiError) {

    }

    @Override
    public void onCancel() {

    }
}