package com.lovechatapp.chat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.VipInfoBean;

import java.util.Arrays;
import java.util.List;

/**
 * sVIP
 */
public class SVipFragment extends VipFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        vipTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.aa_charge_svip, 0, 0, 0);
        openIv.setImageResource(R.drawable.svip_open);
        rightsInterestsIv.setImageResource(R.drawable.svip_rights_interests);
        bgView.setBackgroundResource(R.drawable.bg_charge_svip);
    }

    protected void setVipTv(VipInfoBean bean) {
        vipTv.setText("未开通");
        if (bean.t_is_svip == 0) {
            vipTv.setText(String.format("%s到期", bean.svipTime.t_end_time));
        }
    }

    protected int getVipType() {
        return 2;
    }

    protected List<RightsInterestsBean> getRightsInterests() {
        return Arrays.asList(
                new RightsInterestsBean(
                        R.drawable.svip_rights_interests1,
                        "普通VIP所有特权",
                        "享受普通VIP的所有特权"),
                new RightsInterestsBean(
                        R.drawable.svip_rights_interests3,
                        "每天免费视频五分钟",
                        "相当于每天送你10元钱"),
                new RightsInterestsBean(
                        R.drawable.svip_rights_interests4,
                        "附近主播",
                        "免费查询附近的主播"),
                new RightsInterestsBean(
                        R.drawable.svip_rights_interests5,
                        "尊贵SVIP会员标识",
                        "svip专用头像，更受女神偏爱"),
                new RightsInterestsBean(
                        R.drawable.svip_rights_interests6,
                        "查看访客详情",
                        "访问过的女神更容易撩")
        );
    }
}