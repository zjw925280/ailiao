package com.lovechatapp.chat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.base.BaseListResponse;
import com.lovechatapp.chat.bean.VipBean;
import com.lovechatapp.chat.bean.VipInfoBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.activity.PayChooserActivity;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * vip
 */
public class VipFragment extends BaseFragment {

    @BindView(R.id.vip_tv)
    TextView vipTv;

    @BindView(R.id.vip_btn)
    TextView vipBtn;

    @BindView(R.id.rights_interests_rv)
    RecyclerView rightsInterestsRv;

    @BindView(R.id.package_rv)
    RecyclerView packageRv;

    @BindView(R.id.vip_pay)
    TextView vipPay;

    @BindView(R.id.rights_interests_iv)
    ImageView rightsInterestsIv;

    @BindView(R.id.open_iv)
    ImageView openIv;

    @BindView(R.id.bg_ll)
    View bgView;

    AbsRecycleAdapter adapter;

    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRightsInterestsRv();
        getVipList();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshVip();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_vip;
    }

    @OnClick({R.id.vip_pay})
    public void onClick(View view) {

        /**
         * 开始支付
         */
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
            PayChooserActivity.start(getActivity(), selectedBean.t_id);
        }
    }

    /**
     * vip到期时间
     */
    protected void setVipTv(VipInfoBean bean) {
        vipTv.setText("未开通");
        if (bean.t_is_vip == 0) {
            vipTv.setText(String.format("%s到期", bean.vipTime.t_end_time));
        }
    }

    /**
     * 更新VIP状态UI
     */
    private void refreshVip() {
        AppManager.getInstance().refreshVip(new OnCommonListener<VipInfoBean>() {
            @Override
            public void execute(VipInfoBean bean) {
                if (getActivity() == null || getActivity().isFinishing())
                    return;
                setVipTv(bean);
            }
        });
    }

    /**
     * 设置权益RecycleView
     */
    private void setRightsInterestsRv() {
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
        rightsInterestsRv.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rightsInterestsRv.setAdapter(adapter);
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
                        R.drawable.vip_rights_interests6,
                        "榜单隐身",
                        "排行榜中隐身"),

                new RightsInterestsBean(
                        R.drawable.vip_rights_interests6,
                        "屏蔽飘屏",
                        "屏蔽所有飘屏"),

                new RightsInterestsBean(
                        R.drawable.vip_rights_interests6,
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
        paramMap.put("t_vip_type", getVipType());
        OkHttpUtils.post().url(ChatApi.GET_VIP_SET_MEAL_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<VipBean>>() {
            @Override
            public void onResponse(BaseListResponse<VipBean> response, int id) {
                if (getActivity() == null || getActivity().isFinishing()) {
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
     * 0:普通vip  2:超级svip
     */
    protected int getVipType() {
        return 0;
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
                        vipPay.setText(String.format("立即支付%s元", decimalFormat.format(bean.t_money)));
                    }
                    holder.itemView.setSelected(bean.isSelected);
                    holder.<TextView>getView(R.id.month_tv).setText(bean.t_setmeal_name);
                    holder.<TextView>getView(R.id.price_tv).setText(String.format("￥%s", decimalFormat.format(bean.t_money)));
                    holder.<TextView>getView(R.id.day_price_tv).setText(bean.t_remarks);
                    holder.<TextView>getView(R.id.day_price_tv).setVisibility(TextUtils.isEmpty(bean.t_remarks) ? View.INVISIBLE : View.VISIBLE);
                }
            };
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, Object object, int position) {
                    VipBean selectedBean = (VipBean) adapter.getData().get(position);
                    for (Object datum : adapter.getData()) {
                        VipBean bean = (VipBean) datum;
                        bean.isSelected = bean == selectedBean;
                    }
                    adapter.notifyDataSetChanged();
                }
            });
            adapter.setDatas(beans);
            packageRv.setLayoutManager(new GridLayoutManager(getActivity(), 3));
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