package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.ChargeActivity;
import com.lovechatapp.chat.adapter.GiftPagerAdapter;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseListResponse;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.BalanceBean;
import com.lovechatapp.chat.bean.GiftBean;
import com.lovechatapp.chat.bean.MansionUserInfoBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.OnItemClickListener;
import com.lovechatapp.chat.view.recycle.OnViewPagerListener;
import com.lovechatapp.chat.view.recycle.ViewHolder;
import com.lovechatapp.chat.view.recycle.ViewPagerLayoutManager;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 多人通话打赏礼物
 */
public class MultipleGiftDialog extends Dialog {

    private List<GiftBean> mGiftBeans;

    private Activity activity;

    private int mMyGoldNumber;

    private TextView reward_tv;

    private List<MansionUserInfoBean> list;

    private AbsRecycleAdapter adapter;

    public MultipleGiftDialog(@NonNull Activity context, List<MansionUserInfoBean> anochorInfo) {
        super(context, R.style.DialogStyle);
        this.activity = context;
        this.list = anochorInfo;
        for (MansionUserInfoBean userInfoBean : this.list) {
            userInfoBean.selected = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_multiple_gift_layout);

        setCanceledOnTouchOutside(true);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        RecyclerView recyclerView = findViewById(R.id.user_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new AbsRecycleAdapter(
                new AbsRecycleAdapter.Type(R.layout.item_multiple_gift_user, MansionUserInfoBean.class)) {

            @Override
            public void convert(ViewHolder holder, Object t) {
                MansionUserInfoBean bean = (MansionUserInfoBean) t;
                holder.<ImageView>getView(R.id.content_iv)
                        .setBackgroundResource(bean.selected ? R.drawable.circle_main : R.drawable.circle_white);
                Glide.with(holder.itemView.getContext())
                        .load(bean.t_handImg)
                        .error(R.drawable.default_head)
                        .transform(new CircleCrop())
                        .into(holder.<ImageView>getView(R.id.content_iv));
            }
        };
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object obj, int position) {
                MansionUserInfoBean bean = (MansionUserInfoBean) obj;
                bean.selected = !bean.selected;
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.setDatas(list);

        findViewById(R.id.dismiss_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getGiftList();
    }

    private String getUserId() {
        return AppManager.getInstance().getUserInfo().t_id + "";
    }

    /**
     * 获取礼物列表
     */
    private void getGiftList() {
        if (mGiftBeans != null) {
            setGiftDialogView();
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post()
                .url(ChatApi.GET_GIFT_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<GiftBean>>() {
            @Override
            public void onResponse(BaseListResponse<GiftBean> response, int id) {
                if (!isShowing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<GiftBean> giftBeans = response.m_object;
                    if (giftBeans != null && giftBeans.size() > 0) {
                        mGiftBeans = giftBeans;
                        setGiftDialogView();
                    } else {
                        handleError();
                    }
                } else {
                    handleError();
                }
            }
        });
    }

    /**
     * 礼物dialog view 初始化
     */
    private void setGiftDialogView() {
        //-----------------初始化----------------
        final RecyclerView gift_rv = findViewById(R.id.gift_rv);
        final LinearLayout indicator_ll = findViewById(R.id.indicator_ll);
        final TextView gift_tv = findViewById(R.id.gift_tv);
        final TextView gold_tv = findViewById(R.id.gold_tv);
        TextView charge_tv = findViewById(R.id.charge_tv);
        reward_tv = findViewById(R.id.reward_tv);

        //初始化显示礼物
        gift_tv.setSelected(true);
        gift_rv.setVisibility(View.VISIBLE);
        indicator_ll.setVisibility(View.VISIBLE);

        //可用金币
        getMyGold(gold_tv);

        //处理list
        List<List<GiftBean>> giftListBeanList = new ArrayList<>();
        if (mGiftBeans != null && mGiftBeans.size() > 0) {
            int count = mGiftBeans.size() / 8;
            int left = mGiftBeans.size() % 8;
            if (count > 0) {//如果大于等于8个
                for (int i = 1; i <= count; i++) {
                    int start = (i - 1) * 8;
                    int end = i * 8;
                    List<GiftBean> subList = mGiftBeans.subList(start, end);
                    giftListBeanList.add(i - 1, subList);
                }
                if (left != 0) {//如果还剩余的话,那剩余的加进入
                    List<GiftBean> leftBeans = mGiftBeans.subList(count * 8, mGiftBeans.size());
                    giftListBeanList.add(count, leftBeans);
                }
            } else {
                giftListBeanList.add(0, mGiftBeans);
            }
        }

        //-----------------礼物---------------
        final List<ImageView> imageViews = new ArrayList<>();
        ViewPagerLayoutManager mLayoutManager = new ViewPagerLayoutManager(getContext(), OrientationHelper.HORIZONTAL);
        gift_rv.setLayoutManager(mLayoutManager);
        final GiftPagerAdapter giftAdapter = new GiftPagerAdapter(getContext());
        gift_rv.setAdapter(giftAdapter);
        if (giftListBeanList.size() > 0) {
            giftAdapter.loadData(giftListBeanList);
            //设置指示器
            for (int i = 0; i < giftListBeanList.size(); i++) {
                ImageView imageView = new ImageView(getContext());
                int width = DevicesUtil.dp2px(getContext(), 6);
                int height = DevicesUtil.dp2px(getContext(), 6);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                params.leftMargin = 10;
                imageView.setLayoutParams(params);
                if (i == 0) {
                    imageView.setImageResource(R.drawable.shape_gift_indicator_white_back);
                } else {
                    imageView.setImageResource(R.drawable.shape_gift_indicator_gray_back);
                }
                imageViews.add(imageView);
                indicator_ll.addView(imageView);
            }
        }

        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {

            @Override
            public void onInitComplete() {

            }

            @Override
            public void onPageRelease(View view) {

            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                if (imageViews.size() > 0) {
                    for (int i = 0; i < imageViews.size(); i++) {
                        if (i == position) {
                            imageViews.get(i).setImageResource(R.drawable.shape_gift_indicator_white_back);
                        } else {
                            imageViews.get(i).setImageResource(R.drawable.shape_gift_indicator_gray_back);
                        }
                    }
                }
            }
        });

        //--------------处理切换----------------
        //礼物
        gift_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gift_tv.isSelected()) {
                    return;
                }
                gift_tv.setSelected(true);
                gift_rv.setVisibility(View.VISIBLE);
                indicator_ll.setVisibility(View.VISIBLE);
            }
        });

        //充值
        charge_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ChargeActivity.class);
                activity.startActivity(intent);
                dismiss();
            }
        });

        //dismiss的时候清空
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mGiftBeans != null && mGiftBeans.size() > 0) {
                    for (GiftBean bean : mGiftBeans) {
                        bean.isSelected = false;
                    }
                }
            }
        });

        //打赏
        reward_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedSize = 0;
                List<MansionUserInfoBean> list = new ArrayList<>();
                String ids = "";
                for (MansionUserInfoBean userInfoBean : MultipleGiftDialog.this.list) {
                    if (userInfoBean.selected) {
                        selectedSize++;
                        list.add(userInfoBean);
                        ids += TextUtils.isEmpty(ids) ? userInfoBean.t_id : "," + userInfoBean.t_id;
                    }
                }
                if (selectedSize == 0) {
                    ToastUtil.INSTANCE.showToast(activity, "请点击头像，选择受赠礼物的主播");
                    return;
                }
                GiftBean giftBean = giftAdapter.getSelectBean();
                if (giftBean == null) {
                    ToastUtil.INSTANCE.showToast(activity, R.string.please_select_gift);
                    return;
                }
                if (selectedSize * giftBean.t_gift_gold > mMyGoldNumber) {
                    ToastUtil.INSTANCE.showToast(activity, R.string.gold_not_enough);
                    return;
                }
                reWardGift(giftBean, ids, list);
            }
        });
    }

    /**
     * 打赏礼物
     */

    private void reWardGift(final GiftBean giftBean, String ids, final List<MansionUserInfoBean> list) {
        reward_tv.setVisibility(View.GONE);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverConsumeUserId", ids);
        paramMap.put("giftId", giftBean.t_gift_id);
        paramMap.put("giftNum", "1");
        OkHttpUtils.post().url(ChatApi.USER_GIVE_GIFT())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (!isShowing()) {
                    return;
                }
                reward_tv.setVisibility(View.VISIBLE);
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ToastUtil.INSTANCE.showToast(activity, activity.getString(R.string.reward_success));
                        dismiss();
                        sendOk(giftBean, 0, list);
                    } else if (response.m_istatus == -1) {
                        ToastUtil.INSTANCE.showToast(activity, R.string.gold_not_enough);
                    } else {
                        ToastUtil.INSTANCE.showToast(activity, activity.getString(R.string.pay_fail));
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(activity, activity.getString(R.string.pay_fail));
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                if (!isShowing()) {
                    return;
                }
                reward_tv.setVisibility(View.VISIBLE);
            }
        });
    }

    public void sendOk(GiftBean giftBean, int gold, List<MansionUserInfoBean> list) {

    }

    /**
     * 获取我的金币余额
     */
    private void getMyGold(final TextView can_use_iv) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        OkHttpUtils.post()
                .url(ChatApi.GET_USER_BALANCE())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<BalanceBean>>() {
            @Override
            public void onResponse(BaseResponse<BalanceBean> response, int id) {
                if (!isShowing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    BalanceBean balanceBean = response.m_object;
                    if (balanceBean != null) {
                        mMyGoldNumber = balanceBean.amount;
                        String content = activity.getResources().getString(R.string.can_use_gold) + mMyGoldNumber;
                        can_use_iv.setText(content);
                        can_use_iv.setVisibility(View.VISIBLE);
                    } else {
                        handleError();
                    }
                } else {
                    handleError();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (!isShowing()) {
                    return;
                }
                super.onError(call, e, id);
                handleError();
            }
        });
    }

    private void handleError() {
        ToastUtil.INSTANCE.showToast(activity, R.string.data_get_error);
        dismiss();
    }
}