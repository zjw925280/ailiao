package com.lovechatapp.chat.util.share;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.ShareUrlHelper;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;

/**
 * QQ图片分享
 */
public class ShareQQ implements IShare, IUiListener {

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
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);// 标题
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, des);// 摘要
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mShareUrl);// 内容地址
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mineUrl);// 图片地址
        tencent.shareToQQ(activity, params, this);
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