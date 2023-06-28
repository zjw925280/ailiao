package com.lovechatapp.chat.activity;

import android.app.Dialog;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.BankListRecyclerAdapter;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.BankBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.layoutmanager.PickerLayoutManager;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.LogUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.util.VerifyUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述    申请CPS页面
 * 作者：
 * 创建时间：2018/9/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ApplyCPSActivity extends BaseActivity {

    @BindView(R.id.withdraw_way_tv)
    TextView mWithdrawWayTv;
    @BindView(R.id.channel_name_et)
    EditText mChannelNameEt;
    @BindView(R.id.channel_web_et)
    EditText mChannelWebEt;
    @BindView(R.id.active_number_et)
    EditText mActiveNumberEt;
    @BindView(R.id.percent_tv)
    TextView mPercentTv;
    @BindView(R.id.real_name_et)
    EditText mRealNameEt;
    @BindView(R.id.account_et)
    EditText mAccountEt;
    @BindView(R.id.contact_et)
    EditText mContactEt;

    private List<BankBean> mBankList = new ArrayList<>();
    private BankBean mOptionSelectBean;
    private BankBean mFinalSelectBean;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_apply_cps_layout);
    }

    @Override
    protected void onContentAdded() {
        setTitle(R.string.apply_cps);
        getTakeOutMode();
    }

    @OnClick({R.id.withdraw_way_tv, R.id.add_tv, R.id.sub_tv, R.id.apply_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.withdraw_way_tv: {//提现方式
                showOptionDialog();
                break;
            }
            case R.id.add_tv: {//加
                String percent = mPercentTv.getText().toString().trim();
                int number = Integer.parseInt(percent);
                if (number < 25) {
                    number++;
                    mPercentTv.setText(String.valueOf(number));
                }
                break;
            }
            case R.id.sub_tv: {
                String percent = mPercentTv.getText().toString().trim();
                int number = Integer.parseInt(percent);
                if (number > 10) {
                    number--;
                    mPercentTv.setText(String.valueOf(number));
                }
                break;
            }
            case R.id.apply_tv: {//我要申请
                apply();
                break;
            }
        }
    }

    /**
     * 申请
     */
    private void apply() {
        //渠道名
        String channelName = mChannelNameEt.getText().toString().trim();
        if (TextUtils.isEmpty(channelName)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_channel_name);
            return;
        }
        //网址
        String channelWeb = mChannelWebEt.getText().toString().trim();
        if (TextUtils.isEmpty(channelWeb)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_web_site);
            return;
        }
        //活跃用户
        String activeNumber = mActiveNumberEt.getText().toString().trim();
        if (TextUtils.isEmpty(activeNumber)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_active_user);
            return;
        }
        //真实姓名
        String realName = mRealNameEt.getText().toString().trim();
        if (TextUtils.isEmpty(realName)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_real_name_des);
            return;
        }
        //提现方式
        if (mFinalSelectBean == null) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_choose_withdraw_way);
            return;
        }
        //提现账号
        String account = mAccountEt.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_withdraw_account);
            return;
        }
        //联系方式
        String contact = mContactEt.getText().toString().trim();
        if (TextUtils.isEmpty(contact)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.please_input_contact_way);
            return;
        }
        if (!VerifyUtils.isPhoneNum(contact)) {
            ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.wrong_phone_number);
            return;
        }
        //分成比例
        String percent = mPercentTv.getText().toString().trim();

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("cpsName", channelName);
        paramMap.put("cpsUrl", channelWeb);
        paramMap.put("active", activeNumber);
        paramMap.put("proportions", percent);
        paramMap.put("realName", realName);
        paramMap.put("takeOutId", mFinalSelectBean.backKey);
        paramMap.put("accountNumber", account);
        paramMap.put("phone", contact);
        OkHttpUtils.post().url(ChatApi.ADD_CPS_MS())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.apply_success);
                    finish();
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.apply_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
            }
        });

    }


    /**
     * 获取提现方式列表
     */
    private void getTakeOutMode() {
        OkHttpUtils.post().url(ChatApi.GET_TAKE_OUT_MODE())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    JSONObject jsonObject = JSON.parseObject(response);
                    int m_istatus = jsonObject.getInteger("m_istatus");
                    if (m_istatus == NetCode.SUCCESS) {
                        JSONObject bankObject = jsonObject.getJSONObject("m_object");
                        if (bankObject != null) {
                            Set<String> keys = bankObject.keySet();
                            if (keys.size() > 0) {
                                for (String key : keys) {
                                    BankBean bean = new BankBean();
                                    bean.backKey = key;
                                    bean.bankName = bankObject.getString(key);
                                    mBankList.add(bean);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 提现方式
     */
    private void showOptionDialog() {
        final Dialog mDialog = new Dialog(this, R.style.DialogStyle_Dark_Background);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_set_charge_layout, null);
        setDialogView(view, mDialog);
        mDialog.setContentView(view);
        Point outSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
        }
        mDialog.setCanceledOnTouchOutside(false);
        if (!isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置 dialog view
     */
    private void setDialogView(View view, final Dialog mDialog) {
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        TextView title_tv = view.findViewById(R.id.title_tv);
        title_tv.setText(getResources().getString(R.string.withdraw_way_one));

        BankListRecyclerAdapter adapter = new BankListRecyclerAdapter(this);
        RecyclerView content_rv = view.findViewById(R.id.content_rv);
        PickerLayoutManager pickerLayoutManager = new PickerLayoutManager(getApplicationContext(),
                content_rv, PickerLayoutManager.VERTICAL, false, 5, 0.3f, true);
        content_rv.setLayoutManager(pickerLayoutManager);
        content_rv.setAdapter(adapter);
        if (mBankList != null && mBankList.size() > 0) {
            adapter.loadData(mBankList);
        }
        pickerLayoutManager.setOnSelectedViewListener(new PickerLayoutManager.OnSelectedViewListener() {
            @Override
            public void onSelectedView(View view, int position) {
                LogUtil.i("位置: " + position);
                mOptionSelectBean = mBankList.get(position);
            }
        });
        //确定
        TextView confirm_tv = view.findViewById(R.id.confirm_tv);
        confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBankList != null && mBankList.size() > 0) {
                    if (mOptionSelectBean == null) {
                        mOptionSelectBean = mBankList.get(0);
                    }
                    mFinalSelectBean = mOptionSelectBean;
                    mWithdrawWayTv.setText(mOptionSelectBean.bankName);
                    mOptionSelectBean = null;
                }
                mDialog.dismiss();
            }
        });
    }

}
