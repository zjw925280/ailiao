package com.lovechatapp.chat.view.recycle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.cjt2325.cameralibrary.util.ScreenUtils;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.ChargeActivity;
import com.lovechatapp.chat.activity.CommonWebViewActivity;
import com.lovechatapp.chat.activity.HelpCenterActivity;
import com.lovechatapp.chat.activity.InviteEarnActivity;
import com.lovechatapp.chat.activity.InviteRewardActivity;
import com.lovechatapp.chat.activity.PhoneNaviActivity;
import com.lovechatapp.chat.activity.RankActivity;
import com.lovechatapp.chat.activity.VipCenterActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseBean;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.BannerBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.glide.GlideRoundTransform;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.DensityUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.view.banner.BannerIndicator;
import com.lovechatapp.chat.view.banner.HorizontalBanner;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BannerHolder {

    private HorizontalBanner mBannerView;
    private List<BannerBean> bannerBeans;
    private BannerIndicator indicator;
    private Context context;
    private Banner banner;

    private ImageView leftIv;
    private ImageView rightIv;

    private Activity activity;

    public BannerHolder(Activity activity, View view) {
        this.activity = activity;
        mBannerView = view.findViewById(R.id.banner);
        indicator = view.findViewById(R.id.indicator);
        leftIv = view.findViewById(R.id.left_iv);
        rightIv = view.findViewById(R.id.right_iv);
        context = view.getContext();
    }

    public final void loop(Activity activity, boolean b) {
        if (bannerBeans != null) {
            mBannerView.loop(b);
        } else {
            getBannerList(activity);
        }
    }

    /**
     * 获取banner
     */
    private void getBannerList(final Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (banner != null) {
            return;
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("t_banner_type", 0);
        OkHttpUtils.post().url(ChatApi.getAllBannerList())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<Banner>>() {
            @Override
            public void onResponse(BaseResponse<Banner> response, int id) {
                if (activity.isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    if (response.m_object != null) {
                        banner = response.m_object;
                        setBanner(banner.bannerList);
                        setImg(null);
                    }
                }
            }
        });
    }

    private void setImg(List<BannerBean> beans) {
        if (beans == null || beans.size() == 0) {
            ((View) leftIv.getParent()).setVisibility(View.GONE);
        } else {
            ((View) leftIv.getParent()).setVisibility(View.VISIBLE);
            final BannerBean leftBean = beans.get(0);
            Glide.with(leftIv.getContext())
                    .load(leftBean.t_img_url)
                    .transform(new CenterCrop(), new GlideRoundTransform(6))
                    .into(leftIv);
            leftIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toIntent(context, leftBean.t_link_url);
                }
            });
            if (beans.size() > 1) {
                final BannerBean rightBean = beans.get(1);
                Glide.with(rightIv.getContext())
                        .load(rightBean.t_img_url)
                        .transform(new CenterCrop(), new GlideRoundTransform(6))
                        .into(rightIv);
                rightIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toIntent(context, rightBean.t_link_url);
                    }
                });
            } else {
                rightIv.setImageResource(0);
                rightIv.setOnClickListener(null);
            }
        }
    }

    private void setBanner(List<BannerBean> beans) {
        if (bannerBeans != null || beans == null || beans.size() == 0) {
            return;
        }
        final int overWidth = ScreenUtils.getScreenWidth(mBannerView.getContext())
                - (int) mBannerView.getContext().getResources().getDimension(R.dimen.item_space) * 2;
        final int overHeight = DensityUtil.dip2px(mBannerView.getContext(), 95);

        bannerBeans = beans;
        mBannerView.initDataSize(beans.size(), new HorizontalBanner.OnBannerListener() {

            @Override
            public void bind(ImageView imageView, int position) {
                if (activity.isFinishing()) {
                    return;
                }
                BannerBean bean = bannerBeans.get(position);
                Glide.with(imageView.getContext())
                        .load(bean.t_img_url)
                        .error(R.drawable.default_back)
                        .override(overWidth, overHeight)
                        .transform(new CenterCrop(), new GlideRoundTransform(6))
                        .into(imageView);
            }

            @Override
            public void onItemClick(int position) {
                BannerBean bean = bannerBeans.get(position);
                toIntent(context, bean.t_link_url);
            }

            @Override
            public void onSelected(int position) {
                if (indicator != null) {
                    indicator.setCurrentIndicator(position);
                }
            }
        });
        mBannerView.loop(true);
        if (indicator != null) {
            indicator.setIndicatorCount(bannerBeans.size());
        }
    }

    public static void toIntent(Context context, String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("http")) {
                Intent intent = new Intent(context, CommonWebViewActivity.class);
                intent.putExtra(Constant.TITLE, context.getResources().getString(R.string.app_name));
                intent.putExtra(Constant.URL, url);
                context.startActivity(intent);
            } else if (url.contains("InviteEarn")) {//跳转内部
                Intent intent = new Intent(context, InviteEarnActivity.class);
                context.startActivity(intent);
            } else if (url.contains("PhoneNavi")) {//手机指南
                Intent intent = new Intent(context, PhoneNaviActivity.class);
                context.startActivity(intent);
            } else if (url.contains("HelpCenter")) {//帮助中心
                Intent intent = new Intent(context, HelpCenterActivity.class);
                context.startActivity(intent);
            } else if (url.contains("Rank")) {//排行榜
                Intent intent = new Intent(context, RankActivity.class);
                context.startActivity(intent);
            } else if (url.contains("Invite")) {//邀请有奖
                Intent intent = new Intent(context, InviteRewardActivity.class);
                context.startActivity(intent);
            } else if (url.contains("VipCenter")) {//Vip
                Intent intent = new Intent(context, VipCenterActivity.class);
                context.startActivity(intent);
            } else if (url.contains("Charge")) {//Charge
                Intent intent = new Intent(context, ChargeActivity.class);
                context.startActivity(intent);
            }
        }
    }

    private static class Banner extends BaseBean {
        public List<BannerBean> beancurdList;
        public List<BannerBean> bannerList;
    }

}