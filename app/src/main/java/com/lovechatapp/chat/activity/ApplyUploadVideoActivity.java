package com.lovechatapp.chat.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjt2325.cameralibrary.util.ScreenUtils;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.DensityUtil;
import com.lovechatapp.chat.util.FileUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.util.upload.FileUploadManager;
import com.lovechatapp.chat.util.upload.UploadTask;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class ApplyUploadVideoActivity extends BaseActivity {

    @BindView(R.id.video_iv)
    ImageView videoIv;

    @BindView(R.id.video_info_tv)
    TextView videoInfoTv;

    String videoUrl;

    UploadTask uploadTask;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_apply_upload_video);
    }

    @Override
    protected void onContentAdded() {
        setTitle("视频认证");
        videoInfoTv.setText(String.format(
                "请正对摄像头，大声朗读以下内容：\n你好，我在爱聊的ID是%s，快来找我聊天吧！",
                AppManager.getInstance().getUserInfo().getIdCard()));
    }

    @OnClick({R.id.select_iv, R.id.done_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_iv:
                VideoRecordActivity.start(ApplyUploadVideoActivity.this);
                break;
            case R.id.done_tv:
                if (uploadTask == null) {
                    ToastUtil.INSTANCE.showToast("请上传自拍认证视频");
                    return;
                }
                commit();
                break;
        }
    }

    private void commit() {
        showLoadingDialog();
        FileUploadManager.permissionUpload(Arrays.asList(uploadTask), new OnCommonListener<Boolean>() {
            @Override
            public void execute(Boolean aBoolean) {
                if (isFinishing())
                    return;
                if (aBoolean) {
                    uploadVerifyInfo(uploadTask.url);
                }
                dismissLoadingDialog();
            }
        });
    }

    /**
     * t_user_video 自拍认证视频
     * t_type 0：主播认证  1：视频认证  3：身份证认证
     */
    private void uploadVerifyInfo(String t_user_video) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_user_video", t_user_video);
        paramMap.put("t_type", 1);
        OkHttpUtils.post().url(ChatApi.SUBMIT_VERIFY_DATA())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (isFinishing())
                    return;
                dismissLoadingDialog();
                boolean ok = response != null && response.m_istatus == NetCode.SUCCESS;
                ToastUtil.INSTANCE.showToast(getApplicationContext(), ok ? "提交成功" : "提交失败");
                if (ok) {
                    finish();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (isFinishing())
                    return;
                super.onError(call, e, id);
                dismissLoadingDialog();
                ToastUtil.INSTANCE.showToast(getApplicationContext(), "提交失败");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VideoRecordActivity.RequestCode && resultCode == 102) {
            String imagePath = data.getStringExtra("imagePath");
            videoUrl = data.getStringExtra("videoUrl");
            if (!TextUtils.isEmpty(imagePath) && !TextUtils.isEmpty(videoUrl)) {
                uploadTask = new UploadTask(true);
                uploadTask.filePath = videoUrl;
                uploadTask.coverURL = imagePath;
                ImageLoadHelper.glideShowImageWithUrl(
                        this,
                        videoUrl,
                        videoIv,
                        ScreenUtils.getScreenWidth(this),
                        DensityUtil.dip2px(this, 200));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //删除REPORT目录中的图片
        FileUtil.deleteFiles(Constant.VERIFY_AFTER_RESIZE_DIR);
    }
}