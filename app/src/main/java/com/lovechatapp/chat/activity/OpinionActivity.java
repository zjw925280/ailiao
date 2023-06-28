package com.lovechatapp.chat.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.ImageHelper;
import com.lovechatapp.chat.listener.OnFileUploadListener;
import com.lovechatapp.chat.listener.OnLuCompressListener;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.oss.QServiceCfg;
import com.lovechatapp.chat.util.BitmapUtil;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.FileUtil;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.util.VerifyUtils;
import com.zhihu.matisse.Matisse;
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
 * 功能描述：意见反馈页面
 * 作者：
 * 创建时间：2018/6/25
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class OpinionActivity extends BaseActivity {

    @BindView(R.id.evidence_ll)
    LinearLayout mEvidenceLl;//证据Ll
    @BindView(R.id.mobile_et)
    EditText mMobileEt;
    @BindView(R.id.input_et)
    EditText mInputEt;

    //腾讯云
    private QServiceCfg mQServiceCfg;
    private List<String> mSelectFilePath = new ArrayList<>();
    private String mCoverImageHttpUrl = "";//拼接图片url

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_opinion_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.opinion_feed_back);
        mQServiceCfg = QServiceCfg.instance(getApplicationContext());
    }

    @OnClick({R.id.upload_iv, R.id.submit_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_iv: {//上传证据
                //判断是否多于5张
                if (mEvidenceLl.getChildCount() >= 4) {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.five_most);
                    return;
                }
                //图片选择
                ImageHelper.openPictureChoosePage(OpinionActivity.this, Constant.REQUEST_CODE_CHOOSE);
                break;
            }
            case R.id.submit_tv: {
                submitOpinion();
                break;
            }

        }
    }

    /**
     * 提交意见反馈
     */
    private void submitOpinion() {
        final String content = mInputEt.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_your_opinion);
            return;
        }
        final String phone = mMobileEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_phone);
            return;
        }
        if (!VerifyUtils.isPhoneNum(phone)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.wrong_phone_number);
            return;
        }

        showLoadingDialog();

        if (mSelectFilePath != null && mSelectFilePath.size() > 0) {
            uploadReportFileWithQQ(0, new OnFileUploadListener() {
                @Override
                public void onFileUploadSuccess() {
                    submit(phone, content);
                }
            });
        } else {
            submit(phone, content);
        }
    }

    /**
     * 提交
     */
    private void submit(String phone, String content) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_phone", TextUtils.isEmpty(phone) ? "" : phone);
        paramMap.put("t_img_url", mCoverImageHttpUrl);
        paramMap.put("content", content);
        OkHttpUtils.post().url(ChatApi.ADD_FEED_BACK())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                dismissLoadingDialog();
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.success_str))) {
                        showSeeWeChatRemindDialog();
                    }else {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.submit_fail);
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.submit_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.submit_fail);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 显示查看微信号提醒
     */
    private void showSeeWeChatRemindDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_thanks_layout, null);
        //bitmap
        TextView ok_tv = view.findViewById(R.id.ok_tv);
        ok_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                finish();
            }
        });

        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> mSelectedUris = Matisse.obtainResult(data);
            LogUtil.i("==--", "mSelected: " + mSelectedUris);
            dealFile(mSelectedUris);
        }
    }

    /**
     * 处理返回的图片,过大的话 就压缩
     * 每次只允许选择一张,所以只处理第一个
     */
    private void dealFile(List<Uri> mSelectedUris) {
        if (mSelectedUris != null && mSelectedUris.size() > 0) {
            try {
                Uri uri = mSelectedUris.get(0);
                String filePath = FileUtil.getPathAbove19(this, uri);
                if (!TextUtils.isEmpty(filePath)) {
                    File file = new File(filePath);
                    LogUtil.i("==--", "file大小: " + file.length() / 1024);
                    //压缩图片
                    compressImageWithLuBan(filePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用LuBan压缩图片
     */
    private void compressImageWithLuBan(String filePath) {
        ImageHelper.compressImageWithLuBan(getApplicationContext(), filePath, Constant.AFTER_COMPRESS_DIR, new OnLuCompressListener() {
            @Override
            public void onStart() {
                showLoadingDialog();
            }

            @Override
            public void onSuccess(File file) {
                mSelectFilePath.add(file.getAbsolutePath());
                Bitmap bitmap = BitmapUtil.getImageThumbnail(file.getPath(),
                        DevicesUtil.dp2px(getApplicationContext(), 50), DevicesUtil.dp2px(getApplicationContext(), 50));
                addImageToLinearLayout(bitmap);
                dismissLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.choose_picture_failed);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 添加imageView到Ll布局
     */
    private void addImageToLinearLayout(Bitmap bitmap) {
        LogUtil.i("==--", "分配的大小: " + bitmap.getAllocationByteCount() / 1024);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DevicesUtil.dp2px(getApplicationContext(), 50),
                DevicesUtil.dp2px(getApplicationContext(), 50));
        layoutParams.leftMargin = 15;
        layoutParams.rightMargin = 15;
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageBitmap(bitmap);
        mEvidenceLl.addView(imageView);
        mEvidenceLl.setVisibility(View.VISIBLE);
    }

    /**
     * 使用腾讯云上传封面文件
     */
    private void uploadReportFileWithQQ(final int index, final OnFileUploadListener listener) {
        String filePath = mSelectFilePath.get(index);
        //文件名
        String fileName;
        if (filePath.length() < 50) {
            fileName = filePath.substring(filePath.length() - 17, filePath.length());
        } else {
            String last = filePath.substring(filePath.length() - 4, filePath.length());
            if (last.contains("png")) {
                fileName = System.currentTimeMillis() + ".png";
            } else {
                fileName = System.currentTimeMillis() + ".jpg";
            }
        }

        String cosPath = "/report/" + fileName;
        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(Constant.TENCENT_CLOUD_BUCKET, cosPath, filePath);
        putObjectRequest.setSign(signDuration, null, null);
        mQServiceCfg.getCosCxmService().putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                LogUtil.i("腾讯云success =  " + result.accessUrl);
                String resultUrl = result.accessUrl;
                if (!resultUrl.contains("http") || !resultUrl.contains("https")) {
                    resultUrl = "https://" + resultUrl;
                }
                mCoverImageHttpUrl += resultUrl + ",";
                if (mSelectFilePath != null) {
                    if (mSelectFilePath.size() > index + 1) {//如果还有下一张,就继续上传
                        uploadReportFileWithQQ(index + 1, listener);
                    } else {//没有下一张了就上传头像
                        listener.onFileUploadSuccess();
                    }
                }
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                String errorMsg = clientException != null ? clientException.toString() : serviceException.toString();
                LogUtil.i("腾讯云fail: " + errorMsg);
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.choose_picture_failed);
                dismissLoadingDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //删除REPORT目录中的图片
        FileUtil.deleteFiles(Constant.AFTER_COMPRESS_DIR);
    }


}
