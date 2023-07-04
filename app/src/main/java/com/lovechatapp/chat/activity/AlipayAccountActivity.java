package com.lovechatapp.chat.activity;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.yalantis.ucrop.UCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.AccountBean;
import com.lovechatapp.chat.bean.WithDrawBean;
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
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
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
 * 功能描述：管理微信账号页面
 * 作者：
 * 创建时间：2018/8/16
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class AlipayAccountActivity extends BaseActivity {

    @BindView(R.id.alipay_account_et)
    EditText mAlipayAccountEt;
    @BindView(R.id.real_name_et)
    EditText mRealNameEt;
    @BindView(R.id.submit_tv)
    TextView mSubmitTv;
    //收款码图片
    @BindView(R.id.money_code_iv)
    ImageView mMoneyCodeIv;
    //描述
    @BindView(R.id.money_des_tv)
    TextView mMoneyDesTv;

    private final int REQUEST_MONEY_CODE = 1;
    private final int UCROP_CODE = 2;
    //收款码本地地址
    private String mSelectLocalPath;
    //腾讯云
    private QServiceCfg mQServiceCfg;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_alipay_account_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.alipay_account);
        mQServiceCfg = QServiceCfg.instance(getApplicationContext());
        getMyAccountInfo();
    }

    /**
     * 获取用户可提现约豆
     */
    private void getMyAccountInfo() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_USEABLE_GOLD())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<WithDrawBean<AccountBean>>>() {
            @Override
            public void onResponse(BaseResponse<WithDrawBean<AccountBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    WithDrawBean<AccountBean> balanceBean = response.m_object;
                    if (balanceBean != null) {
                        //设置账号
                        List<AccountBean> accountBeans = balanceBean.data;
                        if (accountBeans != null && accountBeans.size() > 0) {
                            for (AccountBean bean : accountBeans) {
                                if (bean.t_type == 0) {//0.支付宝 1.微信
                                    String mAlipayName = bean.t_real_name;
                                    String mAlipayAccountNumber = bean.t_account_number;

                                    if (!TextUtils.isEmpty(mAlipayName)) {
                                        mRealNameEt.setText(mAlipayName);
                                    }
                                    if (!TextUtils.isEmpty(mAlipayAccountNumber)) {
                                        mAlipayAccountEt.setText(mAlipayAccountNumber);
                                    }
                                    //图片
                                    if (!TextUtils.isEmpty(bean.qrCode)) {
                                        mMoneyDesTv.setVisibility(View.GONE);
                                        ImageLoadHelper.glideShowImageWithUrl(AlipayAccountActivity.this,
                                                bean.qrCode, mMoneyCodeIv);
                                    }
                                    mRealNameEt.setEnabled(false);
                                    mAlipayAccountEt.setEnabled(false);
                                }
                            }
                        } else {
                            mRealNameEt.setEnabled(true);
                            mAlipayAccountEt.setEnabled(true);
                            mSubmitTv.setText(getResources().getString(R.string.finish));
                        }
                    } else {
                        mRealNameEt.setEnabled(true);
                        mAlipayAccountEt.setEnabled(true);
                        mSubmitTv.setText(getResources().getString(R.string.finish));
                    }
                }
            }
        });
    }

    @OnClick({R.id.submit_tv, R.id.code_fl})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_tv: {//保存
                String content = mSubmitTv.getText().toString().trim();
                if (content.equals(getResources().getString(R.string.save_safely))) {
                    mRealNameEt.setEnabled(true);
                    mAlipayAccountEt.setEnabled(true);
                    mSubmitTv.setText(getResources().getString(R.string.finish));
                } else {
                    String realName = mRealNameEt.getText().toString().trim();
                    if (TextUtils.isEmpty(realName)) {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_alipay_name);
                        return;
                    }
                    String alipayAccountNumber = mAlipayAccountEt.getText().toString().trim();
                    if (TextUtils.isEmpty(alipayAccountNumber)) {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_alipay_account);
                        return;
                    }
                    //收款码
                    if (TextUtils.isEmpty(mSelectLocalPath)) {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.money_code_picture_not_choose);
                        return;
                    }
                    File selfFile = new File(mSelectLocalPath);
                    if (!selfFile.exists()) {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.money_code_picture_not_choose);
                        return;
                    }
                    uploadCodeWithQQ();
                }
                break;
            }
            case R.id.code_fl: {//上传收款码
                mMoneyDesTv.setVisibility(View.GONE);
                //图片选择
                ImageHelper.openPictureChoosePage(AlipayAccountActivity.this, REQUEST_MONEY_CODE);
                break;
            }
        }
    }

    /**
     * 保存信息
     */
    private void saveInfo(String mVerifyImageHttpUrl) {
        String realName = mRealNameEt.getText().toString().trim();
        String alipayAccountNumber = mAlipayAccountEt.getText().toString().trim();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_real_name", realName);
        paramMap.put("t_nick_name", "");
        paramMap.put("t_account_number", alipayAccountNumber);
        paramMap.put("t_type", "0");//0.支付宝1.微信
        paramMap.put("qrCodeUrl", mVerifyImageHttpUrl);//收款码地址
        OkHttpUtils.post().url(ChatApi.MODIFY_PUT_FORWARD_DATA())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.save_success);
                    finish();
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.save_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.save_fail);
            }
        });
    }

    /**
     * 上传收款码到腾讯云
     */
    private void uploadCodeWithQQ() {
        String fileName = mSelectLocalPath.substring(mSelectLocalPath.length() - 17);
        String cosPath = "/verify/" + fileName;
        long signDuration = 600; //签名的有效期，单位为秒
        PutObjectRequest putObjectRequest = new PutObjectRequest(Constant.TENCENT_CLOUD_BUCKET, cosPath, mSelectLocalPath);
        putObjectRequest.setSign(signDuration, null, null);
        mQServiceCfg.getCosCxmService().putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                LogUtil.i("收款码 =  " + result.accessUrl);
                String mVerifyImageHttpUrl = result.accessUrl;
                if (!mVerifyImageHttpUrl.contains("http") || !mVerifyImageHttpUrl.contains("https")) {
                    mVerifyImageHttpUrl = "https://" + mVerifyImageHttpUrl;
                }
                saveInfo(mVerifyImageHttpUrl);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_MONEY_CODE) {//选择收款码
                List<Uri> mSelectedUris = Matisse.obtainResult(data);
                LogUtil.i("==--", "选择收款码: " + mSelectedUris);
                dealFile(mSelectedUris);
            } else if (requestCode == UCROP_CODE) {//裁剪收款码
                Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    int resizeWidth = DevicesUtil.dp2px(getApplicationContext(), 120);
                    int resizeHeight = DevicesUtil.dp2px(getApplicationContext(), 120);
                    ImageLoadHelper.glideShowImageWithUri(AlipayAccountActivity.this,
                            resultUri, mMoneyCodeIv, resizeWidth, resizeHeight);
                    mSelectLocalPath = resultUri.getPath();
                }
            }
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
            boolean res = pFile.mkdir();
            LogUtil.i("res: " + res);
        }
        File file = new File(Constant.VERIFY_AFTER_RESIZE_DIR);
        if (!file.exists()) {
            boolean res = file.mkdir();
            LogUtil.i("res: " + res);
        }
        String filePath = file.getPath() + File.separator + System.currentTimeMillis() + ".jpg";
        //请求码
        UCrop.of(sourceUri, Uri.fromFile(new File(filePath)))
                .withAspectRatio(480, 480)
                .withMaxResultSize(480, 480)
                .start(this, UCROP_CODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //删除REPORT目录中的图片
        FileUtil.deleteFiles(Constant.VERIFY_AFTER_RESIZE_DIR);
    }

}
