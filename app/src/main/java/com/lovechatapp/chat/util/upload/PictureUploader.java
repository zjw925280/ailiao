package com.lovechatapp.chat.util.upload;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.ImageHelper;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.listener.OnLuCompressListener;
import com.lovechatapp.chat.oss.QServiceCfg;
import com.lovechatapp.chat.util.LogUtil;

import java.io.File;

public class PictureUploader {

    private UploadTask uploadTask;

    private OnCommonListener<UploadTask> commonListener;

    public final void execute(UploadTask task) {
        execute(task, null);
    }

    public final void execute(UploadTask task, OnCommonListener<UploadTask> commonListener) {
        uploadTask = task;
        this.commonListener = commonListener;
        File file = new File(task.filePath);
        if (!file.exists()) {
            notifyError("文件获取失败，请重新选取");
        } else {
            //文件大小为200Kb以上才压缩图片
            if (file.length() / 1024 > 200) {
                compressImageWithLuBan(task.filePath);
            } else {
                beginUpload(file);
            }
        }
    }

    /**
     * 使用LuBan压缩图片
     */
    private void compressImageWithLuBan(final String filePath) {
        ImageHelper.compressImageWithLuBan(AppManager.getInstance(), filePath, Constant.AFTER_COMPRESS_DIR,
                new OnLuCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {
                        beginUpload(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        notifyError(AppManager.getInstance().getString(R.string.choose_picture_failed));
                    }
                });
    }

    private void notifyError(String message) {
        uploadTask.message = message;
        if (commonListener != null) {
            commonListener.execute(uploadTask);
        }
        if (uploadTask.listener != null) {
            uploadTask.listener.execute(uploadTask);
        }
    }

    /**
     * 开始上传
     */
    private void beginUpload(File file) {
        String cosPath = "/album/" + AppManager.getInstance().getUserInfo().t_id + "_" + file.getName();
        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(Constant.TENCENT_CLOUD_BUCKET, cosPath, file.getPath());
        putObjectRequest.setSign(signDuration, null, null);
        putObjectRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long progress, long max) {
            }
        });

        QServiceCfg mQServiceCfg = QServiceCfg.instance(AppManager.getInstance());
        mQServiceCfg.getCosCxmService().putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                LogUtil.i("腾讯云success =  " + result.accessUrl);
                String resultUrl = result.accessUrl;
                if (!resultUrl.contains("http") || !resultUrl.contains("https")) {
                    resultUrl = "https://" + resultUrl;
                }

                uploadTask.ok = true;
                uploadTask.url = resultUrl;

                if (commonListener != null) {
                    commonListener.execute(uploadTask);
                }

                if (uploadTask.listener != null) {
                    uploadTask.listener.execute(uploadTask);
                }
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                String errorMsg = clientException != null ? clientException.toString() : serviceException.toString();
                LogUtil.i("腾讯云fail: " + errorMsg);
                notifyError(AppManager.getInstance().getString(R.string.upload_fail) + "p-" + errorMsg);
            }
        });
    }
}