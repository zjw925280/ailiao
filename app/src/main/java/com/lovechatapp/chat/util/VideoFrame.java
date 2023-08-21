package com.lovechatapp.chat.util;

import android.graphics.Bitmap;

import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.constant.Constant;

import java.io.File;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class VideoFrame implements OnCompressListener {

    //源视频地址
    private String videoPath;

    protected boolean isOk;

    public VideoFrame(String path) {
        this.videoPath = path;
    }

    public final void execute() {
        getThumb(this);
    }

    /**
     * 获取视频封面图
     *
     * @param listener
     */
    private void getThumb(OnCompressListener listener) {
        String filePath = Constant.AFTER_COMPRESS_DIR + "thumb_" + System.currentTimeMillis() + ".png";
        FileUtil.checkDirection(Constant.AFTER_COMPRESS_DIR);
        Bitmap bitmap = VideoFileUtils.getVideoFirstFrame(videoPath);
        BitmapUtil.saveBitmapAsJpg(bitmap, filePath);
        Luban.with (AppManager.getInstance())
                .load(filePath)
                .setTargetDir(Constant.AFTER_COMPRESS_DIR)
                .setCompressListener(listener)
                .launch();
    }

    public void complete(File firstFile) {
        isOk = firstFile != null && firstFile.exists();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onSuccess(File file) {
        end(file);
    }

    @Override
    public void onError(Throwable e) {
        end(null);
    }

    private void end(File file) {
        complete(file);
    }
}