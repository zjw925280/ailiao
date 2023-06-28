package com.lovechatapp.chat.util.upload;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.videoupload.TXUGCPublish;
import com.lovechatapp.chat.videoupload.TXUGCPublishTypeDef;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class VideoUploader {

    private UploadTask uploadTask;

    private OnCommonListener<UploadTask> commonListener;

    public final void execute(UploadTask task) {
        execute(task, null);
    }

    public final void execute(UploadTask task, OnCommonListener<UploadTask> commonListener) {
        uploadTask = task;
        this.commonListener = commonListener;
        try {
            String filePath = task.filePath;
            if (!TextUtils.isEmpty(filePath)) {
                File file = new File(filePath);
                if (!file.exists()) {
                    notifyError(AppManager.getInstance().getString(R.string.file_not_exist));
                    return;
                } else {
                    double fileSize = (double) file.length() / 1024 / 1024;
                    if (fileSize > 50) {
                        notifyError(AppManager.getInstance().getString(R.string.file_too_big));
                        return;
                    }
                }
                if (file.getName().contains(" ")) {
                    File f = new File(file.getParentFile().getPath(), file.getName().replace(" ", ""));
                    if (file.renameTo(f)) {
                        filePath = f.getPath();
                    }
                }
                getSign(filePath);
            } else {
                notifyError(AppManager.getInstance().getString(R.string.upload_fail) + 1301);
            }
        } catch (Exception e) {
            e.printStackTrace();
            notifyError(AppManager.getInstance().getString(R.string.upload_fail) + 1302);
        }
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
     * 获取视频上传签名
     * fromCheck  是鉴黄用的签名 还是上传用的
     */
    private void getSign(final String filePath) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        OkHttpUtils.post().url(ChatApi.GET_VIDEO_SIGN())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        notifyError(AppManager.getInstance().getString(R.string.system_error));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (!TextUtils.isEmpty(response)) {
                            JSONObject jsonObject = JSON.parseObject(response);
                            int m_istatus = jsonObject.getInteger("m_istatus");
                            if (m_istatus == NetCode.SUCCESS) {
                                String m_object = jsonObject.getString("m_object");
                                if (!TextUtils.isEmpty(m_object)) {
                                    //上传文件
                                    beginUpload(m_object, filePath);
                                } else {
                                    notifyError(AppManager.getInstance().getString(R.string.upload_fail) + 1303);
                                }
                            } else {
                                notifyError(AppManager.getInstance().getString(R.string.upload_fail) + 1304);
                            }
                        } else {
                            notifyError(AppManager.getInstance().getString(R.string.upload_fail) + 1305);
                        }
                    }
                });
    }

    /**
     * 开始上传
     */
    private void beginUpload(final String sign, String filePath) {
        TXUGCPublish mVideoPublish = new TXUGCPublish(AppManager.getInstance(), "carol_android");
        mVideoPublish.setListener(new TXUGCPublishTypeDef.ITXVideoPublishListener() {
            @Override
            public void onPublishProgress(long uploadBytes, long totalBytes) {
                uploadTask.setProgress((int) (100 * uploadBytes / totalBytes));
            }

            @Override
            public void onPublishComplete(TXUGCPublishTypeDef.TXPublishResult result) {
                //上传成功
                if (result.retCode == 0) {
                    uploadTask.ok = true;
                    uploadTask.videoId = result.videoId;
                    uploadTask.coverURL = result.coverURL;
                    uploadTask.url = result.videoURL;

                    if (commonListener != null) {
                        commonListener.execute(uploadTask);
                    }

                    if (uploadTask.listener != null) {
                        uploadTask.listener.execute(uploadTask);
                    }
                } else {
                    if (result.retCode == 1015) {
                        notifyError(AppManager.getInstance().getString(R.string.upload_fail) + "-" + "文件名称不合法，请修改文件名称后重试");
                    } else {
                        notifyError(AppManager.getInstance().getString(R.string.upload_fail) + "-" + result.retCode);
                    }
                }
            }
        });
        TXUGCPublishTypeDef.TXPublishParam param = new TXUGCPublishTypeDef.TXPublishParam();
        param.signature = sign;
        param.videoPath = filePath;
        mVideoPublish.publishVideo(param);
    }
}