package com.lovechatapp.chat.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.WithDrawRecyclerAdapter;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseListResponse;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.AccountBean;
import com.lovechatapp.chat.bean.ChargeListBean;
import com.lovechatapp.chat.bean.WithDrawBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：提现页面
 * 作者：
 * 创建时间：2018/6/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class WithDrawActivity extends BaseActivity {

    @BindView(R.id.gold_tv)
    TextView mGoldTv;
    @BindView(R.id.alipay_iv)
    ImageView mAlipayIv;
    @BindView(R.id.alipay_v)
    View mAlipayV;
    @BindView(R.id.we_chat_iv)
    ImageView mWeChatIv;
    @BindView(R.id.we_chat_v)
    View mWeChatV;
    @BindView(R.id.content_rv)
    RecyclerView mContentRv;
    @BindView(R.id.need_gold_tv)
    TextView mNeedGoldTv;
    @BindView(R.id.no_account_tv)
    TextView mNoAccountTv;
    @BindView(R.id.nick_name_tv)
    TextView mNickNameTv;
    @BindView(R.id.real_name_tv)
    TextView mRealNameTv;

    private WithDrawRecyclerAdapter mAdapter;
    //-2:微信支付 -1:支付宝
    private final int ALIPAY = -1;
    private final int WECHAT = -2;
    private int mMyGold;
    private int mSelectAccountType = 0;//选中的提现方式,默认0 支付宝
    private String mWeChatNickName = "";//微信前面的昵称
    private String mWeChatRealName = "";//真实姓名
    private String mAlipayAccountNumber = "";//支付宝账号
    private String mAlipayRealName = "";//支付宝真实姓名
    private AccountBean mWeChatData;
    private AccountBean mAlipayData;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_with_draw_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.withdraw);
        setView();
        setSelectOption(ALIPAY);
        getWithDraw(ALIPAY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyGold();
    }

    /**
     * 获取用户可提现金币
     */
    private void getMyGold() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_USEABLE_GOLD())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<WithDrawBean<AccountBean>>>() {
            @Override
            public void onResponse(BaseResponse<WithDrawBean<AccountBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    WithDrawBean<AccountBean> balanceBean = response.m_object;
                    if (balanceBean != null) {
                        //可提现金币
                        mMyGold = balanceBean.totalMoney;
                        if (mMyGold >= 0) {
                            mGoldTv.setText(String.valueOf(mMyGold));
                        }

                        //设置账号
                        boolean haveAlipay = false;
                        boolean haveWeChat = false;
                        List<AccountBean> accountBeans = balanceBean.data;
                        if (accountBeans != null && accountBeans.size() > 0) {
                            for (AccountBean bean : accountBeans) {
                                if (bean.t_type == 0) {//0.支付宝 1.微信
                                    mAlipayData = bean;
                                    mAlipayAccountNumber = bean.t_account_number;
                                    mAlipayRealName = bean.t_real_name;
                                    haveAlipay = true;
                                } else {//微信 昵称+真实姓名
                                    mWeChatData = bean;
                                    mWeChatNickName = bean.t_account_number;
                                    mWeChatRealName = bean.t_real_name;
                                    haveWeChat = true;
                                }
                            }
                            if (haveAlipay) {
                                mAlipayIv.setSelected(false);
                                mAlipayV.setSelected(false);
                                setSelectOption(ALIPAY);
                            } else if (haveWeChat) {
                                mWeChatIv.setSelected(false);
                                mWeChatV.setSelected(false);
                                setSelectOption(WECHAT);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 获取提现比例
     */
    private void getWithDraw(int type) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("t_end_type", String.valueOf(type));//端类型:0.Android 1.iPhone
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.GET_PUT_FORWARD_DISCOUNT())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<ChargeListBean>>() {
            @Override
            public void onResponse(BaseListResponse<ChargeListBean> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<ChargeListBean> listBean = response.m_object;
                    if (listBean != null) {
                        if (listBean.size() > 1) {
                            listBean.get(1).isSelected = true;
                        }
                        mAdapter.loadData(listBean);
                    }
                }
            }
        });
    }

    /**
     * 设置GridView
     */
    private void setView() {
        //右边
        setRightText(R.string.with_draw_money);
        mRightTv.setTextColor(getResources().getColor(R.color.black_3f3b48));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new WithDrawRecyclerAdapter(this);
        mContentRv.setAdapter(mAdapter);
        mAdapter.setOnItemSelectListener(new WithDrawRecyclerAdapter.OnItemSelectListener() {
            @Override
            public void onItemSelect(ChargeListBean chargeListBean) {
                if (chargeListBean != null && chargeListBean.isSelected) {
                    mNeedGoldTv.setText(String.valueOf(chargeListBean.t_gold));
                }
            }
        });
    }

    @OnClick({R.id.alipay_ll, R.id.we_chat_ll, R.id.with_draw_tv, R.id.bind_tv, R.id.right_text})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alipay_ll: {//支付宝
                setSelectOption(ALIPAY);
                break;
            }
            case R.id.we_chat_ll: {//微信
                setSelectOption(WECHAT);
                break;
            }
            case R.id.with_draw_tv: {//申请提现
                ChargeListBean mSelectedBean = mAdapter.getSelectBean();
                if (mSelectedBean == null) {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.with_draw_not_select);
                    return;
                }
                if (mSelectedBean.t_gold > mMyGold) {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.gold_not_enough);
                    return;
                }
                if (mSelectAccountType != ALIPAY && mSelectAccountType != WECHAT) {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_select_with_draw_way);
                    return;
                }
                //判断是否选择提现方式
                if (mSelectAccountType == ALIPAY) {
                    if (mAlipayData == null) {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_choose_alipay_account);
                        return;
                    }
                }
                if (mSelectAccountType == WECHAT) {
                    if (mWeChatData == null) {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_choose_wechat_account);
                        return;
                    }
                }
                applyWithDraw(mSelectedBean.t_id);
                break;
            }
            case R.id.bind_tv: {
                if (mSelectAccountType == ALIPAY) {
                    Intent intent = new Intent(getApplicationContext(), AlipayAccountActivity.class);
                    startActivity(intent);
                } else {//微信
                    Intent intent = new Intent(getApplicationContext(), WeChatAccountActivity.class);
                    startActivity(intent);
                }
                break;
            }
            case R.id.right_text: {//我的订单
                Intent intent = new Intent(getApplicationContext(), WithDrawDetailActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    /**
     * 申请提现
     */
    private void applyWithDraw(int selectId) {
        String forwardId;
        if (mSelectAccountType == ALIPAY) {
            forwardId = String.valueOf(mAlipayData.t_id);
        } else {
            forwardId = String.valueOf(mWeChatData.t_id);
        }

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("putForwardId", forwardId);
        paramMap.put("dataId", String.valueOf(selectId));
        OkHttpUtils.post().url(ChatApi.CONFIRM_PUT_FORWARD())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.apply_withdraw_success);
                    finish();
                } else {
                    if (response != null && !TextUtils.isEmpty(response.m_strMessage)) {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), response.m_strMessage);
                    } else {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.withdraw_fail);
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

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 设置选中
     */
    private void setSelectOption(int position) {
        switch (position) {
            case ALIPAY: {
                if (mAlipayV.isSelected()) {
                    return;
                }
                mAlipayIv.setSelected(true);
                mAlipayV.setSelected(true);
                mAlipayV.setVisibility(View.VISIBLE);
                mWeChatIv.setSelected(false);
                mWeChatV.setSelected(false);
                mWeChatV.setVisibility(View.INVISIBLE);
                mSelectAccountType = ALIPAY;

                if (!TextUtils.isEmpty(mAlipayAccountNumber) || !TextUtils.isEmpty(mAlipayRealName)) {
                    mNoAccountTv.setVisibility(View.GONE);
                } else {
                    mNoAccountTv.setVisibility(View.VISIBLE);
                }
                if (!TextUtils.isEmpty(mAlipayAccountNumber)) {
                    mNickNameTv.setText(mAlipayAccountNumber);
                } else {
                    mNickNameTv.setText(null);
                }
                if (!TextUtils.isEmpty(mAlipayRealName)) {
                    mRealNameTv.setText(mAlipayRealName);
                } else {
                    mRealNameTv.setText(null);
                }

                getWithDraw(ALIPAY);
                break;
            }
            case WECHAT: {
                if (mWeChatV.isSelected()) {
                    return;
                }
                mWeChatIv.setSelected(true);
                mWeChatV.setSelected(true);
                mWeChatV.setVisibility(View.VISIBLE);
                mAlipayIv.setSelected(false);
                mAlipayV.setSelected(false);
                mAlipayV.setVisibility(View.INVISIBLE);
                mSelectAccountType = WECHAT;

                if (!TextUtils.isEmpty(mWeChatNickName) || !TextUtils.isEmpty(mWeChatRealName)) {
                    mNoAccountTv.setVisibility(View.GONE);
                } else {
                    mNoAccountTv.setVisibility(View.VISIBLE);
                }
                if (!TextUtils.isEmpty(mWeChatNickName)) {
                    mNickNameTv.setText(mWeChatNickName);
                } else {
                    mNickNameTv.setText(null);
                }
                if (!TextUtils.isEmpty(mWeChatRealName)) {
                    mRealNameTv.setText(mWeChatRealName);
                } else {
                    mRealNameTv.setText(null);
                }
                getWithDraw(WECHAT);
                break;
            }
        }
    }

}
