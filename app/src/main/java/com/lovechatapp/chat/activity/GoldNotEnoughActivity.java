package com.lovechatapp.chat.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.GoldItemRecyclerAdapter;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseListResponse;
import com.lovechatapp.chat.bean.ChargeListBean;
import com.lovechatapp.chat.bean.PayOptionBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述   金币不足页面
 * 作者：
 * 创建时间：2018/8/15
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class GoldNotEnoughActivity extends BaseActivity {

    //默认
    @BindView(R.id.default_rl)
    RelativeLayout mDefaultRl;
    //默认
    @BindView(R.id.default_iv)
    ImageView mDefaultIv;
    //默认名称
    @BindView(R.id.default_name_tv)
    TextView mDefaultNameTv;
    //默认选中
    @BindView(R.id.default_check_iv)
    ImageView mDefaultCheckIv;

    //支付
    private IWXAPI mWxApi;
    //支付宝
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private GoldItemRecyclerAdapter mVipMoneyRecyclerAdapter;

    //默认支付方式bean
    private PayOptionBean mDefaultBean;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_gold_not_anouth_layout);
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        mWxApi = WXAPIFactory.createWXAPI(mContext, Constant.WE_CHAT_APPID, true);
        mWxApi.registerApp(Constant.WE_CHAT_APPID);
        needHeader(false);
        initStart();
        getChargeOption();
    }

    /**
     * 初始化
     */
    private void initStart() {
        //设置RecyclerView
        RecyclerView content_rv = findViewById(R.id.content_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        content_rv.setLayoutManager(gridLayoutManager);
        mVipMoneyRecyclerAdapter = new GoldItemRecyclerAdapter(mContext);
        content_rv.setAdapter(mVipMoneyRecyclerAdapter);
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
                                mDefaultBean = bean;
                                beans.remove(bean);
                                break;
                            }
                        }
                        //如果为空,那么取第一条
                        if (mDefaultBean == null) {
                            mDefaultBean = beans.get(0);
                        }
                        //如果默认不为空
                        if (mDefaultBean != null) {
                            //图标
                            ImageLoadHelper.glideShowImageWithUrl(GoldNotEnoughActivity.this,
                                    mDefaultBean.payIcon, mDefaultIv);
                            //名称
                            mDefaultNameTv.setText(mDefaultBean.payName);
                            mDefaultRl.setVisibility(View.VISIBLE);
                            mDefaultCheckIv.setSelected(true);
                            //获取列表
                            getChargeList(mDefaultBean.payType);
                        }
                    }
                }
            }
        });

    }

    /**
     * 获取充值列表
     */
    private void getChargeList(int type) {
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
                    if (beans != null && beans.size() > 0) {
                        dealBean(beans);
                    }
                }
            }
        });
    }

    /**
     * 处理获取到的bean
     */
    private void dealBean(List<ChargeListBean> beans) {
        //取第二 和 第四
        List<ChargeListBean> newBeans = new ArrayList<>();
        if (beans.size() > 4) {
            ChargeListBean bean = beans.get(1);
            bean.isSelected = true;
            newBeans.add(bean);
            newBeans.add(beans.get(3));
        }
        mVipMoneyRecyclerAdapter.loadData(newBeans);
    }

    @OnClick({R.id.charge_tv, R.id.top_v, R.id.upgrade_tv, R.id.more_tv, R.id.get_gold_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.charge_tv: {
                if (mDefaultBean == null) {
                    ToastUtil.INSTANCE.showToast(mContext, R.string.please_choose_pay_way_one);
                    return;
                }
                ChargeListBean mSelectVipBean = mVipMoneyRecyclerAdapter.getSelectBean();
                if (mSelectVipBean == null) {
                    ToastUtil.INSTANCE.showToast(mContext, R.string.please_choose_money);
                    return;
                }
                PayChooserActivity.start(mContext, mSelectVipBean.t_id, mDefaultBean.payType, mDefaultBean.t_id, false);
                break;
            }
            case R.id.top_v: {
                finish();
                break;
            }
            case R.id.upgrade_tv: {//立即升级vip
                Intent intent = new Intent(getApplicationContext(), VipCenterActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.more_tv: {//更多
                Intent intent = new Intent(getApplicationContext(), ChargeActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.get_gold_tv: {//邀请赚钱
                Intent intent = new Intent(getApplicationContext(), InviteEarnActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}