package com.lovechatapp.chat.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.ShareRecyclerAdapter;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseBean;
import com.lovechatapp.chat.bean.ShareLayoutBean;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.util.BitmapUtil;
import com.lovechatapp.chat.util.DialogUtil;
import com.lovechatapp.chat.util.FileUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.util.permission.PermissionUtil;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShareActivity extends Activity {

    //分享
    private Tencent mTencent;
    private IWXAPI mWxApi;
    private ShareParams shareParams;
    private List<ShareLayoutBean> beans = new ArrayList<>();

    //加载dialog
    private Dialog mDialogLoading;

    public static void start(Context context, ShareParams shareParams) {
        Intent starter = new Intent(context, ShareActivity.class);
        starter.putExtra("shareParams", shareParams);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        mDialogLoading = DialogUtil.showLoadingDialog(this);

        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.setAttributes(lp);
        win.setGravity(Gravity.BOTTOM);

        shareParams = (ShareParams) getIntent().getSerializableExtra("shareParams");
        if (shareParams == null) {
            finish();
            return;
        }

        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, getApplicationContext());
        mWxApi = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID, true);
        mWxApi.registerApp(Constant.WE_CHAT_APPID);

        findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RecyclerView content_rv = findViewById(R.id.content_rv);
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        content_rv.setLayoutManager(manager);
        ShareRecyclerAdapter adapter = new ShareRecyclerAdapter(this);
        content_rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new ShareRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                checkImageUrl(new OnCommonListener<String>() {
                    @Override
                    public void execute(String s) {

                        if (TextUtils.isEmpty(s)) {
                            dismissLoadingDialog();
                            ToastUtil.INSTANCE.showToast(R.string.share_picture_error);
                            return;
                        }

                        ShareLayoutBean shareLayoutBean = beans.get(position);

                        switch (shareLayoutBean.resId) {

                            /*
                             * 微信/朋友圈
                             */
                            case R.drawable.share_wechat:
                            case R.drawable.share_wechatfriend: {
                                boolean isFriendCircle = shareLayoutBean.resId == R.drawable.share_wechatfriend;
                                shareToWeChat(isFriendCircle, s);
                                break;
                            }

                            /*
                             * 分享QQ
                             */
                            case R.drawable.share_qq: {
                                shareToQQ(s);
                                break;
                            }

                            /*
                             * 分享QQ空间
                             */
                            case R.drawable.share_qq_zone: {
                                shareToQZone(s);
                                break;
                            }

                            /*
                             * 保存图片
                             */
                            case R.drawable.share_save: {

                                PermissionUtil.requestPermissions(ShareActivity.this, new PermissionUtil.OnPermissionListener() {
                                    @Override
                                    public void onPermissionGranted() {

                                        FileUtil.checkDirection(Constant.GALLERY_PATH);
                                        File file = new File(Constant.GALLERY_PATH + "share_picture.jpg");
                                        if (FileUtil.copyFile(shareParams.imageUrl, file.getAbsolutePath())) {
                                            addPictureToAlbum(file);
                                            if (!TextUtils.isEmpty(shareParams.imageUrl)) {
                                                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.save_success);
                                                finish();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onPermissionDenied() {
                                        ToastUtil.INSTANCE.showToast("没有文件读写权限，无法保存");
                                    }
                                }, PermissionUtil.filePermission);

                                break;
                            }

                            /*
                             * 复制链接
                             */
                            case R.drawable.share_copy: {
                                if (copy(shareParams.contentUrl)) {
                                    ToastUtil.INSTANCE.showToast(R.string.copy_success);
                                    finish();
                                }
                                break;
                            }

                        }
                    }
                });
            }
        });

        if (TextUtils.isEmpty(shareParams.contentUrl)) {
            findViewById(R.id.copy_tv).setVisibility(View.GONE);
        } else {
            findViewById(R.id.copy_tv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (copy(shareParams.contentUrl)) {
                        ToastUtil.INSTANCE.showToast(R.string.copy_success);
                    }
                }
            });
        }
        beans.add(new ShareLayoutBean("微信", R.drawable.share_wechat));
        beans.add(new ShareLayoutBean("朋友圈", R.drawable.share_wechatfriend));
        beans.add(new ShareLayoutBean("QQ", R.drawable.share_qq));
        beans.add(new ShareLayoutBean("QQ空间", R.drawable.share_qq_zone));
        if (shareParams.savePic) {
            beans.add(new ShareLayoutBean("保存图片", R.drawable.share_save));
        }
        beans.add(new ShareLayoutBean("复制链接", R.drawable.share_copy));
        adapter.loadData(beans);
    }

    /**
     * 复制内容到剪切板
     */
    private boolean copy(String copyStr) {
        try {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("Label", copyStr);
            cm.setPrimaryClip(mClipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检测图片地址&下载图片
     */
    private void checkImageUrl(final OnCommonListener<String> onCommonListener) {

        final boolean needScale = ShareParams.TypePictureText == shareParams.type;
        String imageUrl = shareParams.imageUrl;

        //图片地址如果为空，图片设置为logo
        if (TextUtils.isEmpty(imageUrl)) {
            File file = new File(Constant.SHARE_PATH + "local.png");
            if (!file.exists()) {
                file = BitmapUtil.saveBitmapAsPng(
                        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher),
                        Constant.SHARE_PATH + "local.png");
            }
            if (file != null && file.exists())
                imageUrl = file.getPath();
            if (onCommonListener != null)
                onCommonListener.execute(imageUrl);
            return;
        }

        File file = new File(imageUrl);
        if (file.exists()) {
            if (needScale) {
                file = BitmapUtil.saveImageThumbnail(file);
            }
            if (file != null && file.exists()) {
                imageUrl = file.getPath();
            }
            if (onCommonListener != null)
                onCommonListener.execute(imageUrl);
            return;
        }
        showLoadingDialog();
        final String finalImageUrl = imageUrl;
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        String path;
                        if (finalImageUrl.toLowerCase().endsWith("png")) {
                            path = Constant.SHARE_PATH + "share.png";
                        } else {
                            path = Constant.SHARE_PATH + "share.jpg";
                        }
                        File file;
                        if (needScale) {
                            file = BitmapUtil.saveImageThumbnail(resource);
                        } else
                            file = BitmapUtil.saveBitmapAsPng(resource, path);
                        boolean ok = file != null && file.exists();
                        if (ok)
                            path = file.getPath();
                        if (onCommonListener != null)
                            if (ok) {
                                onCommonListener.execute(path);
                            } else {
                                onCommonListener.execute(null);
                            }
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        if (onCommonListener != null)
                            onCommonListener.execute(null);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 把bitmap画到一个白底的newBitmap上,将newBitmap返回
     * 微信黑底问题
     *
     * @param bitmap 要绘制的位图
     * @return Bitmap
     */
    public static Bitmap drawableBitmapOnWhiteBg(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap, 0, 0, paint); //将原图使用给定的画笔画到画布上
        return newBitmap;
    }

    public void showLoadingDialog() {
        try {
            if (!isFinishing() && mDialogLoading != null && !mDialogLoading.isShowing()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mDialogLoading.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void dismissLoadingDialog() {
        try {
            if (!isFinishing() && mDialogLoading != null && mDialogLoading.isShowing()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mDialogLoading.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分享到QQ
     */
    private void shareToQQ(String imageUrl) {

        final Bundle params = new Bundle();

        final int[] types = {
                QQShare.SHARE_TO_QQ_TYPE_IMAGE,
                QQShare.SHARE_TO_QQ_TYPE_DEFAULT
        };
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, types[shareParams.type]);

        if (types[shareParams.type] == QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl);
            mTencent.shareToQQ(ShareActivity.this, params, new MyUIListener());
        } else {
            // 标题
            if (!TextUtils.isEmpty(shareParams.title)) {
                params.putString(QQShare.SHARE_TO_QQ_TITLE, shareParams.title);
            }

            // 摘要
            if (!TextUtils.isEmpty(shareParams.summary)) {
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareParams.summary);
            }

            // 图片地址
            if (!TextUtils.isEmpty(imageUrl)) {
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
            }

            // 内容地址
            if (!TextUtils.isEmpty(shareParams.contentUrl)) {
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareParams.contentUrl);
            }
            mTencent.shareToQQ(this, params, new MyUIListener());
        }
    }


    /**
     * 分享到QZone,必须有图片
     */
    private void shareToQZone(String imageUrl) {

        final Bundle params = new Bundle();

        final int[] types = {
                QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE,
                QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT
        };

        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, types[shareParams.type]);

        if (types[shareParams.type] == QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE) {
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);  //注意，要向qq空间分享纯图片，只能传这三个参数，不能传其他的
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl);  //localImgUrl必须是本地手机图片地址
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
            mTencent.shareToQQ(this, params, new MyUIListener());
        } else {
            // 标题
            if (!TextUtils.isEmpty(shareParams.title)) {
                params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareParams.title);
            }

            // 摘要
            if (!TextUtils.isEmpty(shareParams.summary)) {
                params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareParams.summary);
            }

            // 图片地址
            if (!TextUtils.isEmpty(imageUrl)) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(imageUrl);
                params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, arrayList);
            }

            // 内容地址
            if (!TextUtils.isEmpty(shareParams.contentUrl)) {
                params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareParams.contentUrl);
            }
            mTencent.shareToQzone(this, params, new MyUIListener());
        }
    }

    /**
     * 分享到微信
     */
    private void shareToWeChat(boolean isFriendCricle, String imageUrl) {
        dismissLoadingDialog();
        if (mWxApi == null || !mWxApi.isWXAppInstalled()) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.not_install_we_chat);
            return;
        }

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareParams.contentUrl;

        WXMediaMessage msg = new WXMediaMessage(webpage);

        // 标题
        if (!TextUtils.isEmpty(shareParams.title)) {
            msg.title = shareParams.title;
        }

        // 描述
        if (!TextUtils.isEmpty(shareParams.summary)) {
            msg.description = shareParams.summary;
        }

        if (shareParams.type == ShareParams.TypePicture) {
            WXImageObject wxImageObject = new WXImageObject();
            wxImageObject.setImagePath(imageUrl);
            msg.mediaObject = wxImageObject;
        } else {
            msg.setThumbImage(drawableBitmapOnWhiteBg(BitmapUtil.getBitmapByFile(new File(imageUrl))));
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        //WXSceneTimeline朋友圈    WXSceneSession聊天界面
        req.scene = isFriendCricle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;//聊天界面
        req.message = msg;
        req.transaction = String.valueOf(System.currentTimeMillis());
        mWxApi.sendReq(req);
        boolean res = mWxApi.sendReq(req);
        if (res) {
            AppManager.getInstance().setIsMainPageShareQun(false);
        }
    }

    class MyUIListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
