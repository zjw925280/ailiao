package com.lovechatapp.chat.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.oss.QServiceCfg;
import com.lovechatapp.chat.util.ClickControlUtil;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.FileUtil;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.yalantis.ucrop.UCrop;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   申请认证手势页面
 * 作者：
 * 创建时间：2018/12/10
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ApplyVerifyHandActivity extends BaseActivity {

    @BindView(R.id.click_shoot_iv)
    ImageView mClickShootIv;

    @BindView(R.id.wx_et)
    EditText wxEt;

    private final int CAMERA_REQUEST_CODE = 0x16;
    private final int UCROP_SELF = 0x400;
    private String mSelectZiPaiLocalPath = "";
    private Uri mZiPaiuri;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_apply_verify_hand_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.verify_apply);
        requestPermission();
    }

    @OnClick({R.id.click_shoot_iv, R.id.submit_now_tv, R.id.agree_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.click_shoot_iv: {//点击拍照\
                if (ClickControlUtil.getInstance().isClickable()) {
                    jumpToCamera();
                }
                break;
            }
            case R.id.submit_now_tv: {//提交认证
                if (ClickControlUtil.getInstance().isClickable()) {
                    showLoadingDialog();
                    submitVerify();
                }
                break;
            }
            case R.id.agree_tv: {//绿色协议
                Intent intent = new Intent(getApplicationContext(), CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, getResources().getString(R.string.green_agree));
                intent.putExtra(Constant.URL, "file:///android_asset/green.html");
                startActivity(intent);
                break;
            }
        }
    }

    /**
     * 提交认证
     */
    private void submitVerify() {
        //自拍照片
        if (TextUtils.isEmpty(mSelectZiPaiLocalPath)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.id_card_picture_not_choose);
            return;
        }
        File file = new File(mSelectZiPaiLocalPath);
        if (!file.exists()) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.file_not_exist);
            return;
        }

        String wx = wxEt.getText().toString().trim();
        if (TextUtils.isEmpty(wx)) {
            ToastUtil.INSTANCE.showToast(this, "请填写微信号");
            return;
        }

        uploadZipaiFileWithQQ();
    }

    /**
     * 上传认证图到腾讯云
     */
    private void uploadZipaiFileWithQQ() {
        File file = new File(mSelectZiPaiLocalPath);
        if (!file.exists()) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.verify_fail);
            return;
        }
        String fileName = mSelectZiPaiLocalPath.substring(mSelectZiPaiLocalPath.length() - 17);
        String cosPath = "/verify/" + fileName;
        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(Constant.TENCENT_CLOUD_BUCKET, cosPath, mSelectZiPaiLocalPath);
        putObjectRequest.setSign(signDuration, null, null);
        putObjectRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long progress, long max) {
            }
        });
        QServiceCfg.instance(getApplicationContext()).getCosCxmService().putObjectAsync(putObjectRequest,
                new CosXmlResultListener() {
                    @Override
                    public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                        LogUtil.i("自拍 认证=  " + result.accessUrl);
                        String mVerifyImageHttpUrl = result.accessUrl;
                        if (!mVerifyImageHttpUrl.contains("http") || !mVerifyImageHttpUrl.contains("https")) {
                            mVerifyImageHttpUrl = "https://" + mVerifyImageHttpUrl;
                        }
                        uploadVerifyInfo(mVerifyImageHttpUrl);
                    }

                    @Override
                    public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException,
                                       CosXmlServiceException serviceException) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.verify_fail);
                            }
                        });
                    }
                });
    }

    /**
     * 上传认证信息到公司服务器
     */
    private void uploadVerifyInfo(String selfUrl) {
        if (TextUtils.isEmpty(selfUrl)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.choose_again);
            return;
        }
        String wx = wxEt.getText().toString().trim();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_user_photo", selfUrl);
        paramMap.put("t_weixin", wx);
        paramMap.put("t_type", 0);
        OkHttpUtils.post().url(ChatApi.SUBMIT_VERIFY_DATA())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response, int id) {
                        dismissLoadingDialog();
                        if (response != null) {
                            if (response.m_istatus == NetCode.SUCCESS) {
                                ToastUtil.INSTANCE.showToast(R.string.verify_success);
                                Intent intent = new Intent(getApplicationContext(), ActorVerifyingActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            ToastUtil.INSTANCE.showToast(response.m_strMessage);
                        } else {
                            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.verify_fail);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        dismissLoadingDialog();
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.verify_fail);
                    }
                });
    }

    /**
     * 跳转到拍照
     */
    private void jumpToCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    PackageManager.PERMISSION_GRANTED !=
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                requestPermission();
            } else {
                jumpToCameraActivity();
            }
        } else {
            jumpToCameraActivity();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 100);
            }
        }
    }

    /**
     * 调用系统拍照
     */
    public void jumpToCameraActivity() {
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            boolean res = pFile.mkdir();
            if (!res) {
                return;
            }
        }
        File imageFile = new File(Constant.VERIFY_AFTER_RESIZE_DIR, "zipai.jpg");
        if (!imageFile.getParentFile().exists()) {
            boolean res = imageFile.getParentFile().mkdirs();
            if (!res) {
                return;
            }
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mZiPaiuri = null;
        try {
            //API>=24 android 7.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (intent.resolveActivity(getPackageManager()) != null) {
                    String imageName = imageFile.getName();
                    //7.0以上 的拍照文件必须在storage/emulated/0/Android/data/包名/files/pictures文件夹
                    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File file = new File(storageDir + "/" + imageName);
                    //添加这一句表示对目标应用临时授权该Uri所代表的文件
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    mZiPaiuri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() +
                            ".fileProvider", file);
                }
            } else {//<24
                mZiPaiuri = Uri.fromFile(imageFile);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mZiPaiuri);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //当拍摄照片完成时会回调到onActivityResult 在这里处理照片的裁剪
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE: {
                    // 获得图片
                    try {
                        //该uri就是照片文件夹对应的uri
                        if (mZiPaiuri != null) {
                            cutWithUCrop(mZiPaiuri);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case UCROP_SELF: {//裁剪自拍完成
                    Uri resultUri = UCrop.getOutput(data);
                    if (resultUri != null) {
                        int resizeWidth = DevicesUtil.dp2px(getApplicationContext(), 168);
                        int resizeHeight = DevicesUtil.dp2px(getApplicationContext(), 170);
                        ImageLoadHelper.glideShowImageWithUri(ApplyVerifyHandActivity.this, resultUri,
                                mClickShootIv, resizeWidth, resizeHeight);
                        mSelectZiPaiLocalPath = resultUri.getPath();
                    }
                    break;
                }
            }
        }
    }

    /**
     * 使用u crop裁剪
     */
    private void cutWithUCrop(Uri sourceUri) {
        //计算 图片裁剪的大小
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            boolean res = pFile.mkdir();
            if (!res) {
                return;
            }
        }
        File file = new File(Constant.VERIFY_AFTER_RESIZE_DIR);
        if (!file.exists()) {
            boolean res = file.mkdir();
            if (!res) {
                return;
            }
        }
        String filePath = file.getPath() + File.separator + System.currentTimeMillis() + ".jpg";
        UCrop.of(sourceUri, Uri.fromFile(new File(filePath)))
                .withAspectRatio(720, 720)
                .withMaxResultSize(720, 720)
                .start(this, UCROP_SELF);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //删除REPORT目录中的图片
        FileUtil.deleteFiles(Constant.VERIFY_AFTER_RESIZE_DIR);
    }

}