package com.lovechatapp.chat.rtc;

import com.faceunity.FURenderer;
import com.faceunity.RenderConfig;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.constant.Constant;

import io.agora.capture.video.camera.CameraVideoManager;
import io.agora.capture.video.camera.VideoCapture;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;

public class RtcManager {

    private static final int CAPTURE_WIDTH = 1280;

    private static final int CAPTURE_HEIGHT = 720;

    private static final int CAPTURE_FRAME_RATE = 24;

    private static RtcManager rtcManager;

    private CameraVideoManager cameraVideoManager;

    private PreprocessorFaceUnity preprocessorFaceUnity;

    private RtcEngine mRtcEngine;

    private RtcEngineEventHandlerProxy mRtcEventHandler;

    public static RtcManager get() {
        if (rtcManager == null) {
            synchronized (RtcManager.class) {
                if (rtcManager == null) {
                    rtcManager = new RtcManager();
                }
            }
        }
        return rtcManager;
    }

    private RtcManager() {

        try {

            mRtcEventHandler = new RtcEngineEventHandlerProxy();

            mRtcEngine = RtcEngine.create(AppManager.getInstance(), Constant.AGORA_APP_ID, mRtcEventHandler);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);

            preprocessorFaceUnity = new PreprocessorFaceUnity(AppManager.getInstance());
            cameraVideoManager = new CameraVideoManager(AppManager.getInstance(), preprocessorFaceUnity);
            cameraVideoManager.setPictureSize(CAPTURE_WIDTH, CAPTURE_HEIGHT);
            cameraVideoManager.setFrameRate(CAPTURE_FRAME_RATE);
            cameraVideoManager.setFacing(io.agora.capture.video.camera.Constant.CAMERA_FACING_FRONT);
            cameraVideoManager.setLocalPreviewMirror(io.agora.capture.video.camera.Constant.MIRROR_MODE_AUTO);
            cameraVideoManager.setCameraStateListener(new VideoCapture.VideoCaptureStateListener() {

                @Override
                public void onFirstCapturedFrame(int width, int height) {

                }

                @Override
                public void onCameraCaptureError(int error, String msg) {
                    if (cameraVideoManager != null) {
                        cameraVideoManager.stopCapture();
                    }
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }

    public CameraVideoManager getCameraManager() {
        return cameraVideoManager;
    }

    public void addRtcHandler(RtcEngineEventHandler handler) {
        mRtcEventHandler.addEventHandler(handler);
    }

    public void removeRtcHandler(RtcEngineEventHandler handler) {
        mRtcEventHandler.removeEventHandler(handler);
    }

    public FURenderer getRender() {
        return preprocessorFaceUnity.getFURenderer();
    }

    public void startCamera() {
        getRender().config(RenderConfig.get(AppManager.getInstance()));
        getRender().onSurfaceCreated();
        getCameraManager().startCapture();
    }

    public void stopCamera() {
        getRender().onSurfaceDestroyed();
        getCameraManager().stopCapture();
    }
}