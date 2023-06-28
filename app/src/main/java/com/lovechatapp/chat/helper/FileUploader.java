package com.lovechatapp.chat.helper;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.oss.QServiceCfg;
import com.lovechatapp.chat.util.BeanParamUtil;
import com.lovechatapp.chat.util.LogUtil;

import java.io.File;

public class FileUploader {

    private OnUploaderListener onUploaderListener;

    public FileUploader uploadTencent(final File file, String cosPath) {

        QServiceCfg mQServiceCfg = QServiceCfg.instance(AppManager.getInstance());

        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(Constant.TENCENT_CLOUD_BUCKET, cosPath, file.getPath());
        putObjectRequest.setSign(signDuration, null, null);

        mQServiceCfg.getCosCxmService().putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                String resultUrl = result.accessUrl;
                if (!resultUrl.contains("http") || !resultUrl.contains("https")) {
                    resultUrl = "https://" + resultUrl;
                }

                if (onUploaderListener != null) {
                    onUploaderListener.onSuccess(file, resultUrl);
                }
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                String errorMsg = clientException != null ? clientException.toString() : serviceException.toString();
                LogUtil.i("腾讯云fail: " + errorMsg);
                if (onUploaderListener != null) {
                    onUploaderListener.onFailure(file);
                }
            }
        });
        return this;
    }

    public FileUploader upload(File file, String type) {
        uploadTencent(file, type + BeanParamUtil.generateFileName(file.getName()));
        return this;
    }

    public FileUploader setOnUploaderListener(OnUploaderListener onUploaderListener) {
        this.onUploaderListener = onUploaderListener;
        return this;
    }

    public interface OnUploaderListener {

        void onSuccess(File file, String url);

        void onFailure(File file);

    }
}