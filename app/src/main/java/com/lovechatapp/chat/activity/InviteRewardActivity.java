package com.lovechatapp.chat.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseBean;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.dialog.ShareDialog;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.util.share.ShareCopyUrl;
import com.lovechatapp.chat.util.share.ShareWechatCircle;
import com.lovechatapp.chat.util.share.ShareWechatGraphic;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.OnItemClickListener;
import com.lovechatapp.chat.view.recycle.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Request;

public class InviteRewardActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.retain_friend_tv)
    TextView retainFriendTv;

    @BindView(R.id.retain_gold_tv)
    TextView retainGoldTv;

    private AbsRecycleAdapter redAdapter;

    private RewardResponse rewardResponse;

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_red_envelopes);
    }

    @Override
    protected void onContentAdded() {

        needHeader(false);

        GridLayoutManager manager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setNestedScrollingEnabled(false);
        redAdapter = new AbsRecycleAdapter(
                new AbsRecycleAdapter.Type(R.layout.item_reward_package, RedPackage.class)) {

            @Override
            public void convert(ViewHolder holder, Object t) {
                RedPackage bean = (RedPackage) t;
                if (bean.isReceived == 1) {
                    holder.<TextView>getView(R.id.state_tv).setTextColor(0xff999999);
                    holder.<TextView>getView(R.id.state_tv).setText("已领取");
                    holder.<ImageView>getView(R.id.content_iv).setImageResource(R.drawable.reward_package_unbg);
                    holder.<ImageView>getView(R.id.anim_iv).setVisibility(View.GONE);
                    stopAnim(holder.<ImageView>getView(R.id.anim_iv));
                } else if (bean.isReceiving == 1) {
                    holder.<TextView>getView(R.id.state_tv).setTextColor(0xffff1437);
                    holder.<TextView>getView(R.id.state_tv).setText("待领取");
                    holder.<ImageView>getView(R.id.content_iv).setImageResource(R.drawable.reward_package_bg);
                    holder.<ImageView>getView(R.id.anim_iv).setVisibility(View.VISIBLE);
                    startAnim(holder.<ImageView>getView(R.id.anim_iv));
                } else {
                    holder.<TextView>getView(R.id.state_tv).setTextColor(0xff333333);
                    holder.<TextView>getView(R.id.state_tv).setText(String.format("差%s人", bean.t_share_people - rewardResponse.shareRewardCount));
                    holder.<ImageView>getView(R.id.content_iv).setImageResource(R.drawable.reward_package_bg);
                    holder.<ImageView>getView(R.id.anim_iv).setVisibility(View.VISIBLE);
                    stopAnim(holder.<ImageView>getView(R.id.anim_iv));
                }
                holder.<TextView>getView(R.id.yuan_tv).setText(String.format("%s元", bean.t_share_rmb));
            }

            private void startAnim(View view) {
                AnimatorSet animSet = (AnimatorSet) view.getTag();
                if (animSet == null) {
                    ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.3f);
                    anim1.setRepeatCount(ValueAnimator.INFINITE);
                    anim1.setRepeatMode(ValueAnimator.REVERSE);
                    ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.3f);
                    anim2.setRepeatCount(ValueAnimator.INFINITE);
                    anim2.setRepeatMode(ValueAnimator.REVERSE);
                    animSet = new AnimatorSet();
                    animSet.setDuration(400);
                    animSet.playTogether(anim1, anim2);
                    view.setTag(animSet);
                }
                if (animSet.isPaused()) {
                    animSet.resume();
                } else {
                    animSet.start();
                }
            }

            private void stopAnim(View view) {
                AnimatorSet animSet = (AnimatorSet) view.getTag();
                if (animSet != null) {
                    animSet.pause();
                }
            }

        };
        recyclerView.setAdapter(redAdapter);
        redAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object obj, int position) {
                RedPackage bean = (RedPackage) obj;
                if (bean.isReceiving == 1 && bean.isReceived != 1) {
                    receivePackage(bean);
                }
            }
        });

        getRewardDetails();
    }

    private void receivePackage(RedPackage redPackage) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("t_reward_id", redPackage.t_id);
        OkHttpUtils.post().url(ChatApi.receiveShareRewardGold())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<String>>() {
            @Override
            public void onResponse(BaseResponse<String> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        redPackage.isReceived = 1;
                        redPackage.isReceiving = 0;
                        redAdapter.notifyDataSetChanged();
                        new ReceivePackageDialog(mContext, redPackage).show();
                    } else {
                        if (response.m_istatus == -5) {
                            redPackage.isReceived = 1;
                            redPackage.isReceiving = 0;
                            redAdapter.notifyDataSetChanged();
                        }
                        ToastUtil.INSTANCE.showToast(response.m_strMessage);
                    }

                }
            }

            @Override
            public void onBefore(Request request, int id) {
                showLoadingDialog();
            }

            @Override
            public void onAfter(int id) {
                if (isFinishing()) {
                    return;
                }
                dismissLoadingDialog();
            }
        });
    }

    private void getRewardDetails() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post().url(ChatApi.getShareRewardConfigList())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<RewardResponse>>() {
            @Override
            public void onResponse(BaseResponse<RewardResponse> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS && response.m_object != null) {

                    rewardResponse = response.m_object;

                    //总收益
                    retainGoldTv.setText(String.valueOf(rewardResponse.receiveAllGold));

                    //总人数
                    retainFriendTv.setText(String.valueOf(rewardResponse.shareRewardCount));

                    redAdapter.setData(rewardResponse.shareRewardList, true);
                }
            }
        });
    }

    @OnClick({R.id.btn_invitation, R.id.finish_btn})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_invitation: {
                new ShareDialog(mContext, Arrays.asList(
                        new ShareDialog.ShareInfo(R.drawable.share_we_chat, "微信好友", new ShareWechatGraphic()),
                        new ShareDialog.ShareInfo(R.drawable.share_we_circle, "微信朋友圈", new ShareWechatCircle()),
                       // new ShareDialog.ShareInfo(R.drawable.share_qq, "QQ", new ShareQQ()),
                       // new ShareDialog.ShareInfo(R.drawable.share_qq_zone, "QQ空间", new ShareQZone()),
                       // new ShareDialog.ShareInfo(R.drawable.share_poster, "分享海报", new SharePoster()),
                        new ShareDialog.ShareInfo(R.drawable.share_copy, "复制链接", new ShareCopyUrl())
                )).show();
                break;
            }

            case R.id.finish_btn: {
                finish();
                break;
            }
        }

    }

    private static class ReceivePackageDialog extends Dialog {

        private RedPackage bean;

        public ReceivePackageDialog(@NonNull Context context, RedPackage bean) {
            super(context);
            this.bean = bean;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            setContentView(R.layout.dialog_receive_package);

            Window win = getWindow();
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            win.setAttributes(lp);

            findViewById(R.id.finish_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            TextView textView = findViewById(R.id.yuan_tv);
            textView.setText(String.format("￥%s", bean.t_share_rmb));
        }
    }

    public static class RedPackage extends BaseBean {

        public int t_id;
        public int t_share_people;
        public int t_share_rmb;
        public int t_share_reward_gold;
        public long t_create_time;
        public int t_is_use;
        public int t_reward_id;
        public int isReceived;
        public int isReceiving;

    }

    public static class RewardResponse extends BaseBean {

        public List<RedPackage> shareRewardList;
        public int receiveAllGold;
        public int shareRewardCount;

    }

}