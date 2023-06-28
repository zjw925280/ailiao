package com.lovechatapp.chat.net;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 加入&删除黑名单
 */
public class BlackRequester {

    public void post(int blackId, final boolean addToBlack) {

        Map<String, String> paramMap = new HashMap<>();

        String myId = AppManager.getInstance().getUserInfo().t_id + "";
        paramMap.put("userId", myId);

        if (addToBlack) {
            paramMap.put("cover_userId", blackId + "");
        } else {
            paramMap.put("t_id", blackId + "");
        }

        String url = addToBlack ? ChatApi.addBlackUser() : ChatApi.delBlackUser();

        OkHttpUtils.post().url(url)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    onSuccess(response, addToBlack);
                } else {
                    ToastUtil.INSTANCE.showToast(response.m_strMessage);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(AppManager.getInstance(), R.string.system_error);
            }
        });
    }

    public void onSuccess(BaseResponse response, boolean addToBlack) {
        ToastUtil.INSTANCE.showToast(AppManager.getInstance(), response.m_strMessage);
    }
}