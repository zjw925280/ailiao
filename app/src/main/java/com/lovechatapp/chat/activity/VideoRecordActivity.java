package com.lovechatapp.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.cjt2325.cameralibrary.CaptureButton;
import com.cjt2325.cameralibrary.CaptureLayout;
import com.cjt2325.cameralibrary.JCameraView;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;

import java.lang.reflect.Field;

/**
 * 认证主播录制10s自拍视频Activity
 */
public class VideoRecordActivity extends CameraActivity {

    public static final int RequestCode = 1010;

    public static void start(Activity context) {
        Intent starter = new Intent(context, VideoRecordActivity.class);
        context.startActivityForResult(starter, RequestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_RECORDER);
        setMinDuration();

        ((TextView) findViewById(R.id.info_tv)).setText(String.format(
                "请正对摄像头，大声朗读以下内容：\n你好，我在爱聊的ID是%s，快来找我聊天吧！",
                AppManager.getInstance().getUserInfo().getIdCard()));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera_apply_video;
    }

    /**
     * 设置最短录制时间为10s
     */
    private void setMinDuration() {
        try {
            Field field = JCameraView.class.getDeclaredField("mCaptureLayout");
            field.setAccessible(true);
            CaptureLayout captureLayout = (CaptureLayout) field.get(jCameraView);
            Field field2 = CaptureLayout.class.getDeclaredField("btn_capture");
            field2.setAccessible(true);
            CaptureButton captureButton = (CaptureButton) field2.get(captureLayout);
            captureButton.setMinDuration(5000);
            captureButton.setDuration(15000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}