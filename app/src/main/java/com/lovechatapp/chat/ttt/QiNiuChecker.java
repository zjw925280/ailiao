package com.lovechatapp.chat.ttt;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseBean;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.FileUploader;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.BitmapUtil;
import com.lovechatapp.chat.util.FileUtil;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.qiniu.util.Auth;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//import java.io.IOException;

//import okhttp3.MediaType;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;

/**
 * 七牛云鉴黄
 */
public class QiNiuChecker {

    private Auth auth;

    private String TAG = "QiNiuChecker";

    private static QiNiuChecker qiniuChecker;

    private boolean enable;

    //是否保存截图，调试使用
    private boolean saveShotPicture = false;

    //客户端上传需要
    private Handler handler;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final SparseArray<Object> sparseArray = new SparseArray<>();

    private OnCommonListener<Boolean> alertListener;

    private String video_alert;

    private NV21ToBitmap nv21ToBitmap;

    /**
     * 缓存参数
     * videoUserId: 用户Id;
     * videoAnchorUserId: 主播Id;
     * roomId: 房间号;
     * userId: 用户封号后AppManager获取的t_id为0，这里缓存下userId，以免封号后上传失败;
     */
    private int videoUserId;
    private int videoAnchorUserId;
    private int roomId;
    private int userId;
    private int mansionRoomId;

    private boolean needTakeShot;

    private QiNiuChecker() {

        auth = Auth.create("HzTWfgGUs8Sq7pqgLsKMi1JKISAhI1vN8smLJhuw", "XP3CIDuOvUmSdFgIE3OaMGvhmge-YQdw-SAOQZEy");
        userId = AppManager.getInstance().getUserInfo().t_id;

        //客户端上传需要
        HandlerThread handlerThread = new HandlerThread("http");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        nv21ToBitmap = new NV21ToBitmap(AppManager.getInstance());
    }

    public static QiNiuChecker get() {
        if (qiniuChecker == null) {
            synchronized (QiNiuChecker.class) {
                if (qiniuChecker == null) {
                    qiniuChecker = new QiNiuChecker();
                }
            }
        }
        return qiniuChecker;
    }

    public final void setAlertListener(OnCommonListener<Boolean> alertListener) {
        this.alertListener = alertListener;
    }

    public final String getVideoAlert() {
        return video_alert;
    }

    public final void alertCancel() {
        handler.removeCallbacks(uploadRun);
    }

    private void asyncRun(Runnable runnable) {
        handler.post(runnable);
    }

