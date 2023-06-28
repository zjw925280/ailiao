package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.MultipleVideoActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.MultipleChatInfo;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.AudioVideoRequester;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 创建多人房间
 */
public class CreateMultipleRoomDialog extends Dialog implements View.OnClickListener {

    private Activity activity;

    private int mansionId;

    public CreateMultipleRoomDialog(@NonNull Activity context, int mansionId) {
        super(context);
        activity = context;
        this.mansionId = mansionId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_create_multiple_room);
        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.setAttributes(lp);

        setCanceledOnTouchOutside(true);
        setCancelable(true);

        findViewById(R.id.confirm_btn).setOnClickListener(this);
        findViewById(R.id.cancel_btn).setOnClickListener(this);

        EditText editText = findViewById(R.id.name_tv);
        editText.setText(getDefaultName());

    }

    private String getDefaultName() {
        return String.format("%s的欢乐房间", AppManager.getInstance().getUserInfo().t_nickName);
    }

    private boolean isVideo() {
        RadioButton radioButton = findViewById(R.id.video_rb);
        return radioButton.isChecked();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.confirm_btn) {
            EditText editText = findViewById(R.id.name_tv);
            String roomName = editText.getText().toString().trim();
            if (TextUtils.isEmpty(roomName)) {
                roomName = getDefaultName();
            }
            createRoom(roomName);
        } else {
            dismiss();
        }
    }

    @Override
    public void dismiss() {
        if (getWindow() != null && getWindow().getCurrentFocus() != null) {
            ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        super.dismiss();
    }

    /**
     * 府邸用户创建房间
     * t_mansion_house_id 府邸ID
     * roomName 房间名称
     * chatType 1:视频 2:语音
     */
    private void createRoom(String roomName) {

        final TextView confirmTv = findViewById(R.id.confirm_btn);
        final String oldText = confirmTv.getText().toString();
        final MultipleChatInfo multipleChatInfo = new MultipleChatInfo();

        multipleChatInfo.mansionId = mansionId;
        multipleChatInfo.chatType = isVideo() ? Constant.CHAT_VIDEO : Constant.CHAT_AUDIO;
        multipleChatInfo.roomName = roomName;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("roomName", roomName);
        paramMap.put("t_mansion_house_id", mansionId);
        paramMap.put("chatType", multipleChatInfo.chatType);
        OkHttpUtils.post().url(ChatApi.addMansionHouseRoom())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<Integer>>() {

            @Override
            public void onResponse(BaseResponse<Integer> response, int id) {
                if (activity == null || activity.isFinishing() || response == null) {
                    return;
                }
                if (response.m_istatus == 1) {
                    dismiss();
                    multipleChatInfo.mansionRoomId = response.m_object;
                    AudioVideoRequester.getAgoraSign(multipleChatInfo.mansionRoomId, new OnCommonListener<String>() {
                        @Override
                        public void execute(String s) {
                            if (TextUtils.isEmpty(s)) {
                                ToastUtil.INSTANCE.showToast("创建失败，请重试");
                            } else {
                                if (activity == null || activity.isFinishing()) {
                                    return;
                                }
                                multipleChatInfo.sign = s;
                                MultipleVideoActivity.start(activity, multipleChatInfo, true);
                            }
                        }
                    });
                } else {
                    ToastUtil.INSTANCE.showToast(response.m_strMessage);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.INSTANCE.showToast("创建失败");
            }

            @Override
            public void onBefore(Request request, int id) {
                if (isShowing()) {
                    confirmTv.setClickable(false);
                    confirmTv.setText("请稍候");
                }
            }

            @Override
            public void onAfter(int id) {
                if (isShowing()) {
                    confirmTv.setClickable(true);
                    confirmTv.setText(oldText);
                }
            }

        });
    }
}