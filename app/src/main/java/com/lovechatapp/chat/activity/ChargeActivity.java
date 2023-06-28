package com.lovechatapp.chat.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.pay.paytypelibrary.OrderInfo;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.ChargeRecyclerAdapter;
import com.lovechatapp.chat.adapter.PayOptionRecyclerAdapter;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseListResponse;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.BalanceBean;
import com.lovechatapp.chat.bean.ChargeListBean;
import com.lovechatapp.chat.bean.PayOptionBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.CodeUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   充值页面
 * 作者：
 * 创建时间：2018/6/15
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ChargeActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.gold_number_tv)
    TextView mGoldNumberTv;
    //其余方式
    @BindView(R.id.pay_option_rv)
    RecyclerView mPayOptionRv;

    private ChargeRecyclerAdapter mAdapter;

    //接收关闭activity广播
    private MyFinishBroadcastReceiver mMyBroadcastReceiver;

    //支付方式
    private List<PayOptionBean> mPayOptionBeans = new ArrayList<>();
    private PayOptionRecyclerAdapter mOptionRecyclerAdapter;

    //选中的bean
    private PayOptionBean mSelectedBean;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_charge_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.charge_gold);
        setRightText(R.string.service);
        initStart();
        getMyGold();
        getChargeOption();
    }

    /**
     * 初始化开始
     */
    private void initStart() {

        mMyBroadcastReceiver = new MyFinishBroadcastReceiver();
        IntentFilter filter = new IntentFilter(Constant.FINISH_CHARGE_PAGE);
        registerReceiver(mMyBroadcastReceiver, filter);

        //其余充值方式
        mOptionRecyclerAdapter = new PayOptionRecyclerAdapter(this);
        mPayOptionRv.setAdapter(mOptionRecyclerAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mPayOptionRv.setLayoutManager(manager);
        mPayOptionRv.setNestedScrollingEnabled(false);
        mOptionRecyclerAdapter.setOnItemSelectListner(bean -> {
            if (bean != null) {
                mSelectedBean = bean;
                getChargeList(bean.payType);
            }
        });

        //充值具体项目
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new ChargeRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);
        mContentRv.setNestedScrollingEnabled(false);
    }

    /**
     * 获取支付配置
     */
    private void getChargeOption() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_PAY_DEPLOY_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<PayOptionBean>>() {
            @Override
            public void onResponse(BaseListResponse<PayOptionBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<PayOptionBean> beans = response.m_object;
                    if (beans != null && beans.size() > 0) {
                        //取出默认bean
                        for (PayOptionBean bean : beans) {
                            if (bean.isdefault == 1) {//默认
                                mSelectedBean = bean;
                                mSelectedBean.isSelected = true;
                                break;
                            }
                        }
                        mPayOptionBeans = beans;
                        if (mSelectedBean == null) {
                            mSelectedBean = mPayOptionBeans.get(0);
                            mSelectedBean.isSelected = true;
                        }
                        mOptionRecyclerAdapter.loadData(mPayOptionBeans);
                    }
                }
            }
        });

    }

    /**
     * 获取我的金币余额
     */
    private void getMyGold() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_USER_BALANCE())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<BalanceBean>>() {
            @Override
            public void onResponse(BaseResponse<BalanceBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    BalanceBean balanceBean = response.m_object;
                    if (balanceBean != null) {
                        mGoldNumberTv.setText(String.valueOf(balanceBean.amount));
                    }
                }
            }
        });
    }

    /**
     * 获取充值列表
     */
    private void getChargeList(int type) {
        mAdapter.clearData();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_end_type", String.valueOf(type));//充值类型:0.支付宝 1.微信
        OkHttpUtils.post().url(ChatApi.GET_RECHARGE_DISCOUNT())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<ChargeListBean>>() {
            @Override
            public void onResponse(BaseListResponse<ChargeListBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<ChargeListBean> beans = response.m_object;
                    if (beans != null) {
                        mAdapter.clearSelectedBean();
                        if (beans.size() > 1) {
                            beans.get(1).isSelected = true;
                        }
                        mAdapter.loadData(beans);
                    }
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                dismissLoadingDialog();
            }
        });
    }

    @OnClick({R.id.account_detail_tv, R.id.right_text, R.id.go_pay_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_detail_tv: {//账单详情
                Intent intent = new Intent(getApplicationContext(), AccountBalanceActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.right_text: {
                CodeUtil.jumpToQQ(ChargeActivity.this);
                break;
            }
            case R.id.go_pay_tv: {//去支付
                if (mSelectedBean == null) {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_choose_pay_way);
                    return;
                }
                ChargeListBean mSelectChargeBean = mAdapter.getSelectBean();
                if (mSelectChargeBean == null) {
                    ToastUtil.INSTANCE.showToast(mContext, R.string.please_choose_money);
                    return;
                }
                PayChooserActivity.start(mContext, mSelectChargeBean.t_id, mSelectedBean.payType, mSelectedBean.t_id, false);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (data == null) {
            return;
        }

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case 100:
                    OrderInfo orderInfo = (OrderInfo) data.getSerializableExtra("orderInfo");
                    if (orderInfo != null) {
                        if (!TextUtils.isEmpty(orderInfo.getTokenId())) {
                            startWxpay(mContext, orderInfo);
                        } else if (!TextUtils.isEmpty(orderInfo.getTradeNo())) {
                            startUnionpay(mContext, orderInfo.getTradeNo());
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(message);
                    builder.setInverseBackgroundForced(true);
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMyBroadcastReceiver != null) {
            unregisterReceiver(mMyBroadcastReceiver);
        }
    }

    class MyFinishBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

}
