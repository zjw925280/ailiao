package com.lovechatapp.chat.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.pay.paytypelibrary.OrderInfo;
import com.pay.paytypelibrary.PayUtil;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseListResponse;
import com.lovechatapp.chat.bean.PayOptionBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.pay.PayResult;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.ViewHolder;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.qcloud.tim.uikit.utils.ThreadHelper;
import com.unionpay.UPPayAssistEx;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 支付Activity
 */
public class PayChooserActivity extends Activity {

    private List<PayOptionBean> beans;

    private PayOptionBean selectedBean;

    private Activity activity;

    private AbsRecycleAdapter absRecycleAdapter;

    private int chargeId;

    private boolean isVip;

    /**
     * vip选择支付方式
     */
    public static void start(@NonNull Activity context, int vipId) {
        start(context, vipId, true);
    }

    /**
     * 选择支付方式
     */
    public static void start(Activity context, int chargeId, boolean isVip) {
        start(context, chargeId, 0, 0, isVip);
    }

    /**
     * 直接支付方式
     */
    public static void start(Activity context, int chargeId, int payType, int payId, boolean isVip) {
        Intent starter = new Intent(context, PayChooserActivity.class);
        starter.putExtra("isVip", isVip);
        starter.putExtra("chargeId", chargeId);
        starter.putExtra("payType", payType);
        starter.putExtra("payId", payId);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.CENTER);

        activity = this;
        isVip = getIntent().getBooleanExtra("isVip", false);
        chargeId = getIntent().getIntExtra("chargeId", 0);

        int payType = getIntent().getIntExtra("payType", 0);
        int payId = getIntent().getIntExtra("payId", 0);

