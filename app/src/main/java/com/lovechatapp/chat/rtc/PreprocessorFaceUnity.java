package com.lovechatapp.chat.rtc;

import android.content.Context;
import android.opengl.GLES20;

import com.faceunity.FURenderer;
import com.faceunity.RenderConfig;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.ttt.QiNiuChecker;

import io.agora.capture.framework.modules.channels.VideoChannel;
import io.agora.capture.framework.modules.processors.IPreprocessor;
import io.agora.capture.video.camera.VideoCaptureFrame;

public class PreprocessorFaceUnity implements IPreprocessor {

    private final static String TAG = "FaceUnity";

    private FURenderer mFURenderer;
    private Context mContext;
    private boolean mEnabled;

    public PreprocessorFaceUnity(Context context) {
        mContext = context;
        mEnabled = true;
    }

    @Override
    public VideoCaptureFrame onPreProcessFrame(VideoCaptureFrame outFrame, VideoChannel.ChannelContext context) {
        if (mFURenderer == null || !mEnabled) {
            return outFrame;
        }
        QiNiuChecker.get().checkTakeShot(outFrame.image, outFrame.format.getWidth(), outFrame.format.getHeight());
        outFrame.textureId = mFURenderer.onDrawFrame(
                outFrame.image,
                outFrame.textureId,
                outFrame.format.getWidth(),
                outFrame.format.getHeight());
        outFrame.format.setTexFormat(GLES20.GL_TEXTURE_2D);
        return outFrame;
    }

    @Override
    public void initPreprocessor() {
        mFURenderer = new FURenderer.Builder(mContext)
                .inputTextureType(1)
                .build();
        mFURenderer.config(RenderConfig.get(AppManager.getInstance()));
    }

    @Override
    public void enablePreProcess(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    public void releasePreprocessor(VideoChannel.ChannelContext context) {
    }

    public FURenderer getFURenderer() {
        return mFURenderer;
    }
}