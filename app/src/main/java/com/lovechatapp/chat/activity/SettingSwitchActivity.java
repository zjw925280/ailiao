package com.lovechatapp.chat.activity;

import android.view.View;
import android.widget.ImageView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.UserCenterBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 功能开关
 */
public class SettingSwitchActivity extends BaseActivity {

    @BindView(R.id.sound_iv)
    ImageView mSoundIv;

    @BindView(R.id.vibrate_iv)
    ImageView mVibrateIv;

    private UserCenterBean userCenterBean;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_setting_switch);
    }

    @Override
    protected void onContentAdded() {
        setTitle("消息设置");
        initView();
        refreshSwitch();
    }

    /**
     * 初始化版本
     */
    private void initView() {

        //消息提示音
        boolean sound = SharedPreferenceHelper.getTipSound(getApplicationContext());
        mSoundIv.setSelected(sound);

        //消息震动
        boolean vibrate = SharedPreferenceHelper.getTipVibrate(getApplicationContext());
        mVibrateIv.setSelected(vibrate);

    }

    /**
     * 获取个人中心信息
     */
    private void getInfo() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        OkHttpUtils.post().url(ChatApi.INDEX())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<UserCenterBean>>() {
            @Override
            public void onResponse(BaseResponse<UserCenterBean> response, int id) {
                if (isFinishing())
                    return;
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    if (response.m_object != null) {
                        userCenterBean = response.m_object;
                        refreshSwitch();
                    }
                }
            }
        });
    }

    /**
     * 设置开关
     */
    private void refreshSwitch() {

        View switchVideo = findViewById(R.id.video_chat_iv);
        View switchAudio = findViewById(R.id.audio_chat_iv);
        View switchPrivate = findViewById(R.id.private_chat_iv);

        if (userCenterBean != null) {
            switchAudio.setSelected(userCenterBean.t_voice_switch == 1);
            switchPrivate.setSelected(userCenterBean.t_text_switch == 1);
            switchVideo.setSelected(userCenterBean.t_is_not_disturb == 1);
        } else {
            getInfo();
        }

        switchVideo.setTag(1);
        switchAudio.setTag(2);
        switchPrivate.setTag(3);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (userCenterBean == null) {
                    ToastUtil.INSTANCE.showToast("获取数据中");
                    getInfo();
                    return;
                }

                mContext.showLoadingDialog();

                final int type = Integer.parseInt(view.getTag().toString());
                final boolean isSelected = !view.isSelected();
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("userId", mContext.getUserId());
                paramMap.put("chatType", type);
                paramMap.put("switchType", isSelected ? 1 : 0);
                OkHttpUtils.post().url(ChatApi.setUpChatSwitch())
                        .addParams("param", ParamUtil.getParam(paramMap))
                        .build().execute(
                        new AjaxCallback<BaseResponse>() {

                            @Override
                            public void onResponse(BaseResponse response, int id) {
                                if (isFinishing())
                                    return;
                                mContext.dismissLoadingDialog();
                                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                                    view.setSelected(isSelected);
                                    if (!isSelected) {
                                        int[] strings = {
                                                R.string.swtich_off_alert_video,
                                                R.string.swtich_off_alert_audio,
                                                R.string.swtich_off_alert_private};
                                        ToastUtil.INSTANCE.showToast(mContext, strings[type - 1]);
                                    }
                                } else {
                                    ToastUtil.INSTANCE.showToast(mContext, R.string.system_error);
                                }
                            }

                            @Override
                            public void onError(Call call, Exception e, int id) {
                                super.onError(call, e, id);
                                if (isFinishing())
                                    return;
                                mContext.dismissLoadingDialog();
                            }
                        });
            }
        };
        switchVideo.setOnClickListener(onClickListener);
        switchAudio.setOnClickListener(onClickListener);
        switchPrivate.setOnClickListener(onClickListener);
    }

    @OnClick({R.id.sound_iv,
            R.id.vibrate_iv})
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.sound_iv: {//消息提示音
                if (mSoundIv.isSelected()) {
                    mSoundIv.setSelected(false);
                    SharedPreferenceHelper.saveTipSound(getApplicationContext(), false);
                } else {
                    mSoundIv.setSelected(true);
                    SharedPreferenceHelper.saveTipSound(getApplicationContext(), true);
                }
                break;
            }

            case R.id.vibrate_iv: {//消息震动
                if (mVibrateIv.isSelected()) {
                    mVibrateIv.setSelected(false);
                    SharedPreferenceHelper.saveTipVibrate(getApplicationContext(), false);
                } else {
                    mVibrateIv.setSelected(true);
                    SharedPreferenceHelper.saveTipVibrate(getApplicationContext(), true);
                }
                break;
            }

        }
    }

}