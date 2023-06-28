package com.lovechatapp.chat.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseBean;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.OnClick;

/**
 * 申请主播
 */
public class VerifyOptionActivity extends BaseActivity {

    private Result result;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_verify_option);
    }

    @Override
    protected void onContentAdded() {
        setTitle("完成认证");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getState();
    }

    /**
     * 认证状态
     */
    private void getState() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.getcertifyStatus())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<Result>>() {
            @Override
            public void onResponse(BaseResponse<Result> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS && response.m_object != null) {
                    result = response.m_object;
                    TextView modifyBtn = findViewById(R.id.modify_btn);
                    setText(modifyBtn, response.m_object.userDataStatus);
                    TextView verifyBtn = findViewById(R.id.verify_btn);
                    setText(verifyBtn, response.m_object.certificationStatus);
                }
            }
        });
    }

    private void setText(TextView tv, int state) {
        String[] texts = {"去认证", "已完成", "审核中"};
        tv.setText(texts[state]);
        tv.setBackgroundResource(state == 0 ? R.drawable.corner_solid_main : R.drawable.corner_gray_f2f3f7);
        tv.setTextColor(state == 0 ? 0xffffffff : 0xff999999);
    }

    @OnClick({
            R.id.verify_btn,
            R.id.modify_btn
    })
    public void onClick(View v) {

        if (result == null) {
            getState();
            return;
        }

        switch (v.getId()) {

            //拍照认证
            case R.id.verify_btn: {
                if (result.certificationStatus == 0) {
                    startActivity(new Intent(mContext, ApplyVerifyHandActivity.class));
                } else if (result.certificationStatus == 2) {
                    startActivity(new Intent(mContext, ActorVerifyingActivity.class));
                }
                break;
            }

            //资料认证
            case R.id.modify_btn: {
                if (result.userDataStatus == 0) {
                    ModifyUserInfoActivity.verifyStart(mContext);
                }
                break;
            }

        }
    }

    /**
     * certificationStatus:0 未认证  1:已认证 2:认证中
     */
    static class Result extends BaseBean {
        public int userDataStatus;
        public int certificationStatus;
    }
}