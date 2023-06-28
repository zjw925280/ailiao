package com.lovechatapp.chat.util.upload;

import android.text.TextUtils;
import com.lovechatapp.chat.listener.OnCommonListener;

public class UploadTask {

    //视频文件Id
    public String videoId;

    //视频/图片地址
    public String url;

    //视频封面
    public String coverURL;

    //是否视频
    public boolean isVideo;

    //判断是否上传成功
    protected boolean ok = false;

    //失败信息
    public String message;

    //文件路径
    public String filePath;

    public Object tag;

    OnCommonListener<UploadTask> listener;

    //上传进度
    OnCommonListener<Integer> progressListener;
    private int progress;

    public UploadTask() {
    }

    public UploadTask(boolean isVideo) {
        this.isVideo = isVideo;
    }

    public UploadTask(boolean isVideo, String filePath) {
        this.isVideo = isVideo;
        this.filePath = filePath;
    }

    /**
     * 是否可上传
     *
     */
    public final boolean isAvailable() {
        return !TextUtils.isEmpty(filePath);
    }

    public UploadTask setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    public void setListener(OnCommonListener<UploadTask> listener) {
        this.listener = listener;
    }

    public final void setProgressListener(OnCommonListener<Integer> progress) {
        this.progressListener = progress;
    }

    public final void setProgress(int progress) {
        if (this.progress != progress) {
            this.progress = progress;
            if (this.progressListener != null) {
                progressListener.execute(progress);
            }
        }
    }

    public final boolean isOk() {
        return ok;
    }
}