package com.lovechatapp.chat.util.share;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.util.FileUtil;
import com.lovechatapp.chat.util.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 微信分享图片
 */
public class ShareWechatPic implements IShare {

    private View view;

    public ShareWechatPic(View view) {
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

        IWXAPI mWxApi = WXAPIFactory.createWXAPI(activity, Constant.WE_CHAT_APPID, true);
        mWxApi.registerApp(Constant.WE_CHAT_APPID);
        if (!mWxApi.isWXAppInstalled()) {
            ToastUtil.INSTANCE.showToast(R.string.not_install_we_chat);
            return;
        }
        WXImageObject wxImageObject = new WXImageObject();
        wxImageObject.setImagePath(picPath);
        WXMediaMessage msg = new WXMediaMessage(wxImageObject);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.scene = SendMessageToWX.Req.WXSceneSession;
        req.message = msg;
        req.transaction = String.valueOf(System.currentTimeMillis());
        mWxApi.sendReq(req);
        boolean res = mWxApi.sendReq(req);
        if (res) {
            AppManager.getInstance().setIsMainPageShareQun(false);
        }
    }
}