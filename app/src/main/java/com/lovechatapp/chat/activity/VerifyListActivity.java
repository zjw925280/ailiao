package com.lovechatapp.chat.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.UserCenterBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 认证列表
 */
public class VerifyListActivity extends BaseActivity {

    @BindView(R.id.video_tv)
    TextView videoTv;

    @BindView(R.id.phone_tv)
    TextView phoneTv;

    @BindView(R.id.identity_tv)
    TextView identityTv;

    private UserCenterBean bean;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_verify_list);
    }

    @Override
    protected void onContentAdded() {
        setTitle("我的认证");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInfo();
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

                if (response != null && response.m_istatus == NetCode.SUCCESS && response.m_object != null) {
                    bean = response.m_object;

                    //手机认证
                    boolean isVerifyPhone = bean.phoneIdentity == 1;
                    phoneTv.setText(isVerifyPhone ? "已认证" : "未认证");
                    phoneTv.setClickable(bean.phoneIdentity == 0);
                    phoneTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                            bean.phoneIdentity != 1 ? R.drawable.icon_arrow_gray : 0, 0);

                    //身份认证
                    String[] identities = {"未认证", "已认证", "认证中"};
                    identityTv.setText(identities[bean.idcardIdentity]);
                    identityTv.setClickable(bean.idcardIdentity == 0);
                    identityTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                            bean.idcardIdentity != 1 ? R.drawable.icon_arrow_gray : 0, 0);

                    //视频认证
                    String[] videos = {"未认证", "已认证", "认证中"};
                    videoTv.setText(videos[bean.videoIdentity]);
                    videoTv.setClickable(bean.videoIdentity == 0);
                    videoTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                            bean.videoIdentity != 1 ? R.drawable.icon_arrow_gray : 0, 0);
                }
            }
        });
    }

    @OnClick({
            R.id.video_tv,
            R.id.phone_tv,
            R.id.identity_tv
    })
    public void onClick(View view) {

        if (bean == null) {
            ToastUtil.INSTANCE.showToast(mContext, "获取数据中");
            getInfo();
            return;
        }

        switch (view.getId()) {

            //视频认证
            case R.id.video_tv:
                if (bean.videoIdentity == 0) {
                    startActivity(new Intent(mContext, ApplyUploadVideoActivity.class));
                }
                break;

            //手机认证
            case R.id.phone_tv:
                if (bean.phoneIdentity == 0) {
                    startActivity(new Intent(mContext, PhoneVerifyActivity.class));
                }
                break;

            //身份认证
            case R.id.identity_tv:
                if (bean.idcardIdentity == 0) {
                    startActivity(new Intent(mContext, VerifyIdentityActivity.class));
                }
                break;

        }
    }
}