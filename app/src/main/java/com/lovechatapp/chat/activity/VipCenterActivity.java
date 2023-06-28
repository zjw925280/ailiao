package com.lovechatapp.chat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseListResponse;
import com.lovechatapp.chat.bean.VipBean;
import com.lovechatapp.chat.bean.VipInfoBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.glide.GlideCircleTransform;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.OnItemClickListener;
import com.lovechatapp.chat.view.recycle.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * VIP & sVIP
 */
@SuppressLint("NonConstantResourceId")
public class VipCenterActivity extends BaseActivity {

    public static void start(Context context, boolean svip) {
        Intent starter = new Intent(context, VipCenterActivity.class);
        starter.putExtra("svip", svip);
        context.startActivity(starter);
    }

    @BindView(R.id.vip_tv)
    TextView vipTv;

    @BindView(R.id.vip_interests_rv)
    RecyclerView vipInterestsRv;

    @BindView(R.id.package_rv)
    RecyclerView packageRv;

    @BindView(R.id.vip_pay)
    TextView vipPay;

    AbsRecycleAdapter adapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_vip);
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    protected void onContentAdded() {

        needHeader(false);

        Glide.with(mContext)
                .load(AppManager.getInstance().getUserInfo().headUrl)
                .transform(new GlideCircleTransform(mContext))
                .into((ImageView) findViewById(R.id.head_iv));

        setInterestsRv();

        getVipList();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshVip();
    }

    @OnClick({R.id.vip_pay, R.id.finish_btn})
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.vip_pay: {

                //开始支付
                if (adapter != null && adapter.getData() != null) {
                    VipBean selectedBean = null;
                    for (Object datum : adapter.getData()) {
                        VipBean bean = (VipBean) datum;
                        if (bean.isSelected) {
                            selectedBean = bean;
                            break;
                        }
                    }
                    if (selectedBean == null) {
                        ToastUtil.INSTANCE.showToast("请选择VIP");
                        return;
                    }
                    PayChooserActivity.start(mContext, selectedBean.t_id);
                }
                break;
            }

            case R.id.finish_btn: {
                finish();
                break;
            }

        }

    }

    /**
     * vip到期时间
     */
    protected void setVipTv(VipInfoBean bean) {
        vipTv.setText(" 未开通");
        if (bean.t_is_vip == 0) {
            vipTv.setText(String.format(" %s到期", bean.vipTime.t_end_time));
        }
    }

    /**
     * 更新VIP状态UI
     */
    private void refreshVip() {
        AppManager.getInstance().refreshVip(bean -> {
            if (isFinishing())
                return;
            setVipTv(bean);
        });
    }

    /**
     * 设置权益RecycleView
     */
    private void setInterestsRv() {
        AbsRecycleAdapter adapter = new AbsRecycleAdapter(
                new AbsRecycleAdapter.Type(R.layout.item_vip_rights_interests, RightsInterestsBean.class)) {
            @Override
            public void convert(ViewHolder holder, Object t) {
                RightsInterestsBean bean = (RightsInterestsBean) t;
                holder.<TextView>getView(R.id.title_tv)
                        .setCompoundDrawablesRelativeWithIntrinsicBounds(0, bean.drawId, 0, 0);
                holder.<TextView>getView(R.id.title_tv).setText(bean.title);
                holder.<TextView>getView(R.id.sub_title_tv).setText(bean.subtitle);
            }
        };
        vipInterestsRv.setLayoutManager(new GridLayoutManager(mContext, 3));
        vipInterestsRv.setAdapter(adapter);
        adapter.setDatas(getRightsInterests());
    }

    /**
     * 获取权益数据
     */
    protected List<RightsInterestsBean> getRightsInterests() {
        return Arrays.asList(

                new RightsInterestsBean(
                        R.drawable.vip_rights_interests1,
                        "每天免费视频五分钟",
                        "相当于每天送你10元钱"),

                new RightsInterestsBean(
                        R.drawable.vip_rights_interests2,
                        "开通赠送金币",
                        "开通会员则送金币"),

                new RightsInterestsBean(
                        R.drawable.vip_rights_interests3,
                        "充值另送金币",
                        "充值额外赠送金币"),

                new RightsInterestsBean(
                        R.drawable.vip_rights_interests4,
                        "文字聊天免费",
                        "文字聊天完全免费"),

                new RightsInterestsBean(
                        R.drawable.vip_rights_interests5,
                        "发送语音消息",
                        "可以按住说话发送语音"),

                new RightsInterestsBean(
                        R.drawable.vip_rights_interests6,
                        "发送图片消息",
                        "拍摄或从相册选择发送图片"),

                new RightsInterestsBean(
                        R.drawable.vip_rights_interests7,
                        "榜单隐身",
                        "排行榜中隐身"),

                new RightsInterestsBean(
                        R.drawable.vip_rights_interests8,
                        "屏蔽飘屏",
                        "屏蔽所有飘屏"),

                new RightsInterestsBean(
                        R.drawable.vip_rights_interests9,
                        "尊贵会员标识",
                        "专属会员标识，女神更喜欢")

        );
    }

    /**
     * 获取VIP列表
     */
    private void getVipList() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("t_vip_type", 0);
        OkHttpUtils.post().url(ChatApi.GET_VIP_SET_MEAL_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<VipBean>>() {
                    @Override
                    public void onResponse(BaseListResponse<VipBean> response, int id) {
                        if (isFinishing()) {
                            return;
                        }
                        if (response != null && response.m_istatus == NetCode.SUCCESS) {
                            List<VipBean> vipBeans = response.m_object;
                            if (vipBeans != null && vipBeans.size() > 0) {
                                vipBeans.get(0).isSelected = true;
                                setVipList(vipBeans);
                            }
                        }
                    }
                });
    }

    /**
     * vip包列表
     */
    private void setVipList(List<VipBean> beans) {
        if (adapter == null) {
            final DecimalFormat decimalFormat = new DecimalFormat("#.##");
            adapter = new AbsRecycleAdapter(new AbsRecycleAdapter.Type(R.layout.item_vip_package, VipBean.class)) {
                @Override
                public void convert(ViewHolder holder, Object t) {
                    VipBean bean = (VipBean) t;
                    if (bean.isSelected) {
                        vipPay.setText(String.format("立即支付￥%s元", decimalFormat.format(bean.t_money)));
                    }
                    holder.itemView.setSelected(bean.isSelected);
                    holder.<TextView>getView(R.id.month_tv).setText(bean.t_setmeal_name);
                    holder.<TextView>getView(R.id.price_tv).setText(String.format("￥%s", decimalFormat.format(bean.t_money)));
                    holder.<TextView>getView(R.id.gold_tv).setText(bean.t_remarks);
                    holder.<TextView>getView(R.id.day_price_tv).setText(bean.avgDayMoney);
                    holder.getView(R.id.recommend_tv).setVisibility(bean.t_is_recommend == 1 ? View.VISIBLE : View.GONE);
                }
            };
            adapter.setOnItemClickListener((view, object, position) -> {
                VipBean selectedBean = (VipBean) adapter.getData().get(position);
                for (Object datum : adapter.getData()) {
                    VipBean bean = (VipBean) datum;
                    bean.isSelected = bean == selectedBean;
                }
                adapter.notifyDataSetChanged();
            });
            adapter.setDatas(beans);
            packageRv.setLayoutManager(new GridLayoutManager(mContext, 3));
            packageRv.setAdapter(adapter);
        }
    }

    protected static class RightsInterestsBean {

        public RightsInterestsBean(int drawId, String title, String subtitle) {
            this.drawId = drawId;
            this.title = title;
            this.subtitle = subtitle;
        }

        public int drawId;
        public String title;
        public String subtitle;
    }

}