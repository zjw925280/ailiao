package com.lovechatapp.chat.wxapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.ApplyVerifyHandActivity;
import com.lovechatapp.chat.activity.ChooseGenderActivity;
import com.lovechatapp.chat.activity.MainActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.ChatUserInfo;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.CodeUtil;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.SystemUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：微信页面
 * 作者：
 * 创建时间：2018/6/28
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //通过WXAPIFactory工厂获取IWXApI的示例
        api = WXAPIFactory.createWXAPI(getApplicationContext(), Constant.WE_CHAT_APPID, true);
        //将应用的app id注册到微信
        api.registerApp(Constant.WE_CHAT_APPID);
        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        api.handleIntent(getIntent(), this);
        LogUtil.i("wxonCreate: ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        api.handleIntent(data, this);
        LogUtil.i("wxonActivityResult: ");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        LogUtil.i("baseReq:" + JSON.toJSONString(baseReq));
    }

    @Override
    public void onResp(BaseResp baseResp) {
        LogUtil.i("baseResp:" + JSON.toJSONString(baseResp));
        LogUtil.i("baseResp:" + baseResp.errStr + "," + baseResp.openId + "," + baseResp.transaction + "," + baseResp.errCode);
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {//微信登录
                    SendAuth.Resp resp = (SendAuth.Resp) baseResp;
                    if (!TextUtils.isEmpty(resp.code)) {
                        //getAccessToken(resp.code);
                        getWXWayRealIp(resp.code);
                    } else {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                        finish();
                    }
                } else if (baseResp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {//微信分享
                    LogUtil.i("微信分享成功");
                    if (AppManager.getInstance().getIsMainPageShareQun()) {
                        //继续完成分享
                        addShareTime();
                    } else {
                        finish();
                    }
                }

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {//微信登录
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_cancel);
                    finish();
                } else if (baseResp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {//微信分享
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.share_cancel);
                    finish();
                }
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                finish();
                break;
            default:
                finish();
                break;
        }
    }

    /**
     * 获取Access_token
     */
    private void getAccessToken(String code) {
        OkHttpUtils.get().url(ChatApi.WX_GET_ACCESS_TOKEN())
                .addParams("appid", Constant.WE_CHAT_APPID)
                .addParams("secret", Constant.WE_CHAT_SECRET)
                .addParams("code", code)
                .addParams("grant_type", "authorization_code")
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e, int id) {
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                finish();
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    JSONObject jsonObject = JSON.parseObject(response);
                    String token = jsonObject.getString("access_token");
                    String openId = jsonObject.getString("openid");
                    if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(openId)) {
                        getUserInfo(token, openId);
                    } else {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                    finish();
                }
            }
        });
    }

    /**
     * 获取UserInfo
     */
    private void getUserInfo(String token, String openId) {
        OkHttpUtils.get().url(ChatApi.WX_GET_USER_INFO())
                .addParams("access_token", token)
                .addParams("openid", openId)//openid:授权用户唯一标识
                .build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e, int id) {
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                finish();
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    JSONObject object = JSON.parseObject(response);
                    if (object != null) {
                        if (!AppManager.getInstance().getIsWeChatBindAccount()) {//微信登录
                            getWXWayRealIp(object);
                        } else {//绑定微信号 提现
                            LogUtil.i("绑定微信号 提现");
                            //发送广播传递信息回去
                            String nickName = object.getString("nickname");
                            String handImg = object.getString("headimgurl");
                            String openId = object.getString("openid");
                            Intent intent = new Intent(Constant.WECHAT_WITHDRAW_ACCOUNT);
                            intent.putExtra(Constant.WECHAT_NICK_INFO, nickName);
                            intent.putExtra(Constant.WECHAT_HEAD_URL, handImg);
                            intent.putExtra(Constant.WECHAT_OPEN_ID, openId);
                            sendBroadcast(intent);
                            finish();
                        }
                    } else {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                        finish();
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                    finish();
                }
            }
        });
    }

    /**
     * 获取WX登录方式真实ip
     */
    private void getWXWayRealIp(final com.alibaba.fastjson.JSONObject object) {
        OkHttpUtils.get().url(ChatApi.GET_REAL_IP())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                loginWx(object, "0.0.0.0");
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.i("WX真实IP: " + response);
                String replace = response.replace("ipCallback({ip:\"", "");
                String    cip= replace.replace("\"})", "");
                if (!TextUtils.isEmpty(cip)) {
                    loginWx(object, cip);
                } else {
                    loginWx(object, "0.0.0.0");
                }

//                if (!TextUtils.isEmpty(response) && response.contains("{") && response.contains("}")) {
//                    try {
//                        int startIndex = response.indexOf("{");
//                        int endIndex = response.indexOf("}");
//                        String content = response.substring(startIndex, endIndex + 1);
//                        LogUtil.i("截取的: " + content);
//                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(content);
//                        String cip = jsonObject.getString("cip");
//                        if (!TextUtils.isEmpty(cip)) {
//                            loginWx(object, cip);
//                        } else {
//                            loginWx(object, "0.0.0.0");
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        loginWx(object, "0.0.0.0");
//                    }
//                } else {
//                    loginWx(object, "0.0.0.0");
//                }
            }
        });
    }

    /**
     * 调用自己的api 进行微信登录
     */
    private void loginWx(JSONObject jsonObject, String ip) {
        String openId = jsonObject.getString("openid");
        if (TextUtils.isEmpty(openId)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.we_chat_fail);
            finish();
            return;
        }
        final String nickName = jsonObject.getString("nickname");
        final String handImg = jsonObject.getString("headimgurl");
        String city = jsonObject.getString("city");
        //用于师徒
        String t_system_version = "Android " + SystemUtil.getSystemVersion();
        String deviceNumber = SystemUtil.getOnlyOneId(getApplicationContext());

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("openId", TextUtils.isEmpty(openId) ? "" : openId);
        paramMap.put("nickName", TextUtils.isEmpty(nickName) ? "" : nickName);
        paramMap.put("handImg", TextUtils.isEmpty(handImg) ? "" : handImg);
        paramMap.put("city", TextUtils.isEmpty(city) ? "" : city);
        paramMap.put("t_phone_type", "Android");
        paramMap.put("t_system_version", TextUtils.isEmpty(t_system_version) ? "" : t_system_version);
        paramMap.put("deviceNumber", deviceNumber);
        paramMap.put("ip", ip);
        String channelId = AppManager.getInstance().getShareId();
        if (TextUtils.isEmpty(channelId)) {
            channelId = CodeUtil.getClipBoardContent(getApplicationContext());
        }
        paramMap.put("shareUserId", channelId);
        OkHttpUtils.post().url(ChatApi.USER_WEIXIN_LOGIN())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
            @Override
            public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS && response.m_object != null) {
                        CodeUtil.clearClipBoard(getApplicationContext());
                        ChatUserInfo userInfo = response.m_object;
                        userInfo.t_nickName = nickName;
                        userInfo.headUrl = handImg;
                        AppManager.getInstance().setUserInfo(userInfo);
                        SharedPreferenceHelper.saveAccountInfo(getApplicationContext(), userInfo);
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_success);

                        Intent intent;
                        if (userInfo.t_sex == 2) {
                            intent = new Intent(getApplicationContext(), ChooseGenderActivity.class);
                            intent.putExtra(Constant.NICK_NAME, nickName);
                            intent.putExtra(Constant.MINE_HEAD_URL, handImg);
                        } else {
                            intent = new Intent(getApplicationContext(), MainActivity.class);
                        }
                        startActivity(intent);

                        //发送广播关闭Login页面
                        Intent broad = new Intent(Constant.FINISH_LOGIN_PAGE);
                        sendBroadcast(broad);

                    } else if (response.m_istatus == -1) {//被封号
                        String message = response.m_strMessage;
                        Intent intent = new Intent(Constant.BEEN_CLOSE_LOGIN_PAGE);
                        intent.putExtra(Constant.BEEN_CLOSE, message);
                        sendBroadcast(intent);
                    } else if (response.m_istatus == -200) {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.seven_days);
                    } else {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
            }

            @Override
            public void onAfter(int id) {
                finish();
            }
        });
    }

    /**
     * 分享微信群完成后 添加分享次数
     */
    private void addShareTime() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.ADD_SHARE_COUNT())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<Integer>>() {
            @Override
            public void onResponse(BaseResponse<Integer> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    Integer m_object = response.m_object;
                    showThreeQunDialog(m_object);
                } else {
                    finish();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                finish();
            }
        });
    }

    /**
     * 显示分享到3个群dialog
     */
    private void showThreeQunDialog(int count) {
        final Dialog mDialog = new Dialog(WXEntryActivity.this, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(WXEntryActivity.this).inflate(R.layout.dialog_share_qun_success_layout, null);
        setThreeQunDialogView(view, mDialog, count);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        }
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    private void setThreeQunDialogView(View view, final Dialog mDialog, int count) {
        //描述
        TextView des_tv = view.findViewById(R.id.des_tv);
        //按钮
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        final int leftTime = 3 - count;
        if (leftTime > 0) {//还剩几次,继续分享
            String content = getResources().getString(R.string.need_one) + leftTime + getResources().getString(R.string.need_two);
            des_tv.setText(content);
            confirm_tv.setText(getResources().getString(R.string.continue_share));
        } else {//分享完成  开始认证
            des_tv.setText(getResources().getString(R.string.back_app));
            confirm_tv.setText(getResources().getString(R.string.back_and_verify));
        }
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (leftTime > 0) {
                    finish();
                } else {
                    Intent broadcastIntent = new Intent(Constant.QUN_SHARE_QUN_CLOSE);
                    sendBroadcast(broadcastIntent);
                    Intent intent = new Intent(getApplicationContext(), ApplyVerifyHandActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /**
     * 获取UserId
     */
    private String getUserId() {
        String sUserId = "";
        if (AppManager.getInstance() != null) {
            ChatUserInfo userInfo = AppManager.getInstance().getUserInfo();
            if (userInfo != null) {
                int userId = userInfo.t_id;
                if (userId >= 0) {
                    sUserId = String.valueOf(userId);
                }
            } else {
                int id = SharedPreferenceHelper.getAccountInfo(getApplicationContext()).t_id;
                sUserId = String.valueOf(id);
            }
        }
        return sUserId;
    }

    //-------------------------------------修改----------------------

    /**
     * 获取WX登录方式真实ip
     */
    private void getWXWayRealIp(final String code) {
        OkHttpUtils.get().url(ChatApi.GET_REAL_IP())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                getCity(code,"0.0.0");
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.i("这是cityWX真实IP: " + response);
                String replace = response.replace("ipCallback({ip:\"", "");
                String cip= replace.replace("\"})", "");
                getCity(code,cip);
            }
        });
    }

    /**
     * 调用自己的api 进行微信登录
     */
    private void loginWx(String code, String ip,String city) {
        //用于师徒
        String t_system_version = "Android " + SystemUtil.getSystemVersion();
        String deviceNumber = SystemUtil.getOnlyOneId(getApplicationContext());

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("t_phone_type", "Android");
        paramMap.put("t_system_version", TextUtils.isEmpty(t_system_version) ? "" : t_system_version);
        paramMap.put("deviceNumber", deviceNumber);
        paramMap.put("ip", ip);
        paramMap.put("weixinCode", code);
        paramMap.put("city", city);
        String channelId = AppManager.getInstance().getShareId();
        if (TextUtils.isEmpty(channelId)) {
            channelId = CodeUtil.getClipBoardContent(getApplicationContext());
        }
        paramMap.put("shareUserId", channelId);
        Log.e("这是city",new Gson().toJson(paramMap));
        OkHttpUtils.post().url(ChatApi.USER_WEIXIN_LOGIN())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<ChatUserInfo>>() {
            @Override
            public void onResponse(BaseResponse<ChatUserInfo> response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        CodeUtil.clearClipBoard(getApplicationContext());
                        ChatUserInfo userInfo = response.m_object;
                        if (userInfo != null) {
                            AppManager.getInstance().setUserInfo(userInfo);
                            SharedPreferenceHelper.saveAccountInfo(getApplicationContext(), userInfo);
                            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_success);
                            if (userInfo.t_sex == 2) {
                                Intent intent = new Intent(getApplicationContext(), ChooseGenderActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            //发送广播关闭Login页面
                            Intent intent = new Intent(Constant.FINISH_LOGIN_PAGE);
                            sendBroadcast(intent);
                        } else {
                            if (!TextUtils.isEmpty(response.m_strMessage)) {
                                ToastUtil.INSTANCE.showToast(getApplicationContext(), response.m_strMessage);
                            } else {
                                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                            }
                        }
                        finish();
                    } else if (response.m_istatus == -1) {//被封号
                        String message = response.m_strMessage;
                        Intent intent = new Intent(Constant.BEEN_CLOSE_LOGIN_PAGE);
                        intent.putExtra(Constant.BEEN_CLOSE, message);
                        sendBroadcast(intent);
                        finish();
                    } else if (response.m_istatus == -200) {//7天内已经登陆过其他账号
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.seven_days);
                        finish();
                    } else {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                        finish();
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                    finish();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.login_fail);
                finish();
            }
        });
    }
    /**
     * 获取真实ip
     */
    private void getCity(String code,String cip) {
        OkHttpUtils.get().url(ChatApi.GET_CITY(cip))
                .build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        loginWx("","","");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("这是city城市json: " + response);
                        org.json.JSONObject jsonObject = null;
                        try {
                            jsonObject = new org.json.JSONObject(response);
                            String result = jsonObject.getString("result");
                            org.json.JSONObject jsonObject1 = new org.json.JSONObject(result);
                            String ad_info = jsonObject1.getString("ad_info");
                            org.json.JSONObject jsonObject2 = new org.json.JSONObject(ad_info);
                            String city = jsonObject2.getString("city");
                            LogUtil.i("这是city城市信息: " + city+" ip="+cip);
                            loginWx(code, cip,city);
                        } catch (JSONException e) {
                            loginWx("", "","");
                            throw new RuntimeException(e);

                        }

                    }
                });
    }
}
