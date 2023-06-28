package com.lovechatapp.chat.ext

import com.lovechatapp.chat.base.AppManager
import com.lovechatapp.chat.base.BaseResponse
import com.lovechatapp.chat.constant.ChatApi
import com.lovechatapp.chat.net.AjaxCallback
import com.lovechatapp.chat.net.NetCode
import com.lovechatapp.chat.util.ParamUtil
import com.zhy.http.okhttp.OkHttpUtils
import okhttp3.Call
import java.lang.Exception

fun operateDate(
    appointmentId: Int,
    status: Int,
    onSuccess: () -> Unit,
    onFail: (message: String) -> Unit
) {
    val paramMap = HashMap<String?, Any>()
    paramMap["appointmentId"] = appointmentId
    paramMap["appointmentStatus"] = status
    paramMap["userId"] = AppManager.getInstance().userInfo.t_id
    OkHttpUtils.post().url(ChatApi.operateDate())
        .addParams("param", ParamUtil.getParam(paramMap))
        .build()
        .execute(object : AjaxCallback<BaseResponse<String>?>() {
            override fun onResponse(response: BaseResponse<String>?, id: Int) {
                response?.apply {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        onSuccess.invoke()
                    } else {
                        onFail(m_strMessage)
                    }
                } ?: apply {
                    onFail("请求失败，请稍后重试")
                }
            }

            override fun onError(call: Call?, e: Exception?, id: Int) {
                onFail("请求失败，请稍后重试")
            }
        })
}