    public final boolean checkAnim(int chatSecond) {
        if (enable && sparseArray.size() > 0) {
            chatSecond += 10;
            if (sparseArray.size() == 1) {
                int spaceTime = sparseArray.keyAt(0);
                if (spaceTime > 0 && chatSecond % spaceTime == 0) {
                    return true;
                }
            } else if (sparseArray.indexOfKey(chatSecond) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 截图一次
     */
    public final void checkTakeShot(byte[] bytes, int width, int height) {
        if (needTakeShot && bytes != null) {
            execute(bytes, width, height);
        }
        needTakeShot = false;
    }

    /**
     * 1v1
     */
    public final void checkTime(int chatSecond, int videoUserId, int videoAnchorUserId, int roomId) {
        checkTime(chatSecond, videoUserId, videoAnchorUserId, roomId, 0);
    }

    /**
     * 1v2
     * 根据通话时间判断是否截图进行鉴黄
     *
     * @param chatSecond        通话时长（秒）
     * @param videoUserId       用户Id
     * @param videoAnchorUserId 主播Id
     * @param roomId            房间号
     * @param mansionRoomId     多人房间号
     */
    public final void checkTime(int chatSecond, int videoUserId, int videoAnchorUserId, int roomId, int mansionRoomId) {
        if (enable && sparseArray.size() > 0) {
            this.roomId = roomId;
            this.videoUserId = videoUserId;
            this.videoAnchorUserId = videoAnchorUserId;
            this.mansionRoomId = mansionRoomId;
            if (sparseArray.size() == 1) {
                int spaceTime = sparseArray.keyAt(0);
                if (spaceTime > 0 && chatSecond % spaceTime == 0) {
                    needTakeShot = true;
                }
            } else if (sparseArray.indexOfKey(chatSecond) >= 0) {
                needTakeShot = true;
            }
        }
    }

    /**
     * 字节码转bitmap
     */
    private void execute(final byte[] bytes, final int width, final int height) {
        asyncRun(new Runnable() {

            @Override
            public void run() {
                if (userId == 0 || videoAnchorUserId == 0 || videoUserId == 0)
                    return;
                try {
                    Bitmap bitmap = nv21ToBitmap.nv21ToBitmap(bytes, width, height);
                    clientUpload(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void bytesToBitmap(byte[] bytes, int width, int height) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
            byteBuffer.put(bytes);
            byteBuffer.rewind();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(byteBuffer);
            uploadData(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * bitmap转base64
     */
    private String base64Bitmap(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
        }
        return null;
    }

    /**
     * 上传base64至服务器，服务器端七牛云鉴黄
     *
     * @param bitmap 截图bitmap
     */
    private void uploadData(final Bitmap bitmap) {

        String data = base64Bitmap(bitmap);

        if (TextUtils.isEmpty(data))
            return;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("imgData", data);

        OkHttpUtils
                .post()
                .url(ChatApi.getQiNiuKey())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build()
                .execute(new AjaxCallback<BaseResponse<String>>() {
                    @Override
                    public void onResponse(BaseResponse<String> response, int id) {

                        if (response != null && response.m_istatus == NetCode.SUCCESS && "1".equals(response.m_object)) {
                            success(bitmap);
                        }
                    }
                });
    }

    private void success(Bitmap bitmap) {

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (alertListener != null) {
                    alertListener.execute(true);
                }
            }
        });

        if (saveShotPicture) {
            FileUtil.checkDirection(Constant.ACTIVE_IMAGE_DIR);
            File file = BitmapUtil.saveBitmapAsJpeg(bitmap, Constant.ACTIVE_IMAGE_DIR + System.currentTimeMillis() + ".jpg");
            if (file != null) {
                filePath = file.getPath();
            }
        }
        //上传图片
        //handler.postDelayed(uploadRun, 10 * 1000);
        //直接上传服务器
        uploadPicture("");
    }

    private String filePath;
    private Runnable uploadRun = new Runnable() {
        @Override
        public void run() {
            if (filePath == null) {
                return;
            }
            //上传截图至服务器
            FileUploader.postImg(filePath, new OnCommonListener<String>() {
                @Override
                public void execute(String s) {
                    if (!TextUtils.isEmpty(s)) {
                        uploadPicture(s);
                    }
                    if (!saveShotPicture) {
                        FileUtil.deleteFiles(Constant.ACTIVE_IMAGE_DIR);
                    }
                }
            });
            filePath = null;
        }
    };

    /**
     * 上传涉黄图片链接至服务器
     *
     * @param url 图片链接
     */
    private void uploadPicture(String url) {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("videoUserId", videoUserId);
        paramMap.put("videoAnchorUserId", videoAnchorUserId);
        paramMap.put("roomId", roomId);
        paramMap.put("videoImgUrl", url);
        paramMap.put("mansionRoomId", mansionRoomId);

        OkHttpUtils
                .post()
                .url(ChatApi.addVideoScreenshotInfo())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build()
                .execute(new AjaxCallback<BaseResponse<String>>() {
                    @Override
                    public void onResponse(BaseResponse<String> response, int id) {

                    }
                });
    }

    /**
     * 客户端上传base64至服务器获取authorization
     */
    private void clientUpload(Bitmap bitmap) {
        handler.post(new Runnable() {

            @Override
            public void run() {
                try {

                    String data = base64Bitmap(bitmap);

                    if (TextUtils.isEmpty(data))
                        return;

                    Map<String, Object> uri = new HashMap<>();
                    uri.put("uri", "data:application/octet-stream;base64," + data);

                    //pulp 黄  terror 恐  politician 敏感人物
                    String[] types = {"pulp"};
                    Map<String, Object> scenes = new HashMap<>();
                    scenes.put("scenes", types);

                    Map<String, Object> params = new HashMap<>();
                    params.put("data", uri);
                    params.put("params", scenes);

                    String paraR = JSON.toJSONString(params);

                    byte[] bodyByte = paraR.getBytes();

                    String url = "http://ai.qiniuapi.com/v3/image/censor";

                    String accessToken = (String) auth.authorizationV2(
                            url,
                            "POST",
                            bodyByte,
                            "application/json").get("Authorization");

                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), bodyByte);
                    final Request.Builder requestBuilder = new Request.Builder()
                            .url(url)
                            .post(requestBody);

                    requestBuilder.header("Authorization", accessToken);

                    Response response = OkHttpUtils
                            .getInstance()
                            .getOkHttpClient()
                            .newCall(requestBuilder.build())
                            .execute();

                    String responseStr = response.body().string();

                    LogUtil.d(TAG, responseStr);

                    JSONObject fromObject = JSONObject.parseObject(responseStr);
                    if (fromObject != null) {
                        if ("200".equals(String.valueOf(fromObject.get("code")))) {
                            JSONObject pulp = fromObject.getJSONObject("result").getJSONObject("scenes").getJSONObject("pulp");
                            List details = (List) pulp.get("details");
                            if (!details.isEmpty()) {
                                Map labelMap = (Map) details.get(0);
                                String label = labelMap.get("label").toString();
                                String suggestion = labelMap.get("suggestion").toString();
                                if ("pulp".equals(label) || "block".equals(suggestion)) {
                                    success(bitmap);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 客服端七牛云鉴黄
     */
    public final void upload(Bitmap bitmap) {

    }

    /**
     * 客户端更新鉴黄设置
     */
    public final void checkEnable() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        OkHttpUtils
                .post()
                .url(ChatApi.getVideoScreenshotStatus())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build()
                .execute(new AjaxCallback<BaseResponse<Bean>>() {
                    @Override
                    public void onResponse(BaseResponse<Bean> response, int id) {
                        if (response != null && response.m_istatus == NetCode.SUCCESS && response.m_object != null) {

                            video_alert = response.m_object.t_screenshot_video_content;

                            enable = AppManager.getInstance().getUserInfo().t_role == 1 ?
                                    response.m_object.t_screenshot_anchor_switch == 1
                                    : response.m_object.t_screenshot_user_switch == 1;

                            sparseArray.clear();

                            if (response.m_object.t_screenshot_time_list != null
                                    && response.m_object.t_screenshot_time_list.size() > 0) {
                                for (String s : response.m_object.t_screenshot_time_list) {
                                    try {
                                        int time = Integer.parseInt(s);
                                        sparseArray.put(time, null);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private static class Bean extends BaseBean {

        //用户端鉴黄开关
        public int t_screenshot_user_switch;

        //主播端鉴黄开关
        public int t_screenshot_anchor_switch;

        //鉴黄间隔时长
        public List<String> t_screenshot_time_list;

        public String t_screenshot_video_content;
    }
}