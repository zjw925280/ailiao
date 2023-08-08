package com.lovechatapp.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.CoverRecyclerAdapter;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.CoverUrlBean;
import com.lovechatapp.chat.bean.LabelBean;
import com.lovechatapp.chat.bean.PersonBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.dialog.CityPickerDialog;
import com.lovechatapp.chat.fragment.HomeCityFragment;
import com.lovechatapp.chat.helper.ImageHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.oss.QServiceCfg;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.FileUtil;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.view.WheelView;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.yalantis.ucrop.UCrop;
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
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：编辑修改个人资料
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ModifyUserInfoActivity extends BaseActivity {

    @BindView(R.id.nick_tv)
    TextView mNickTv;

    @BindView(R.id.job_tv)
    TextView mJobTv;

    @BindView(R.id.high_tv)
    TextView mHighTv;

    @BindView(R.id.age_tv)
    TextView mAgeTv;

    @BindView(R.id.body_tv)
    TextView mBodyTv;

    @BindView(R.id.marriage_tv)
    TextView mMarriageTv;

    @BindView(R.id.city_tv)
    TextView mCityTv;

    @BindView(R.id.sign_tv)
    EditText mSignEt;

    @BindView(R.id.submit_tv)
    TextView mSubmitTv;

    @BindView(R.id.evidence_rv)
    RecyclerView mEvidenceRv;

    @BindView(R.id.upload_iv)
    ImageView mUploadIv;

    @BindView(R.id.scrollView)
    LinearLayout mScrollView;

    //腾讯云
    private QServiceCfg mQServiceCfg;

    //封面相关
    private List<CoverUrlBean> mCoverUrlBeans = new ArrayList<>();
    private CoverRecyclerAdapter mCoverAdapter;

    //option选中的
    private String mOptionSelectStr = "";

    //修改昵称
    private final int SET_NICK = 0x09;

    private PersonBean<LabelBean, CoverUrlBean> personBean;

    private boolean isVerify;

    public static void verifyStart(Context context) {
        Intent starter = new Intent(context, ModifyUserInfoActivity.class);
        starter.putExtra("verify", true);
        context.startActivity(starter);
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_modify_user_info_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.edit_verify_info);
        isVerify = getIntent().getBooleanExtra("verify", false);
        mQServiceCfg = QServiceCfg.instance(ModifyUserInfoActivity.this);
        setListener();
        controlKeyboardLayout();
        getUserInfo();
    }

    private void setListener() {
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mCoverAdapter = new CoverRecyclerAdapter(this) {
            @Override
            protected void deleted() {
                mUploadIv.setVisibility(View.VISIBLE);
            }
        };
        mEvidenceRv.setLayoutManager(layoutManager);
        mEvidenceRv.setAdapter(mCoverAdapter);

        int size = (DevicesUtil.getScreenW(mContext) - DevicesUtil.dp2px(mContext, 40)) / 4;
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(size, size);
        params1.leftMargin = DevicesUtil.dp2px(mContext, 5);
        params1.rightMargin = DevicesUtil.dp2px(mContext, 5);
        mUploadIv.setLayoutParams(params1);
    }

    @OnClick({
            R.id.submit_tv,
            R.id.upload_iv,
            R.id.job_ll,
            R.id.age_rl,
            R.id.high_rl,
            R.id.body_rl,
            R.id.marriage_rl,
            R.id.city_rl,
            R.id.nick_rl,
    })
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.submit_tv: {

                //确认修改
                if (!submitCheckInput()) {
                    return;
                }

                //循环上传图片
                showLoadingDialog();
                uploadInfo();
                break;
            }

            case R.id.job_ll: {//职业
                showOptionDialog(JOB);
                break;
            }

            case R.id.age_rl: {//年龄
                showOptionDialog(AGE);
                break;
            }

            case R.id.high_rl: {//身高
                showOptionDialog(HIGH);
                break;
            }

            case R.id.body_rl: {//体重
                showOptionDialog(BODY);
                break;
            }

            case R.id.marriage_rl: {//婚姻状态
                showOptionDialog(MARRIAGE);
                break;
            }

            case R.id.city_rl: {//城市
                new CityPickerDialog(mContext) {
                    @Override
                    public void onSelected(String city, String city2) {
                        mCityTv.setText(city2);
                        checkInput();
                    }
                }.show();
                break;
            }

            case R.id.upload_iv: {//上传封面

                if (personBean == null) {
                    getUserInfo();
                    ToastUtil.INSTANCE.showToast("获取数据中");
                    return;
                }

                //判断是否多于6张
                if (mEvidenceRv.getChildCount() >= 6 || mCoverUrlBeans.size() >= 6) {
                    ToastUtil.INSTANCE.showToast(ModifyUserInfoActivity.this, R.string.four_most);
                    return;
                }

                //图片选择
                ImageHelper.openPictureChoosePage(ModifyUserInfoActivity.this, Constant.REQUEST_CODE_CHOOSE);
                break;
            }

            case R.id.nick_rl: {//昵称
                String content = mNickTv.getText().toString().trim();
                Intent intent = new Intent(ModifyUserInfoActivity.this, ModifyTwoActivity.class);
                intent.putExtra(Constant.MODIFY_TWO, 0);
                intent.putExtra(Constant.MODIFY_CONTENT, content);
                startActivityForResult(intent, SET_NICK);
                break;
            }

        }
    }

    /**
     * 显示收费标准dialog
     */
    private final int JOB = 0;
    private final int AGE = 1;
    private final int MARRIAGE = 2;
    private final int HIGH = 3;
    private final int BODY = 4;

    private void showOptionDialog(int index) {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_set_charge_layout, null);
        setDialogView(view, mDialog, index);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置 dialog view
     */
    private void setDialogView(View view, final Dialog mDialog, final int index) {
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(v -> mDialog.dismiss());
        TextView title_tv = view.findViewById(R.id.title_tv);

        final List<String> beans = new ArrayList<>();
        switch (index) {
            case JOB: {
                title_tv.setText(R.string.job);
                beans.add("网红");
                beans.add("模特");
                beans.add("白领");
                beans.add("护士");
                beans.add("空姐");
                beans.add("学生");
                beans.add("健身教练");
                beans.add("医生");
                beans.add("客服");
                beans.add("其他");
                break;
            }
            case AGE: {
                title_tv.setText(R.string.age_title);
                for (int i = 18; i < 100; i++) {
                    beans.add(String.valueOf(i));
                }
                break;
            }
            case MARRIAGE: {
                title_tv.setText(R.string.marriage);
                beans.add("保密");
                beans.add("未婚");
                beans.add("已婚");
                break;
            }
            case HIGH: {
                title_tv.setText(R.string.high_title_des);
                for (int i = 160; i < 200; i++) {
                    beans.add(String.valueOf(i));
                }
                break;
            }
            case BODY: {
                title_tv.setText(R.string.body_title_des);
                for (int i = 30; i < 81; i++) {
                    beans.add(String.valueOf(i));
                }
                break;
            }
        }
        WheelView wheel = view.findViewById(R.id.wheelView);
        wheel.setItems(beans);
        wheel.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                mOptionSelectStr = item;
                LogUtil.i("位置: " + selectedIndex + " " + mOptionSelectStr);
            }
        });
        mOptionSelectStr = beans.get(0);
        //确定
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(v -> {
            switch (index) {
                case JOB: {
                    mJobTv.setText(mOptionSelectStr);
                    break;
                }
                case AGE: {
                    mAgeTv.setText(mOptionSelectStr);
                    break;
                }
                case MARRIAGE: {
                    mMarriageTv.setText(mOptionSelectStr);
                    break;
                }
                case HIGH: {
                    mHighTv.setText(mOptionSelectStr);
                    break;
                }
                case BODY: {
                    mBodyTv.setText(mOptionSelectStr);
                    break;
                }
            }
            mDialog.dismiss();
            checkInput();
        });
    }

    /**
     * 获取个人信息
     */
    private void getUserInfo() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_PERSONAL_DATA())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PersonBean<LabelBean, CoverUrlBean>>>() {
            @Override
            public void onResponse(BaseResponse<PersonBean<LabelBean, CoverUrlBean>> response, int id) {

                if (isFinishing()) {
                    return;
                }

                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    personBean = response.m_object;

                    if (personBean != null) {

                        //昵称
                        mNickTv.setText(personBean.t_nickName);

                        //职业
                        String job = personBean.t_vocation;
                        if (!TextUtils.isEmpty(job)) {
                            mJobTv.setText(job);
                        }

                        //身高
                        if (personBean.t_height > 0) {
                            mHighTv.setText(String.valueOf(personBean.t_height));
                        }

                        //年龄
                        if (personBean.t_age > 0) {
                            mAgeTv.setText(String.valueOf(personBean.t_age));
                        }

                        //体重
                        if (personBean.t_weight > 0) {
                            mBodyTv.setText(String.valueOf(personBean.t_weight));
                        }

                        //婚姻状况
                        if (!TextUtils.isEmpty(personBean.t_marriage)) {
                            mMarriageTv.setText(personBean.t_marriage);
                        }

                        //城市
                        if (!TextUtils.isEmpty(personBean.t_city)) {
//                            if (LocationHelper.get().getLocation() != null) {
//                                mCityTv.setText(LocationHelper.get().getLocation().getCity());
//                            } else {
                                mCityTv.setText(personBean.t_city);
//                            }
                        }

                        //个性签名
                        if (!TextUtils.isEmpty(personBean.t_autograph)) {
                            mSignEt.setText(personBean.t_autograph);
                        }

                        //封面图
                        if (personBean.coverList != null && personBean.coverList.size() > 0) {
                            mCoverUrlBeans = personBean.coverList;
                            setThumbImage(mCoverUrlBeans);
                        }

                        //数据加载完成
                        checkInput();
                    }
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                dismissLoadingDialog();
            }

        });
    }

    /**
     * 上传资料
     */
    private void uploadInfo() {

        //昵称
        String nick = mNickTv.getText().toString().trim();

        //职业
        String job = mJobTv.getText().toString().trim();

        //身高
        String high = mHighTv.getText().toString().trim();

        //年龄
        String age = mAgeTv.getText().toString().trim();

        //体重
        String body = mBodyTv.getText().toString().trim();

        //婚姻状况
        String marriage = mMarriageTv.getText().toString().trim();

        //城市
        String city = mCityTv.getText().toString().trim();

        //个性签名
        String sign = mSignEt.getText().toString().trim();

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_nickName", nick);
//        paramMap.put("t_phone", phone);
        paramMap.put("t_height", high);
        paramMap.put("t_weight", body);
        paramMap.put("t_marriage", marriage);
        paramMap.put("t_city", city);
        paramMap.put("t_autograph", sign);
        paramMap.put("t_vocation", job);
        paramMap.put("t_age", age);
//        paramMap.put("t_weixin", weChat);
//        paramMap.put("t_qq", qqChat);
//        paramMap.put("t_handImg", TextUtils.isEmpty(mHeadImageHttpUrl) ? "" : mHeadImageHttpUrl);
//        paramMap.put("lables", labels);
        OkHttpUtils.post().url(ChatApi.UPDATE_PERSON_DATA())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                dismissLoadingDialog();
                if (response != null) {
                    String message = response.m_strMessage;
                    if (response.m_istatus == NetCode.SUCCESS) {
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.INSTANCE.showToast(ModifyUserInfoActivity.this, message);
                        }
                        HomeCityFragment.city = city;
                        finish();
                    } else {
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.INSTANCE.showToast(ModifyUserInfoActivity.this, message);
                        } else {
                            ToastUtil.INSTANCE.showToast(ModifyUserInfoActivity.this, R.string.edit_fail);
                        }
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(ModifyUserInfoActivity.this, R.string.edit_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dismissLoadingDialog();
                ToastUtil.INSTANCE.showToast(ModifyUserInfoActivity.this, R.string.edit_fail);
            }
        });
    }

    private final List<Integer> labelBg = new ArrayList<>();

    /**
     * 点击提交的时候的提示
     */
    private boolean submitCheckInput() {
        if (personBean == null) {
            ToastUtil.INSTANCE.showToast(ModifyUserInfoActivity.this, R.string.data_getting);
            getUserInfo();
            return false;
        }
        return checkInput(true);
    }

    private boolean checkInput() {
        return checkInput(false);
    }

    /**
     * 检查是否都填了
     */
    private boolean checkInput(boolean toast) {

        //昵称
        String nick = mNickTv.getText().toString().trim();
        if (TextUtils.isEmpty(nick)) {
            if (toast)
                ToastUtil.INSTANCE.showToast(ModifyUserInfoActivity.this, R.string.please_input_nick);
            return false;
        }

        //城市
        String city = mCityTv.getText().toString().trim();
        if (TextUtils.isEmpty(city)) {
            if (toast)
                ToastUtil.INSTANCE.showToast(ModifyUserInfoActivity.this, R.string.please_input_city);
            return false;
        }

        //如果是主播或认证主播，必须上传封面
        if (AppManager.getInstance().getUserInfo().t_role == 1 || isVerify) {

            //封面
            if (mCoverUrlBeans == null || mCoverUrlBeans.size() == 0) {
                if (toast)
                    ToastUtil.INSTANCE.showToast(ModifyUserInfoActivity.this, R.string.actor_at_least_upload_one);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == Constant.REQUEST_CODE_CHOOSE || requestCode == Constant.REQUEST_CODE_CHOOSE_HEAD_IMG)
                && resultCode == RESULT_OK) {//图库选择
            List<String> pathResult = Matisse.obtainPathResult(data);
            if (pathResult != null && pathResult.size() > 0) {
                try {
                    String filePath = pathResult.get(0);
                    if (!TextUtils.isEmpty(filePath)) {
                        File file = new File(filePath);
                        if (!file.exists()) {
                            LogUtil.i("文件不存在 ");
                        } else {
                            LogUtil.i("文件大小: " + file.length() / 1024);
                        }
                        //直接裁剪
                        if (requestCode == Constant.REQUEST_CODE_CHOOSE) {
                            cutWithUCrop(filePath, true);
                        } else {
                            cutWithUCrop(filePath, false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == RESULT_OK && (requestCode == Constant.UCROP_REQUEST_CODE_COVER || requestCode == Constant.UCROP_REQUEST_CODE_HEAD)) {
            Uri resultUri = UCrop.getOutput(data);
            String filePath = FileUtil.getPathAbove19(this, resultUri);
            if (requestCode == Constant.UCROP_REQUEST_CODE_COVER) {
                //上传封面图到腾讯云
                uploadCoverFileWithQQ(filePath);
            }
        } else if (requestCode == SET_NICK && resultCode == RESULT_OK) {//修改昵称
            if (data != null) {
                String phone = data.getStringExtra(Constant.MODIFY_CONTENT);
                if (!TextUtils.isEmpty(phone)) {
                    mNickTv.setText(phone);
                }
            }
        }
        checkInput();
    }

    /**
     * 使用u crop裁剪
     */
    private void cutWithUCrop(String sourceFilePath, boolean fromCover) {
        //计算 图片resize的大小
        int overWidth;
        int overHeight;
        if (fromCover) {
            overWidth = DevicesUtil.getScreenW(mContext);
            overHeight = DevicesUtil.dp2px(mContext, 435);
        } else {
            overWidth = DevicesUtil.getScreenW(ModifyUserInfoActivity.this);
            overHeight = DevicesUtil.getScreenW(ModifyUserInfoActivity.this);
        }
        //目录路径
        String dirPath;
        if (fromCover) {
            dirPath = Constant.COVER_AFTER_SHEAR_DIR;
        } else {
            dirPath = Constant.HEAD_AFTER_SHEAR_DIR;
        }
        File pFile = new File(FileUtil.YCHAT_DIR);
        if (!pFile.exists()) {
            boolean res = pFile.mkdir();
            LogUtil.i("res: " + res);
        }
        File file = new File(dirPath);
        if (!file.exists()) {
            boolean res = file.mkdir();
            LogUtil.i("res: " + res);
        }
        if (!fromCover) {
            FileUtil.deleteFiles(dirPath);
        }
        //文件名
        String filePath = file.getPath() + File.separator + System.currentTimeMillis() + ".png";
        //请求码
        int requestCode;
        if (fromCover) {
            requestCode = Constant.UCROP_REQUEST_CODE_COVER;
        } else {
            requestCode = Constant.UCROP_REQUEST_CODE_HEAD;
        }
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            ToastUtil.INSTANCE.showToast(ModifyUserInfoActivity.this, R.string.file_not_exist);
            return;
        }
        UCrop.of(Uri.fromFile(new File(sourceFilePath)), Uri.fromFile(new File(filePath)))
                .withAspectRatio(overWidth, overHeight)
                .withMaxResultSize(overWidth, overHeight)
                .start(this, requestCode);
    }

    /**
     * 使用腾讯云上传封面文件
     */
    private void uploadCoverFileWithQQ(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            ToastUtil.INSTANCE.showToast(ModifyUserInfoActivity.this, R.string.file_invalidate);
            return;
        }
        //文件名
        String fileName;
        if (filePath.length() < 50) {
            fileName = filePath.substring(filePath.length() - 17);
        } else {
            String last = filePath.substring(filePath.length() - 4);
            if (last.contains("png")) {
                fileName = System.currentTimeMillis() + ".png";
            } else {
                fileName = System.currentTimeMillis() + ".jpg";
            }
        }

        String cosPath = "/cover/" + fileName;
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
                //更新到自己服务器
                //上传封面
                uploadToSelfServer(resultUrl);
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                String errorMsg = clientException != null ? clientException.toString() : serviceException.toString();
                LogUtil.i("腾讯云fail: " + errorMsg);
                ToastUtil.INSTANCE.showToast(ModifyUserInfoActivity.this, R.string.upload_fail);
            }
        });
    }

    /**
     * 更新封面到自己服务器
     */
    private void uploadToSelfServer(final String imgUrl) {
        String t_first = "1";//不是主封面
        if (mCoverUrlBeans != null && mCoverUrlBeans.size() == 0) {
            t_first = "0";//第一张
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverImg", imgUrl);
        paramMap.put("t_first", t_first);//0 是主封面  1 主封面
        OkHttpUtils.post().url(ChatApi.REPLACE_COVER_IMG())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<Integer>>() {
            @Override
            public void onResponse(BaseResponse<Integer> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    Integer t_id = response.m_object;
                    CoverUrlBean bean = new CoverUrlBean();
                    bean.t_img_url = imgUrl;
                    bean.t_id = t_id;
                    mCoverUrlBeans.add(bean);
                    setThumbImage(mCoverUrlBeans);
                } else {
                    ToastUtil.INSTANCE.showToast(ModifyUserInfoActivity.this, R.string.upload_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(ModifyUserInfoActivity.this, R.string.upload_fail);
            }
        });
    }

    /**
     * 显示缩略图到封面
     */
    private void setThumbImage(List<CoverUrlBean> coverUrlBeans) {
        if (coverUrlBeans != null && coverUrlBeans.size() >= 6) {
            mUploadIv.setVisibility(View.GONE);
        }
        mCoverAdapter.loadData(coverUrlBeans);
        mEvidenceRv.setVisibility(View.VISIBLE);
    }

    /**
     * 键盘
     */
    private void controlKeyboardLayout() {
        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            mScrollView.getWindowVisibleDisplayFrame(r);
            //r.top 是状态栏高度
            int screenHeight = mScrollView.getRootView().getHeight();
            int softHeight = screenHeight - r.bottom;
            if (softHeight > 200) {//当输入法高度大于100判定为输入法打开了
                mScrollView.scrollTo(0, 150);
            } else {//否则判断为输入法隐藏了
                mScrollView.scrollTo(0, 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //删除cover目录中的图片
        FileUtil.deleteFiles(Constant.COVER_AFTER_SHEAR_DIR);
        FileUtil.deleteFiles(Constant.HEAD_AFTER_SHEAR_DIR);

    }
}