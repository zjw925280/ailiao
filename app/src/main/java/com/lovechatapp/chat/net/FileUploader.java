package com.lovechatapp.chat.net;

import android.util.Log;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.zhy.http.okhttp.OkHttpUtils;
import java.io.File;
import okhttp3.Call;

/**
 * 上传图片至服务器
 */
public class FileUploader {

    public static Task postImg(String filePath, OnCommonListener<String> listener) {

        String lastName = ".jpg";
        try {
            lastName = filePath.substring(filePath.lastIndexOf("."));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        String name = "android" + AppManager.getInstance().getUserInfo().t_id + "_" + System.currentTimeMillis() + lastName;

        Task task = new Task(filePath, name, listener);
        task.upload();
        return task;
    }

    public static class Task {

        private String filePath;
        private String url;
        private String fileName;
        private OnCommonListener<String> callback;

        Task(String filePath, String fileName, OnCommonListener<String> listener) {
            this.filePath = filePath;
            this.url = ChatApi.uploadAPPFile();
            this.callback = listener;
            this.fileName = fileName;
        }

        private void upload() {
            File file = new File(filePath);
            if (!file.exists()) {
                callback.execute(null);
                return;
            }
            //type :1 图片 2 视频
            OkHttpUtils.post().url(url)
                    .tag(Task.this)
                    .addFile("file", fileName, file)
                    .addHeader("Content-Type", "multipart/form-data;")
                    .build()
                    .execute(new AjaxCallback<BaseResponse<String>>() {

                        @Override
                        public void onResponse(BaseResponse<String> response, int id) {
                            if (response != null && response.m_istatus == NetCode.SUCCESS && response.m_object != null) {
                                if (callback != null) {
                                    callback.execute(response.m_object);
                                }
                            }
                        }

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (callback != null) {
                                callback.execute(null);
                            }
                        }

                        @Override
                        public void inProgress(float progress, long total, int id) {
                            Log.d("uploader", "inProgress: " + progress);
                        }
                    });
        }

        public void cancel() {
            this.callback = null;
            OkHttpUtils.getInstance().cancelTag(this);
        }
    }

}