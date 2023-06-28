package com.lovechatapp.chat.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.ErrorListener;
import com.cjt2325.cameralibrary.listener.JCameraListener;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.util.BitmapUtil;
import com.lovechatapp.chat.util.ToastUtil;

import java.io.File;

public class CameraActivity extends AppCompatActivity {

    protected JCameraView jCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(getLayoutId());
        jCameraView = findViewById(R.id.jcameraview);
        //设置视频保存路径
        jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");
        jCameraView.setFeatures(JCameraView.BUTTON_STATE_BOTH);
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);
        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.get_permission_fail);
                Intent intent = new Intent();
                setResult(103, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.get_permission_fail);
            }

        });
        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                File pFile = new File(com.lovechatapp.chat.util.FileUtil.YCHAT_DIR);
                if (!pFile.exists()) {
                    boolean res = pFile.mkdir();
                    if (!res) {
                        return;
                    }
                }
                File file = new File(Constant.ACTIVE_IMAGE_DIR);
                if (!file.exists()) {
                    boolean res = file.mkdir();
                    if (!res) {
                        return;
                    }
                }
                String jpegName = Constant.ACTIVE_IMAGE_DIR + File.separator + System.currentTimeMillis() + ".jpg";
                File localFile = BitmapUtil.saveBitmapAsPng(bitmap, jpegName);
                if (localFile != null) {
                    Intent intent = new Intent();
                    intent.putExtra("imagePath", localFile.getAbsolutePath());
                    setResult(101, intent);
                    finish();
                }
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //获取视频路径
                File pFile = new File(com.lovechatapp.chat.util.FileUtil.YCHAT_DIR);
                if (!pFile.exists()) {
                    boolean res = pFile.mkdir();
                    if (!res) {
                        return;
                    }
                }
                File file = new File(Constant.ACTIVE_IMAGE_DIR);
                if (!file.exists()) {
                    boolean res = file.mkdir();
                    if (!res) {
                        return;
                    }
                }
                String jpegName = Constant.ACTIVE_IMAGE_DIR + File.separator + System.currentTimeMillis() + ".jpg";
                File localFile = BitmapUtil.saveBitmapAsPng(firstFrame, jpegName);
                if (localFile != null) {
                    Intent intent = new Intent();
                    intent.putExtra("imagePath", localFile.getAbsolutePath());
                    intent.putExtra("videoUrl", url);
                    setResult(102, intent);
                    finish();
                }

            }
        });
        jCameraView.setLeftClickListener(CameraActivity.this::finish);
    }

    protected int getLayoutId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }

}
