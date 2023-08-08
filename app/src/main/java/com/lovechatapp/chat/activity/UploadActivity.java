package com.lovechatapp.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.dialog.ChoosePriceDialog;
import com.lovechatapp.chat.helper.ImageHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.FileUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.util.VideoFrame;
import com.lovechatapp.chat.util.upload.FileUploadManager;
import com.lovechatapp.chat.util.upload.UploadTask;
import com.lovechatapp.chat.view.MyProcessView;
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class UploadActivity extends BaseActivity {

    @BindView(R.id.title_et)
    EditText mTitleEt;

    @BindView(R.id.money_tv)
    TextView mMoneyTv;

    @BindView(R.id.upload_iv)
    ImageView mUploadIv;

    @BindView(R.id.process_pv)
    MyProcessView mProcessPv;

    @BindView(R.id.charge_rl)
    View mChargeRl;

    @BindView(R.id.video_done_tv)
    TextView mVideoDoneTv;

    //	0.图片 1.视频
    private int mType;

    /**
     * 视频上传部分
     */
    //视频封面上传
    private UploadTask videoCoverPictureTask;
    //视频上传
    private UploadTask videoTask;

    /**
     * 图片上传部分
     */
    private UploadTask imgTask;

    /**
     * 只能选择（图片\视频）其中一种上传
     */
    private boolean onlyOneType;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_upload_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.upload_album);

        int type = getIntent().getIntExtra("type", -1);
        onlyOneType = type >= 0;
        if (onlyOneType) {
            mType = type;
        }

        //主播
        if (AppManager.getInstance().getUserInfo().isWomenActor()) {
            mChargeRl.setVisibility(View.VISIBLE);
        } else {
            mChargeRl.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({
            R.id.upload_iv,
            R.id.submit_tv,
            R.id.left_fl,
            R.id.charge_rl
    })
    public void onClick(View v) {
        switch (v.getId()) {

            //收费设置
            case R.id.charge_rl: {
                if (videoTask == null && imgTask == null) {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_choose_file);
                    return;
                }
                new ChoosePriceDialog(UploadActivity.this, isVideo()) {
                    @Override
                    public void execute(String data) {
                        mMoneyTv.setText(data);
                    }
                }.show();
                break;
            }

            //选择图片/视频
            case R.id.upload_iv: {
                if (onlyOneType) {
                    if (mType == 0) {
                        ImageHelper.openPictureChoosePage(
                                UploadActivity.this,
                                Constant.REQUEST_CODE_CHOOSE);
                    } else {
                        ImageHelper.openVideoChoosePage(
                                UploadActivity.this,
                                Constant.REQUEST_CODE_CHOOSE_VIDEO);
                    }
                } else {
                    showSelectDialog();
                }
                break;
            }

            case R.id.submit_tv: {
                if (videoTask == null && imgTask == null) {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_choose_file);
                    return;
                }
                if (isVideo()) {
                    uploadVideo();
                } else {
                    uploadPicture();
                }
                break;
            }

            case R.id.left_fl: {
                finish();
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_CHOOSE_VIDEO || requestCode == Constant.REQUEST_CODE_CHOOSE) {
            //视频
            if (requestCode == Constant.REQUEST_CODE_CHOOSE_VIDEO && resultCode == RESULT_OK) {
                mType = 1;
                dealVideoFile(Matisse.obtainPathResult(data));
            }

            //图片
            else if (requestCode == Constant.REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
                mType = 0;
                dealImageFile(Matisse.obtainPathResult(data));
            }

            mProcessPv.setVisibility(View.GONE);
            mVideoDoneTv.setVisibility(View.GONE);
        }
    }

    /**
     * 处理视频文件
     */
    private void dealVideoFile(List<String> mSelectedUris) {
        if (mSelectedUris != null && mSelectedUris.size() > 0) {
            try {
                final String filePath = mSelectedUris.get(0);
                if (!TextUtils.isEmpty(filePath)) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.file_not_exist);
                        return;
                    } else {
                        if (!FileUtil.checkVideoFileSize(file)) {
                            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.file_too_big);
                            return;
                        }
                    }

                    //获取视频封面&最后一帧图片
                    VideoFrame videoFrame = new VideoFrame(filePath) {
                        @Override
                        public void complete(File firstFile) {
                            super.complete(firstFile);
                            if (isOk) {
                                videoCoverPictureTask = new UploadTask();
                                videoCoverPictureTask.filePath = firstFile.getAbsolutePath();

                                videoTask = new UploadTask(true);
                                videoTask.filePath = filePath;
                                videoTask.setProgressListener(integer -> {
                                    mProcessPv.setVisibility(View.VISIBLE);
                                    mProcessPv.setProcess(integer);
                                });
                                videoTask.setListener(uploadTask -> {
                                    mProcessPv.setVisibility(View.GONE);
                                    mVideoDoneTv.setVisibility(uploadTask.isOk() ? View.VISIBLE : View.GONE);
                                });

                                Glide.with(mUploadIv)
                                        .load(firstFile)
                                        .centerCrop()
                                        .override(mUploadIv.getWidth(), mUploadIv.getHeight())
                                        .into(mUploadIv);
                            } else {
                                ToastUtil.INSTANCE.showToast(getApplicationContext(), "视频文件读取失败，请选择其他视频");
                            }
                        }
                    };
                    videoFrame.execute();
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 每次只允许选择一张,所以只处理第一个
     */
    private void dealImageFile(List<String> mSelectedUris) {
        if (mSelectedUris != null && mSelectedUris.size() > 0) {
            try {
                String filePath = mSelectedUris.get(0);
                if (!TextUtils.isEmpty(filePath)) {
                    imgTask = new UploadTask();
                    imgTask.filePath = filePath;
                    Glide.with(mUploadIv)
                            .load(filePath)
                            .centerCrop()
                            .override(mUploadIv.getWidth(), mUploadIv.getHeight())
                            .into(mUploadIv);
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), "图片文件读取失败，请选择其他图片");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isVideo() {
        return 1 == mType;
    }

    /**
     * 图片、视频选择dialog
     */
    private void showSelectDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_video_image_layout, null);
        setDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.BottomPopupAnimation);
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 图片、视频选择dialog
     */
    private void setDialogView(View view, final Dialog mDialog) {

        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(v -> mDialog.dismiss());

        TextView album_tv = view.findViewById(R.id.album_tv);
        album_tv.setOnClickListener(v -> {
            //图片选择
            ImageHelper.openPictureChoosePage(UploadActivity.this, Constant.REQUEST_CODE_CHOOSE);
            mDialog.dismiss();
        });

        TextView take_tv = view.findViewById(R.id.take_tv);
        take_tv.setOnClickListener(v -> {
            //视频选择
            ImageHelper.openVideoChoosePage(UploadActivity.this, Constant.REQUEST_CODE_CHOOSE_VIDEO);
            mDialog.dismiss();
        });
    }

    /**
     * 提交视频
     */
    private void uploadVideo() {
        if (videoTask != null) {
            showLoadingDialog();
            FileUploadManager.permissionUpload(
                    Arrays.asList(
                            videoTask,
                            videoCoverPictureTask),
                    b -> {
                        if (b) {
                            addMyPhotoAlbum();
                        } else {
                            dismissLoadingDialog();
                        }
                    });
        } else {
            ToastUtil.INSTANCE.showToast("请选择视频上传");
        }
    }

    /**
     * 提交图片
     */
    private void uploadPicture() {
        if (imgTask != null) {
            showLoadingDialog();
            FileUploadManager.permissionUpload(
                    Arrays.asList(imgTask),
                    b -> {
                        if (b) {
                            addMyPhotoAlbum();
                        } else {
                            dismissLoadingDialog();
                        }
                    });
        } else {
            ToastUtil.INSTANCE.showToast("请选择图片上传");
        }
    }

    /**
     * 新增相册数据
     */
    private void addMyPhotoAlbum() {

        String title = mTitleEt.getText().toString().trim();
        String money = mMoneyTv.getText().toString().trim();

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_title", title);
        paramMap.put("type", mType);
        paramMap.put("gold", money);

        if (isVideo()) {
            paramMap.put("video_img", videoCoverPictureTask.url);
            paramMap.put("url", videoTask.url);
            paramMap.put("fileId", videoTask.videoId);
        } else {
            paramMap.put("url", imgTask.url);
        }

        OkHttpUtils.post().url(ChatApi.ADD_MY_PHOTO_ALBUM())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                dismissLoadingDialog();
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.success_str))) {
                        getWindow().getDecorView().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 100);
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.upload_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dismissLoadingDialog();
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.upload_fail);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtil.deleteFiles(Constant.AFTER_COMPRESS_DIR);
    }
}