        //选择支付方式
        if (payId == 0) {

            setContentView(R.layout.pay_chooser_activity);
            getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;

            RecyclerView recyclerView = findViewById(R.id.rv);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            absRecycleAdapter = new AbsRecycleAdapter(new AbsRecycleAdapter.Type(R.layout.item_pay_option_layout, PayOptionBean.class)) {

                @Override
                public void convert(ViewHolder holder, Object t) {
                    PayOptionBean bean = (PayOptionBean) t;
                    ImageLoadHelper.glideShowImageWithUrl(activity, bean.payIcon, holder.<ImageView>getView(R.id.icon_iv));
                    holder.<TextView>getView(R.id.name_tv).setText(bean.payName);
                    holder.<ImageView>getView(R.id.check_iv).setSelected(bean.isSelected);
                }

                @Override
                public void setViewHolder(final ViewHolder viewHolder) {
                    viewHolder.itemView.setOnClickListener(v -> {
                        PayOptionBean bean = (PayOptionBean) getData().get(viewHolder.getRealPosition());
                        if (selectedBean != null && selectedBean != bean) {
                            selectedBean.isSelected = false;
                        }
                        bean.isSelected = true;
                        selectedBean = bean;
                        notifyDataSetChanged();
                    });
                }
            };
            recyclerView.setAdapter(absRecycleAdapter);
            findViewById(R.id.go_pay_tv).setOnClickListener(v -> {
                if (selectedBean == null) {
                    ToastUtil.INSTANCE.showToast("请选择支付方式");
                    return;
                }
                if (isVip) {
                    payForVip(activity, chargeId, selectedBean.payType, selectedBean.t_id);
                } else {
                    payForGold(activity, chargeId, selectedBean.payType, selectedBean.t_id);
                }
            });
            getChargeOption();
        } else {
            //直接调用支付
            if (isVip) {
                payForVip(activity, chargeId, payType, payId);
            } else {
                payForGold(activity, chargeId, payType, payId);
            }
        }
    }

    /**
     * 获取支付配置
     */
    private void getChargeOption() {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("请稍候...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        OkHttpUtils.post().url(ChatApi.GET_PAY_DEPLOY_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<PayOptionBean>>() {
            @Override
            public void onResponse(BaseListResponse<PayOptionBean> response, int id) {
                if (activity.isFinishing()) {
                    return;
                }
                progressDialog.dismiss();
                if (response != null
                        && response.m_istatus == NetCode.SUCCESS
                        && response.m_object != null
                        && response.m_object.size() > 0) {
                    for (PayOptionBean bean : response.m_object) {
                        if (bean.isdefault == 1) {
                            bean.isSelected = true;
                            selectedBean = bean;
                            break;
                        }
                    }
                    beans = response.m_object;
                    absRecycleAdapter.setDatas(beans);
                } else {
                    finish();
                    ToastUtil.INSTANCE.showToast("空数据");
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (activity.isFinishing()) {
                    return;
                }
                progressDialog.dismiss();
                finish();
                ToastUtil.INSTANCE.showToast("支付方式获取失败");
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (data == null) {
            return;
        }

        if (resultCode == RESULT_OK) {

            /*
             * 杉德回调
             */
            switch (requestCode) {

                case 100:
                    OrderInfo orderInfo = (OrderInfo) data.getSerializableExtra("orderInfo");
                    if (orderInfo != null) {
                        if (!TextUtils.isEmpty(orderInfo.getTokenId())) {
                            startWxpay(activity, orderInfo);
                        } else if (!TextUtils.isEmpty(orderInfo.getTradeNo())) {
                            startUnionpay(activity, orderInfo.getTradeNo());
                        }
                    }
                    break;

                case 10:
                    Bundle bundle = data.getExtras();
                    if (null == bundle) {
                        return;
                    }

                    String message = "支付异常";
                    /*
                     * 支付控件返回字符串:
                     * success、fail、cancel 分别代表支付成功，支付失败，支付取消
                     */
                    String result = bundle.getString("pay_result");
                    if (result != null) {
                        if (result.equalsIgnoreCase("success")) {
                            message = "支付成功";
                        } else if (result.equalsIgnoreCase("fail")) {
                            message = "支付失败";
                        } else if (result.equalsIgnoreCase("cancel")) {
                            message = "用户取消支付";
                        }
                    }
                    ToastUtil.INSTANCE.showToast(message);
                    finish();
                    break;
            }
        }
    }

    /**
     * 微信小程序
     */
    public static void startWxpay(Context context, OrderInfo orderInfo) {
        String appId = orderInfo.getWxAppId(); // 填应用AppId
        IWXAPI api = WXAPIFactory.createWXAPI(context, appId);
        api.registerApp(appId);
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = orderInfo.getGhOriId(); // 填小程序原始id
        req.path = orderInfo.getPathUrl() + "token_id=" + orderInfo.getTokenId();  //拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
        req.miniprogramType = Integer.parseInt(orderInfo.getMiniProgramType());// 可选打开 开发版，体验版和正式版
        api.sendReq(req);
    }

    /**
     * 银联云闪付
     */
    public static void startUnionpay(Context context, String tradeNo) {
        UPPayAssistEx.startPay(context, null, null, tradeNo, "00");
    }

    /**
     * 约豆充值
     */
    public void payForGold(final Activity activity, int goldId, final int payType, int payDeployId) {
        payFor(activity, ChatApi.GOLD_STORE_VALUE(), goldId, payType, payDeployId);
    }

    /**
     * 购买vip
     */
    public void payForVip(final Activity activity, int vipId, final int payType, int payDeployId) {
        payFor(activity, ChatApi.VIP_STORE_VALUE(), vipId, payType, payDeployId);
    }

    /**
     * 充值约豆/vip
     */
    private void payFor(final Activity activity, String method, int vipId, final int payType, int payDeployId) {

        final ProgressDialog progressDialog = showLoading(activity);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("setMealId", String.valueOf(vipId));
        paramMap.put("payType", String.valueOf(payType));
        paramMap.put("payDeployId", String.valueOf(payDeployId));
        OkHttpUtils.post().url(method)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (activity.isFinishing())
                    return;
                progressDialog.dismiss();
                ToastUtil.INSTANCE.showToast(R.string.system_error);
            }

            @Override
            public void onResponse(String response, int id) {
                if (activity.isFinishing())
                    return;
                progressDialog.dismiss();
                startPay(activity, payType, response);
            }
        });
    }

    private static ProgressDialog showLoading(Activity activity) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("请稍候...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    /**
     * 解析支付方式
     */
    private void startPay(Activity context, int payType, String response) {
        if (!TextUtils.isEmpty(response)) {

            JSONObject object = JSON.parseObject(response);

            if (object.containsKey("m_istatus") && object.getIntValue("m_istatus") == 1) {

                /*
                 * 微信
                 */
                if (payType == -2) {
                    JSONObject payObject = object.getJSONObject("m_object");
                    payWithWeChat(context, payObject);
                }

                /*
                 * 支付宝
                 */
                else if (payType == -1) {
                    String orderInfo = object.getString("m_object");
                    if (!TextUtils.isEmpty(orderInfo)) {
                        payWithAliPay(context, orderInfo);
                    } else {
                        ToastUtil.INSTANCE.showToast(R.string.pay_vip_fail);
                    }
                }

                /*
                 * 网页支付
                 */
                else if (payType == -3) {
                    String payObject = object.getJSONObject("m_object").getString("return_msg");
                    if (!TextUtils.isEmpty(payObject)) {
                        Intent intent = new Intent(context, PayInnerWebViewActivity.class);
                        intent.putExtra(Constant.TITLE, context.getString(R.string.pay));
                        intent.putExtra(Constant.URL, payObject);
                        context.startActivity(intent);
                        finish();
                    } else {
                        ToastUtil.INSTANCE.showToast(R.string.pay_vip_fail);
                    }
                }

                /*
                 * 支付派微信小程序
                 */
                else if (payType == -5) {
                    payPai(
                            object.getJSONObject("m_object").getString("userName"),
                            object.getJSONObject("m_object").getString("path"));
                }

                /*
                 * 支付派支付宝
                 */
                else if (payType == -6) {
                    aliPay(
                            context,
                            object.getJSONObject("m_object").getString("path"));
                }

                /*
                 * 杉德支付宝
                 */
                else if (payType == -7) {
                    String payObject = object.getString("m_object");
                    PayUtil.CashierPay(context, payObject);
                }

                /*
                 * 杉德微信
                 */
                else if (payType == -8) {
                    String payObject = object.getString("m_object");
                    PayUtil.CashierPay(context, payObject);
                }

                /*
                 * 杉德银联
                 */
                else if (payType == -9) {
                    String payObject = object.getString("m_object");
                    PayUtil.CashierPay(context, payObject);
                }
            }

            /*
             * 提示错误
             */
            else if (object.containsKey("m_strMessage")
                    && !TextUtils.isEmpty(object.getString("m_strMessage"))) {
                ToastUtil.INSTANCE.showToast(object.getString("m_strMessage"));
            }
        }
    }

    /**
     * 支付宝支付
     */
    private void payWithAliPay(final Activity activity, final String orderInfo) {
        Runnable payRunnable = () -> {
            PayTask aliPay = new PayTask(activity);
            final Map<String, String> result = aliPay.payV2(orderInfo, true);
            runOnUiThread(() -> {
                if (activity.isFinishing())
                    return;
                PayResult payResult = new PayResult(result);
                //对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                String resultStatus = payResult.getResultStatus();
                // 判断resultStatus 为9000则代表支付成功
                if (TextUtils.equals(resultStatus, "9000")) {
                    // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                    ToastUtil.INSTANCE.showToast(R.string.pay_vip_success);
                } else {
                    // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                    ToastUtil.INSTANCE.showToast(R.string.pay_vip_fail);
                }
                finish();
            });
        };
        // 必须异步调用
        ThreadHelper.INST.execute(payRunnable);
    }

    /**
     * 微信支付
     */
    private void payWithWeChat(Context context, JSONObject payObject) {
        IWXAPI mWxApi = WXAPIFactory.createWXAPI(context, Constant.WE_CHAT_APPID, true);
        mWxApi.registerApp(Constant.WE_CHAT_APPID);
        if (mWxApi.isWXAppInstalled()) {
            try {
                PayReq request = new PayReq();
                request.appId = payObject.getString("appid");
                request.partnerId = payObject.getString("partnerid");
                request.prepayId = payObject.getString("prepayid");
                request.packageValue = "Sign=WXPay";
                request.nonceStr = payObject.getString("noncestr");
                request.timeStamp = payObject.getString("timestamp");
                request.sign = payObject.getString("sign");
                boolean res = mWxApi.sendReq(request);
                if (res) {
                    //设置是充值Vip
                    AppManager.getInstance().setIsWeChatForVip(false);
                }
                LogUtil.i("res : " + res);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.INSTANCE.showToast(R.string.pay_vip_fail);
            }
        } else {
            ToastUtil.INSTANCE.showToast(R.string.not_install_we_chat);
        }
        finish();
    }

    /**
     * 支付派微信小程序支付
     */
    public void payPai(String userName, String path) {
        IWXAPI api = WXAPIFactory.createWXAPI(AppManager.getInstance(), Constant.WE_CHAT_APPID);
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = userName;
        req.path = path;
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;
        api.sendReq(req);
        finish();
    }

    /**
     * 支付派支付宝h5支付
     */
    public void aliPay(Activity activity, String url) {
        PayWebViewActivity.start(activity, url);
        finish();
    }
}