package com.lovechatapp.chat.util.share;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.util.FileUtil;
import com.lovechatapp.chat.util.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * QQ分享图片
 */
public class ShareQQPic implements IShare, IUiListener {

    private View view;

    public ShareQQPic(View view) {
        this.view = view;
    }

    @Override
    public void share(Activity activity) {

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.WHITE);

        int w = view.getWidth();
        int h = view.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        // 如果不设置canvas画布为白色，则生成透明
        view.layout(0, 0, w, h);
        view.draw(c);
        view.destroyDrawingCache();

        String picPath = null;
        FileUtil.checkDirection(Constant.ER_CODE);
        File file = new File(Constant.ER_CODE, "erCode.jpg");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
            picPath = file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (picPath == null) {
            ToastUtil.INSTANCE.showToast("分享失败，图片不可用");
            return;
        }

        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, picPath);
        Tencent tencent = Tencent.createInstance(Constant.QQ_APP_ID, activity);
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