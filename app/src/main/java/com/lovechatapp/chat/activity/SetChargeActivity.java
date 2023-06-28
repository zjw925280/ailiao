package com.lovechatapp.chat.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.SetChargeRecyclerAdapter;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseListResponse;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.CharSetBean;
import com.lovechatapp.chat.bean.ChargeBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.layoutmanager.PickerLayoutManager;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.FileUtil;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.view.WheelView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：设置收费项目页面
 * 作者：
 * 创建时间：2018/6/30
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class SetChargeActivity extends BaseActivity {

    @BindView(R.id.video_chat_tv)
    TextView mVideoChatTv;
    @BindView(R.id.audio_chat_tv)
    TextView mAudioChatTv;
    @BindView(R.id.text_chat_tv)
    TextView mTextChatTv;
    @BindView(R.id.phone_tv)
    TextView mPhoneTv;
    @BindView(R.id.we_chat_tv)
    TextView mWeChatTv;
    @BindView(R.id.qq_tv)
    TextView mQQTv;

    private final int VIDEO = 0;//视频
    private final int TEXT = 1;//文字
    private final int PHONE = 2;//电话
    private final int WE_CHAT = 3;//微信
    private final int AUDIO = 4;//语音
    private final int QQ = 5;//语音
    private String mSelectContent = "";

    private List<CharSetBean> mCharSetBeans = new ArrayList<>();
    private String[] mVideoStrs = new String[]{};
    private String[] mTextStrs = new String[]{};
    private String[] mPhoneStrs = new String[]{};
    private String[] mWeChatStrs = new String[]{};
    private String[] mAudioStrs = new String[]{};
    private String[] mQQStrs = new String[]{};
    private ChargeBean bean = null;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_set_charge_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.set_charge);
        getAnchorVideoCost();
        getActorSetCharge();
    }

    /**
     * 获取收费设置
     */
    private void getAnchorVideoCost() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_ANTHOR_CHARGE_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<CharSetBean>>() {
                    @Override
                    public void onResponse(BaseListResponse<CharSetBean> response, int id) {
                        if (response != null && response.m_istatus == NetCode.SUCCESS) {
                            List<CharSetBean> charSetBeans = response.m_object;
                            if (charSetBeans != null && charSetBeans.size() > 0) {
                                mCharSetBeans = charSetBeans;
                                //处理bean
                                for (CharSetBean charSetBean : mCharSetBeans) {
                                    if (charSetBean.t_project_type == 5) {//5.视频聊天
                                        mVideoStrs = charSetBean.t_extract_ratio.split(",");
                                    } else if (charSetBean.t_project_type == 6) {//6.文字聊天
                                        mTextStrs = charSetBean.t_extract_ratio.split(",");
                                    } else if (charSetBean.t_project_type == 7) {//7.查看手机号
                                        mPhoneStrs = charSetBean.t_extract_ratio.split(",");
                                    } else if (charSetBean.t_project_type == 8) {//8.查看微信号
                                        mWeChatStrs = charSetBean.t_extract_ratio.split(",");
                                    } else if (charSetBean.t_project_type == 12) {//12.语音聊天
                                        mAudioStrs = charSetBean.t_extract_ratio.split(",");
                                    } else if (charSetBean.t_project_type == 13) {//13.查看QQ
                                        mQQStrs = charSetBean.t_extract_ratio.split(",");
                                    }
                                }
                            }
                        }
                    }
                });
    }

    /**
     * 获取主播收费设置
     */
    private void getActorSetCharge() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("anchorId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_ACTOR_CHARGE_SETUP())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ChargeBean>>() {
                    @Override
                    public void onResponse(BaseResponse<ChargeBean> response, int id) {
                        if (isFinishing()) {
                            return;
                        }
                        if (response != null && response.m_istatus == NetCode.SUCCESS) {
                            bean = response.m_object;
                            if (bean != null) {
                                mVideoChatTv.setText(FileUtil.parseFloatToString(bean.t_video_gold) + getResources().getString(R.string.gold));
                                mTextChatTv.setText(FileUtil.parseFloatToString(bean.t_text_gold) + getResources().getString(R.string.gold));
                                mAudioChatTv.setText(FileUtil.parseFloatToString(bean.t_voice_gold) + getString(R.string.gold));

                                float phoneGold = bean.t_phone_gold;
                                if (phoneGold > 0) {
                                    String phone = FileUtil.parseFloatToString(bean.t_phone_gold) + getResources().getString(R.string.gold);
                                    mPhoneTv.setText(phone);
                                } else {
                                    mPhoneTv.setText(getResources().getString(R.string.phone_private));
                                }

                                float wechatGold = bean.t_weixin_gold;
                                if (wechatGold > 0) {
                                    String wechat = FileUtil.parseFloatToString(bean.t_weixin_gold) + getResources().getString(R.string.gold);
                                    mWeChatTv.setText(wechat);
                                } else {
                                    mWeChatTv.setText(getResources().getString(R.string.phone_private));
                                }

                                mQQTv.setText(bean.t_qq_gold > 0 ?
                                        bean.t_qq_gold + getString(R.string.gold) :
                                        getResources().getString(R.string.phone_private));

                            } else {
                                String video = getResources().getString(R.string.zero) + getResources().getString(R.string.gold);
                                mVideoChatTv.setText(video);
                                String text = getResources().getString(R.string.zero) + getResources().getString(R.string.gold);
                                mTextChatTv.setText(text);
                                String phone = getResources().getString(R.string.zero) + getResources().getString(R.string.gold);
                                mPhoneTv.setText(phone);
                                String weChat = getResources().getString(R.string.zero) + getResources().getString(R.string.gold);
                                mWeChatTv.setText(weChat);
                                mAudioChatTv.setText(weChat);
                                mQQTv.setText(weChat);
                            }
                        }
                    }
                });
    }

    /**
     * 修改主播收费设置
     */
    private void modifyChargeSet() {

        //视频
        String video = "";
        String videoContent = mVideoChatTv.getText().toString().trim();
        String[] videoEnum = videoContent.split(getResources().getString(R.string.gold));
        if (videoEnum.length > 0) {
            video = videoEnum[0];
        }

        //语音
        String audio = "";
        String audioContent = mAudioChatTv.getText().toString().trim();
        String[] audioEnum = audioContent.split(getResources().getString(R.string.gold));
        if (audioEnum.length > 0) {
            audio = audioEnum[0];
        }

        //文字
        String text = "";
        String textContent = mTextChatTv.getText().toString().trim();
        String[] textEnum = textContent.split(getResources().getString(R.string.gold));
        if (textEnum.length > 0) {
            text = textEnum[0];
        }

        //电话
        String phone = "";
        String phoneContent = mPhoneTv.getText().toString().trim();
        if (phoneContent.contains(getResources().getString(R.string.phone_private))) {
            phone = "0";
        } else {
            String[] phoneEnum = phoneContent.split(getResources().getString(R.string.gold));
            if (phoneEnum.length > 0) {
                phone = phoneEnum[0];
            }
        }

        //微信
        String weChat = "";
        String weChatContent = mWeChatTv.getText().toString().trim();

        if (weChatContent.contains(getResources().getString(R.string.phone_private))) {
            weChat = "0";
        } else {
            String[] weChatEnum = weChatContent.split(getResources().getString(R.string.gold));
            if (weChatEnum.length > 0) {
                weChat = weChatEnum[0];
            }
        }

        //qq
        String qq = "";
        String qqContent = mQQTv.getText().toString().trim();
        if (qqContent.contains(getResources().getString(R.string.phone_private))) {
            qq = "0";
        } else {
            String[] qqEnum = qqContent.split(getResources().getString(R.string.gold));
            if (qqEnum.length > 0) {
                qq = qqEnum[0];
            }
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_video_gold", video);
        paramMap.put("t_text_gold", text);
        paramMap.put("t_phone_gold", phone);
        paramMap.put("t_weixin_gold", weChat);
        paramMap.put("t_voice_gold", audio);
        paramMap.put("t_qq_gold", qq);
        LogUtil.e("ralph", "params ===== " + paramMap);
        OkHttpUtils.post().url(ChatApi.UPDATE_CHARGE_SET())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response, int id) {
                        if (response != null) {
                            if (response.m_istatus == NetCode.SUCCESS) {
                                String message = response.m_strMessage;
                                if (!TextUtils.isEmpty(message)) {
                                    ToastUtil.INSTANCE.showToast(getApplicationContext(), message);
                                    finish();
                                }
                            } else {
                                if (!TextUtils.isEmpty(response.m_strMessage)) {
                                    ToastUtil.INSTANCE.showToast(getApplicationContext(), response.m_strMessage);
                                } else {
                                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.set_fail);
                                }
                            }
                        }
                    }
                });
    }

    @OnClick({R.id.video_chat_rl, R.id.submit_tv, R.id.text_rl, R.id.phone_rl, R.id.we_chat_rl, R.id.audio_rl, R.id.qq_rl})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_chat_rl: {//视频聊天
                if (mVideoStrs != null && mVideoStrs.length > 0) {
                    showChargeOptionDialog(VIDEO);
                }
                break;
            }
            case R.id.audio_rl: {//语音聊天
                if (mAudioStrs != null && mAudioStrs.length > 0) {
                    showChargeOptionDialog(AUDIO);
                }
                break;
            }
            case R.id.text_rl: {
                if (mTextStrs != null && mTextStrs.length > 0) {
                    showChargeOptionDialog(TEXT);
                }
                break;
            }
            case R.id.phone_rl: {
                if (mPhoneStrs != null && mPhoneStrs.length > 0) {
                    showChargeOptionDialog(PHONE);
                }
                break;
            }
            case R.id.we_chat_rl: {
                if (mWeChatStrs != null && mWeChatStrs.length > 0) {
                    showChargeOptionDialog(WE_CHAT);
                }
                break;
            }
            case R.id.qq_rl: {
                if (mQQStrs != null && mQQStrs.length > 0) {
                    showChargeOptionDialog(QQ);
                }
                break;
            }
            case R.id.submit_tv: {
                modifyChargeSet();
                break;
            }
        }
    }

    /**
     * 显示收费标准dialog
     */
    private void showChargeOptionDialog(int position) {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_set_charge_layout, null);
        setDialogView(view, mDialog, position);
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
    private void setDialogView(View view, final Dialog mDialog, final int passPosition) {
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(v -> mDialog.dismiss());

        TextView title_tv = view.findViewById(R.id.title_tv);

        final List<String> beans = new ArrayList<>();
        float currentSelection = 0f;
        switch (passPosition) {
            case VIDEO: {
                String content = getResources().getString(R.string.video_chat) + getResources().getString(R.string.gold_des);
                title_tv.setText(content);
                beans.addAll(Arrays.asList(mVideoStrs));
                break;
            }
            case AUDIO: {
                String content = getResources().getString(R.string.audio_chat) + getResources().getString(R.string.gold_des);
                title_tv.setText(content);
                beans.addAll(Arrays.asList(mAudioStrs));
                break;
            }
            case TEXT: {
                String content = getResources().getString(R.string.text_private_chat) + getResources().getString(R.string.gold_des);
                title_tv.setText(content);
                beans.addAll(Arrays.asList(mTextStrs));
                break;
            }
            case PHONE: {
                String content = getResources().getString(R.string.see_phone_gold) + getResources().getString(R.string.gold_des);
                title_tv.setText(content);
                beans.add(0, getResources().getString(R.string.phone_private));
                beans.addAll(Arrays.asList(mPhoneStrs));
                break;
            }
            case WE_CHAT: {
                String content = getResources().getString(R.string.see_we_chat_gold) + getResources().getString(R.string.gold_des);
                title_tv.setText(content);
                beans.add(0, getResources().getString(R.string.phone_private));
                beans.addAll(Arrays.asList(mWeChatStrs));
                break;
            }
            case QQ: {
                String content = getResources().getString(R.string.see_qq_gold) + getResources().getString(R.string.gold_des);
                title_tv.setText(content);
                beans.add(0, getResources().getString(R.string.phone_private));
                beans.addAll(Arrays.asList(mQQStrs));
                break;
            }
        }
        WheelView wheel = view.findViewById(R.id.wheelView);
        wheel.setItems(beans);
        wheel.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                LogUtil.i("位置: " + selectedIndex);
                mSelectContent = item;
            }
        });

        mSelectContent = null;

        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(v -> {
            switch (passPosition) {
                case VIDEO: {
                    if (TextUtils.isEmpty(mSelectContent)) {
                        mSelectContent = mVideoStrs[0];
                    }
                    String content = mSelectContent + getResources().getString(R.string.gold);
                    mVideoChatTv.setText(content);
                    mSelectContent = "";
                    break;
                }
                case AUDIO: {
                    if (TextUtils.isEmpty(mSelectContent)) {
                        mSelectContent = mAudioStrs[0];
                    }
                    String content = mSelectContent + getResources().getString(R.string.gold);
                    mAudioChatTv.setText(content);
                    mSelectContent = "";
                    break;
                }
                case TEXT: {
                    if (TextUtils.isEmpty(mSelectContent)) {
                        mSelectContent = mTextStrs[0];
                    }
                    String content = mSelectContent + getResources().getString(R.string.gold);
                    mTextChatTv.setText(content);
                    mSelectContent = "";
                    break;
                }
                case PHONE: {
                    if (TextUtils.isEmpty(mSelectContent)
                            || mSelectContent.equals(getResources().getString(R.string.phone_private))) {
                        mSelectContent = getResources().getString(R.string.phone_private);
                        mPhoneTv.setText(mSelectContent);
                    } else {
                        String content = mSelectContent + getResources().getString(R.string.gold);
                        mPhoneTv.setText(content);
                    }
                    mSelectContent = "";
                    break;
                }
                case WE_CHAT: {
                    if (TextUtils.isEmpty(mSelectContent)
                            || mSelectContent.equals(getResources().getString(R.string.phone_private))) {
                        mSelectContent = getResources().getString(R.string.phone_private);
                        mWeChatTv.setText(mSelectContent);
                    } else {
                        String content = mSelectContent + getResources().getString(R.string.gold);
                        mWeChatTv.setText(content);
                    }
                    mSelectContent = "";
                    break;
                }
                case QQ: {
                    if (TextUtils.isEmpty(mSelectContent)
                            || mSelectContent.equals(getResources().getString(R.string.phone_private))) {
                        mSelectContent = getResources().getString(R.string.phone_private);
                        mQQTv.setText(mSelectContent);
                    } else {
                        String content = mSelectContent + getResources().getString(R.string.gold);
                        mQQTv.setText(content);
                    }
                    mSelectContent = "";
                    break;
                }
            }
            mDialog.dismiss();
        });
    }
}