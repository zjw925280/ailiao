package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.PayChooserActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseBean;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.ChargeListBean;
import com.lovechatapp.chat.bean.PayOptionBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.DensityUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.OnItemClickListener;
import com.lovechatapp.chat.view.recycle.RecycleGridDivider;
import com.lovechatapp.chat.view.recycle.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首冲弹窗
 */
public class FirstChargeDialog extends Dialog implements View.OnClickListener {

    private final Activity activity;
    private ChargeResponse chargeResponse;
    private int selectedCharge;
    private int selectedPay;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private boolean request = true;
    private boolean isRequest;

    public FirstChargeDialog(@NonNull Activity context) {
        super(context);
        activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_first_charge);

        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        setCancelable(true);

        TextView confirmBtn = findViewById(R.id.confirm_tv);
        confirmBtn.setOnClickListener(this);


        //充值包
        AbsRecycleAdapter packageAdapter = new AbsRecycleAdapter(
                new AbsRecycleAdapter.Type(R.layout.item_first_charge_package, ChargeListBean.class)) {
            @Override
            public void convert(ViewHolder holder, Object t) {
                ChargeListBean bean = (ChargeListBean) t;
                bean.isSelected = holder.getRealPosition() == selectedCharge;
                if (bean.isSelected) {
                    confirmBtn.setText(String.format("支付%s元", decimalFormat.format(bean.t_money)));
                }
                holder.<TextView>getView(R.id.charge_tv).setTextColor(!bean.isSelected ? 0xff333333 : 0xffffffff);
                holder.<TextView>getView(R.id.send_tv).setTextColor(!bean.isSelected ? 0xff999999 : 0xffffffff);
                holder.<TextView>getView(R.id.gold_tv).setTextColor(!bean.isSelected ? 0xff666666 : 0xffffffff);
                holder.itemView.setBackgroundResource(bean.isSelected ?
                        R.drawable.corner7_solid_purple_ae4ffd : R.drawable.corner7_stroke_gray_eb);
                holder.<TextView>getView(R.id.gold_tv).setText(String.format("%s约豆", bean.t_gold));
                holder.<TextView>getView(R.id.send_tv).setText(String.format("赠送%s约豆", bean.t_give_gold));
                holder.<TextView>getView(R.id.charge_tv).setText(String.format("%s元", decimalFormat.format(bean.t_money)));
            }
        };

        packageAdapter.setDatas(chargeResponse.rechargeList);
        packageAdapter.setOnItemClickListener((view, obj, position) -> {
            selectedCharge = position;
            packageAdapter.notifyDataSetChanged();
        });

        RecyclerView packageRv = findViewById(R.id.content_rv);
        packageRv.addItemDecoration(new RecycleGridDivider(DensityUtil.dip2px(getContext(), 10)));
        packageRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        packageRv.setAdapter(packageAdapter);

        //支付方式
        AbsRecycleAdapter payAdapter = new AbsRecycleAdapter(
                new AbsRecycleAdapter.Type(R.layout.item_first_charge_pay, PayOptionBean.class)) {
            @Override
            public void convert(ViewHolder holder, Object t) {
                PayOptionBean bean = (PayOptionBean) t;
                bean.isSelected = holder.getRealPosition() == selectedPay;
                holder.itemView.setBackgroundResource(bean.isSelected ?
                        R.drawable.corner5_stroke_main : R.drawable.corner5_stroke_gray_eb);
                Glide.with(getContext()).load(bean.payIcon).into(holder.<ImageView>getView(R.id.pay_iv));
                holder.<TextView>getView(R.id.pay_tv).setText(bean.payName);
            }
        };

        payAdapter.setDatas(chargeResponse.payList);
        payAdapter.setOnItemClickListener((view, obj, position) -> {
            selectedPay = position;
            payAdapter.notifyDataSetChanged();
        });

        RecyclerView payRv = findViewById(R.id.pay_rv);
        payRv.addItemDecoration(new RecycleGridDivider(DensityUtil.dip2px(getContext(), 20)));
        payRv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        payRv.setAdapter(payAdapter);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.confirm_tv) {
            ChargeListBean mSelectChargeBean = chargeResponse.rechargeList.get(selectedCharge);
            PayOptionBean mSelectedBean = chargeResponse.payList.get(selectedPay);
            PayChooserActivity.start(activity, mSelectChargeBean.t_id, mSelectedBean.payType, mSelectedBean.t_id, false);
        }
        request = true;
        dismiss();
    }

    @Override
    public void show() {
        if (isRequest) {
            return;
        }
        isRequest = true;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        OkHttpUtils.post()
                .url(ChatApi.getNewFirstChargeInfo())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build()
                .execute(new AjaxCallback<BaseResponse<ChargeResponse>>() {

                    @Override
                    public void onResponse(BaseResponse<ChargeResponse> response, int id) {
                        if (activity == null || activity.isFinishing()) {
                            return;
                        }
                        if (response != null
                                && response.m_istatus == NetCode.SUCCESS
                                && response.m_object != null) {
                            if (response.m_object.payList != null
                                    && response.m_object.payList.size() > 0
                                    && response.m_object.rechargeList != null
                                    && response.m_object.rechargeList.size() > 0) {
                                chargeResponse = response.m_object;
                                int index = 0;
                                for (PayOptionBean payOptionBean : chargeResponse.payList) {
                                    if (payOptionBean.isdefault == 1) {
                                        selectedPay = index;
                                        break;
                                    }
                                    index++;
                                }
                                FirstChargeDialog.super.show();
                            }
                        } else if (response != null) {
                            ToastUtil.INSTANCE.showToast(response.m_strMessage);
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        isRequest = false;
                    }

                });
    }

    private void request() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        OkHttpUtils.post()
                .url(ChatApi.getFirstChargeInfo())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<FirstChargeBean>>() {
            @Override
            public void onResponse(BaseResponse<FirstChargeBean> response, int id) {
                if (activity == null || activity.isFinishing()) {
                    return;
                }
                request = false;
                ok(response.m_istatus == NetCode.SUCCESS ? View.VISIBLE : View.GONE);
            }
        });
    }

    public final void check() {
        if (request) {
            request();
        }
    }

    protected void ok(int visible) {
    }

    public static class FirstChargeBean extends BaseBean {
        public int t_id;
        public int t_gold;
        public int t_give_gold;
        public double t_money;
        public String t_describe;
        public String info;
    }

    static class ChargeResponse extends BaseBean {
        public List<PayOptionBean> payList;
        public List<ChargeListBean> rechargeList;
    }
}