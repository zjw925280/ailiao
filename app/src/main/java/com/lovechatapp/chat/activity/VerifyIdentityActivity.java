package com.lovechatapp.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.yalantis.ucrop.UCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.ImageHelper;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.FileUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.util.upload.FileUploadManager;
import com.lovechatapp.chat.util.upload.UploadTask;
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnClick;
import okhttp3.Call;

/**
 * 身份认证
 */
public class VerifyIdentityActivity extends BaseActivity {

    private String filePath1, filePath2;
    private int chooserId;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_apply_upload_idcard);
    }

    @Override
    protected void onContentAdded() {
        setTitle("申请认证");

        //删除REPORT目录中的图片
        FileUtil.deleteFiles(Constant.VERIFY_AFTER_RESIZE_DIR);
    }

    @OnClick({R.id.inside_iv, R.id.outside_iv, R.id.next_tv})
    public void onClick(View view) {
        if (view.getId() == R.id.next_tv) {
            if (TextUtils.isEmpty(filePath1)) {
                ToastUtil.INSTANCE.showToast("请上传身份证人像面");
                return;
            }
            if (TextUtils.isEmpty(filePath2)) {
                ToastUtil.INSTANCE.showToast("请上传身份证国徽面");
                return;
            }
            commit();
        } else {
            chooserId = view.getId();
            ImageHelper.openPictureChoosePage(VerifyIdentityActivity.this, 1001);
        }
    }

    /**
     * 提交
     */
    private void commit() {
        final UploadTask task1 = new UploadTask();
        task1.filePath = filePath1;

        final UploadTask task2 = new UploadTask();
        task2.filePath = filePath2;

        showLoadingDialog();
        FileUploadManager.permissionUpload(Arrays.asList(task1, task2), new OnCommonListener<Boolean>() {
            @Override
            public void execute(Boolean aBoolean) {
                if (isFinishing())
                    return;
                if (aBoolean) {
                    uploadVerifyInfo(task1.url, task2.url);
                } else {
                    dismissLoadingDialog();
                }
            }
        });
    }

    /**
     * 上传身份图片地址
     * t_type 0：主播认证  1：视频认证  3：身份证认证
     */
    private void uploadVerifyInfo(String t_card_face, String t_card_back) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_card_face", t_card_face);
        paramMap.put("t_card_back", t_card_back);
        paramMap.put("t_type", 3);
        OkHttpUtils.post().url(ChatApi.SUBMIT_VERIFY_DATA())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (isFinishing())
                    return;
                dismissLoadingDialog();
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ToastUtil.INSTANCE.showToast("提交成功");
                        finish();
                    } else {
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.INSTANCE.showToast(mContext, message);
                        } else {
                            ToastUtil.INSTANCE.showToast(mContext, "提交失败");
                        }
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(mContext, "提交失败");
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                if (isFinishing())
                    return;
                dismissLoadingDialog();
                ToastUtil.INSTANCE.showToast(mContext, "提交失败");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            uCrop(Matisse.obtainPathResult(data));
        } else if (requestCode == 1002 && resultCode == Activity.RESULT_OK) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                int resizeWidth = DevicesUtil.dp2px(getApplicationContext(), 300);
                int resizeHeight = DevicesUtil.dp2px(getApplicationContext(), 200);
                ImageLoadHelper.glideShowImageWithUri(
                        this,
                        resultUri,
                        (ImageView) findViewById(chooserId),
                        resizeWidth,
                        resizeHeight);
                if (chooserId == R.id.inside_iv) {
                    filePath1 = resultUri.getPath();
                } else {
                    filePath2 = resultUri.getPath();
                }
            }
        }
    }

    /**
     * 使用ucrop裁剪图片
     */
    private void uCrop(List<String> mSelectedUris) {
        String sourceUri = null;
        if (mSelectedUris != null && mSelectedUris.size() > 0) {
            try {
                String filePath = mSelectedUris.get(0);
                if (!TextUtils.isEmpty(filePath)) {
                    sourceUri = filePath;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (sourceUri == null)
            return;

        //计算 图片裁剪的大小
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            boolean res = pFile.mkdir();
            if (!res) {
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.verify_fail);
                return;
            }
        }
        File file = new File(Constant.VERIFY_AFTER_RESIZE_DIR);
        if (!file.exists()) {
            boolean res = file.mkdir();
            if (!res) {
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.verify_fail);
                return;
            }
        }
        String filePath = file.getPath() + File.separator + System.currentTimeMillis() + ".jpg";
        UCrop.of(Uri.fromFile(new File(sourceUri)), Uri.fromFile(new File(filePath)))
                .withAspectRatio(600, 400)
                .withMaxResultSize(600, 400)
                .start(this, 1002);
    }
}