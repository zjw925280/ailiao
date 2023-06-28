package com.lovechatapp.chat.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.yalantis.ucrop.UCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.ImageHelper;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.oss.QServiceCfg;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.FileUtil;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.util.VerifyUtils;
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;


/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：申请工会页面
 * 作者：
 * 创建时间：2018/9/11
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ApplyCompanyActivity extends BaseActivity {

    @BindView(R.id.company_name_et)
    EditText mCompanyNameEt;
    @BindView(R.id.apply_name_et)
    EditText mApplyNameEt;
    @BindView(R.id.contact_et)
    EditText mContactEt;
    @BindView(R.id.apply_count_et)
    EditText mApplyCountEt;
    @BindView(R.id.agree_iv)
    ImageView mAgreeIv;
    @BindView(R.id.head_img_iv)
    ImageView mHeadImgIv;
    @BindView(R.id.id_card_et)
    EditText mIdCardEt;

    private String mSelectLocalPath;
    private Dialog mDialog;

    //腾讯云
    private QServiceCfg mQServiceCfg;
    private String mRealName;
    private String mIdNumber;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_apply_company_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.apply_company);
        mAgreeIv.setSelected(true);
        mQServiceCfg = QServiceCfg.instance(getApplicationContext());
    }

    @OnClick({R.id.agree_iv, R.id.share_now_tv, R.id.upload_head_img_ll, R.id.agree_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.agree_iv: {
                if (mAgreeIv.isSelected()) {
                    mAgreeIv.setSelected(false);
                } else {
                    mAgreeIv.setSelected(true);
                }
                break;
            }
            case R.id.share_now_tv: {
                applyCompany();
                break;
            }
            case R.id.upload_head_img_ll: {//上传头像
                //图片选择
                ImageHelper.openPictureChoosePage(ApplyCompanyActivity.this, Constant.REQUEST_CODE_CHOOSE);
                break;
            }
            case R.id.agree_tv: {//协议
                Intent intent = new Intent(getApplicationContext(), CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, getResources().getString(R.string.chat_company_agree));
                intent.putExtra(Constant.URL, "file:///android_asset/company_agree.html");
                startActivity(intent);
                break;
            }
        }
    }

    /**
     * 申请工会
     */
    private void applyCompany() {
        //公会名
        String companyName = mCompanyNameEt.getText().toString().trim();
        if (TextUtils.isEmpty(companyName)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_company_name);
            return;
        }
        //申请人姓名
        mRealName = mApplyNameEt.getText().toString().trim();
        if (TextUtils.isEmpty(mRealName)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_apply_name);
            return;
        }
        mIdNumber = mIdCardEt.getText().toString().trim();
        if (TextUtils.isEmpty(mIdNumber)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_id_card_number);
            return;
        }
        //申请人电话
        String contactNumber = mContactEt.getText().toString().trim();
        if (TextUtils.isEmpty(contactNumber)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_apply_contact);
            return;
        }
        if (!VerifyUtils.isPhoneNum(contactNumber)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.wrong_phone_number);
            return;
        }
        //主播人数
        String actorNumber = mApplyCountEt.getText().toString().trim();
        if (TextUtils.isEmpty(actorNumber)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_apply_count);
            return;
        }
        //协议
        if (!mAgreeIv.isSelected()) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.not_agree);
            return;
        }
        //头像文件
        if (TextUtils.isEmpty(mSelectLocalPath)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.picture_not_choose);
            return;
        }
        File file = new File(mSelectLocalPath);
        if (!file.exists()) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.file_not_exist);
            return;
        }

        uploadFileWithQQ();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> mSelectedUris = Matisse.obtainResult(data);
            LogUtil.i("==--", "mSelected: " + mSelectedUris);
            dealFile(mSelectedUris);
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                int resizeWidth = DevicesUtil.dp2px(getApplicationContext(), 96);
                int resizeHeight = DevicesUtil.dp2px(getApplicationContext(), 64);
                ImageLoadHelper.glideShowImageWithUri(ApplyCompanyActivity.this, resultUri,
                        mHeadImgIv, resizeWidth, resizeHeight);
                mSelectLocalPath = resultUri.getPath();
            }
            //用完之后应该删除
        } else if (resultCode == UCrop.RESULT_ERROR) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.choose_picture_failed);
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
                    cutWithUCrop(uri);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            pFile.mkdir();
        }
        File file = new File(Constant.VERIFY_AFTER_RESIZE_DIR);
        if (!file.exists()) {
            file.mkdir();
        } else {
            FileUtil.deleteFiles(file.getPath());
        }
        String filePath = file.getPath() + File.separator + System.currentTimeMillis() + ".jpg";
        UCrop.of(sourceUri, Uri.fromFile(new File(filePath)))
                .withAspectRatio(1280, 960)
                .withMaxResultSize(1280, 960)
                .start(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //删除REPORT目录中的图片
        FileUtil.deleteFiles(Constant.VERIFY_AFTER_RESIZE_DIR);
    }

    /**
     * 上传认证图到腾讯云
     */
    private void uploadFileWithQQ() {
        File file = new File(mSelectLocalPath);
        if (!file.exists()) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.verify_fail);
            return;
        }
        String fileName = mSelectLocalPath.substring(mSelectLocalPath.length() - 17, mSelectLocalPath.length());
        String cosPath = "/verify/" + fileName;
        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(Constant.TENCENT_CLOUD_BUCKET, cosPath, mSelectLocalPath);
        putObjectRequest.setSign(signDuration, null, null);
        putObjectRequest.setProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long progress, long max) {
            }
        });
        mQServiceCfg.getCosCxmService().putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                LogUtil.i("腾讯云success 认证=  " + result.accessUrl);
                String mVerifyImageHttpUrl = result.accessUrl;
                if (!mVerifyImageHttpUrl.contains("http") || !mVerifyImageHttpUrl.contains("https")) {
                    mVerifyImageHttpUrl = "https://" + mVerifyImageHttpUrl;
                }
                uploadInfo(mVerifyImageHttpUrl);
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.verify_fail);
            }
        });
    }

    /**
     * 上传信息到公司接口
     */
    private void uploadInfo(String verifyImageHttpUrl) {
        //公会名
        String companyName = mCompanyNameEt.getText().toString().trim();
        //申请人电话
        String contactNumber = mContactEt.getText().toString().trim();
        //主播人数
        String actorNumber = mApplyCountEt.getText().toString().trim();

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("guildName", companyName);
        paramMap.put("adminName", mRealName);
        paramMap.put("adminPhone", contactNumber);
        paramMap.put("anchorNumber", actorNumber);
        paramMap.put("idCard", mIdNumber);
        paramMap.put("handImg", verifyImageHttpUrl);
        OkHttpUtils.post().url(ChatApi.APPLY_GUILD())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.apply_success);
                    finish();
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.verify_fail);
                }
            }
        });
    }

}
