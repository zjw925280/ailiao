package com.lovechatapp.chat.helper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.FileUtil;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 上传语音通话记录
 */
public class RecordUploader {

    private static RecordUploader recordUploader;

    /**
     * 上传状态
     */

    private boolean isUploading;

    /**
     * 录音文件路径
     */
    private String recordPath;

    /**
     * 后台控制开关
     */
    private boolean isSaveRecord;

    private RecordUploader() {
        recordUploader = this;
    }

    public static RecordUploader get() {
        if (recordUploader == null) {
            new RecordUploader();
        }
        return recordUploader;
    }

    private void checkFile() {
        File file = new File(Constant.RECORD_PATH);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File file1 : files) {
                    upload(file1);
                    if (isUploading)
                        break;
                }
            }
        }
    }

    /**
     * 开始上传
     */
    public final void upload() {
        if (isSaveRecord) {
            upload(new File(recordPath));
        }
    }

    /**
     * 上传文件
     *
     * @param file
     */
    private void upload(File file) {
        if (isUploading)
            return;
        if (!file.exists()) {
            return;
        }
        if (!file.getName().startsWith("" + AppManager.getInstance().getUserInfo().t_id)) {
            FileUtil.deleteFiles(file.getAbsolutePath());
            return;
        }
        isUploading = true;
        new FileUploader().setOnUploaderListener(new FileUploader.OnUploaderListener() {
            @Override
            public void onSuccess(File file, String url) {
                commit(url, file);
            }

            @Override
            public void onFailure(File file) {
                LogUtil.d("==-", "记录上传失败");
                isUploading = false;
            }
        }).upload(file, "/record/");
    }

    /**
     * 上传数据
     *
     * @param url
     * @param file
     */
    private void commit(String url, final File file) {
        String cover_userId = null;
        long time = 0;
        try {
            String[] data = file.getName().replace(".aac", "").split("_");
            cover_userId = data[1];
            time = Long.parseLong(data[2]) / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(cover_userId) || time == 0) {
            isUploading = false;
            if (file.delete()) {
                checkFile();
            }
            return;
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("cover_userId", cover_userId);
        paramMap.put("soundUrl", url);
        paramMap.put("video_start_time", time);
        OkHttpUtils.post().url(ChatApi.saveSounRecording())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<String>>() {
            @Override
            public void onResponse(BaseResponse<String> response, int id) {
                isUploading = false;
                if (response.m_istatus == 1) {
                    FileUtil.deleteFiles(file.getAbsolutePath());
                    checkFile();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                isUploading = false;
            }
        });
    }

    /**
     * 创建文件名称
     *
     * @param coverId
     * @return
     */
    public final String createRecordPath(int coverId) {
        FileUtil.checkDirection(Constant.RECORD_PATH);
        return recordPath = Constant.RECORD_PATH
                + AppManager.getInstance().getUserInfo().t_id + "_"
                + coverId + "_"
                + System.currentTimeMillis() + ".aac";
    }

    /**
     * 设置是否保存语音记录
     */
    public final void updateSaveRecord() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AppManager.getInstance());
        isSaveRecord = sharedPreferences.getBoolean("save_record_data", false);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        OkHttpUtils.post().url(ChatApi.getSounRecordingSwitch())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<String>>() {
            @Override
            public void onResponse(BaseResponse<String> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    try {
                        JSONObject jsonObject = JSON.parseObject(response.m_object);
                        isSaveRecord = jsonObject.getIntValue("t_sound_recording_switch") == 1;
                        sharedPreferences.edit().putBoolean("save_record_data", isSaveRecord).apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public final boolean isSaveRecord() {
        return isSaveRecord;
    }
}