//            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.share_success);
            finish();
        }

        @Override
        public void onError(UiError uiError) {
//            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.share_fail);
            finish();
        }

        @Override
        public void onCancel() {
//            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.share_cancel);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, new MyUIListener());
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_QQ_SHARE
                    || resultCode == Constants.REQUEST_QZONE_SHARE
                    || resultCode == Constants.REQUEST_OLD_SHARE) {
                Tencent.handleResultData(data, new MyUIListener());
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoadingDialog();
    }

    public static class ShareParams extends BaseBean {

        public static final int TypePicture = 0;
        public static final int TypePictureText = 1;

        private int type;
        private String title;
        private String summary;
        private String contentUrl;
        private boolean savePic;

        /**
         * 网络图片路径或者文件文件路径
         */
        private String imageUrl;

        public ShareParams setSavePic(boolean savePic) {
            this.savePic = savePic;
            return this;
        }

        public ShareParams typeImage() {
            this.type = TypePicture;
            return this;
        }

        public ShareParams typeTextImage() {
            this.type = TypePictureText;
            return this;
        }

        public ShareParams setTitle(String title) {
            this.title = title;
            return this;
        }

        public ShareParams setSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public ShareParams setContentUrl(String contentUrl) {
            this.contentUrl = contentUrl;
            return this;
        }

        public ShareParams setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }
    }

    MediaScannerConnection scanner;

    private void addPictureToAlbum(final File paramFile) {
        MediaScannerConnection mMediaScanner = new MediaScannerConnection(this,
                new MediaScannerConnection.MediaScannerConnectionClient() {

                    @Override
                    public void onMediaScannerConnected() {
                        scanner.scanFile(paramFile.getAbsolutePath(), "image/jpeg");
                    }

                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        if (scanner != null) {
                            scanner.disconnect();
                        }
                        scanner = null;
                    }
                });
        scanner = mMediaScanner;
        mMediaScanner.connect();
    }

}