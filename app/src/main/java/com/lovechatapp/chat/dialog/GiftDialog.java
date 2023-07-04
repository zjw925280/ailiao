package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.ChargeActivity;
import com.lovechatapp.chat.adapter.GiftPagerAdapter;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseListResponse;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.BalanceBean;
import com.lovechatapp.chat.bean.GiftBean;
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
 * 打赏礼物&红包
 */
public class GiftDialog extends Dialog {

    private GiftPagerAdapter giftAdapter;
    private AbsRecycleAdapter adapter;
    private int amount = 1;
    private List<GiftBean> mGiftBeans;
    private final Activity activity;
    private final int mActorId;
    private int mMyGoldNumber;
    private View rootView;
    private TextView reward_tv;
    private TextView amountTv;
    private View popView;
    private View rewardLl;

    public GiftDialog(@NonNull Activity context, int actorId) {
        super(context, R.style.DialogStyle);
        this.activity = context;
        mActorId = actorId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_gift_layout, null);
        setContentView(rootView);
        setCanceledOnTouchOutside(true);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        initAmountRv();

        //其他数量
        findViewById(R.id.other_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popView.setVisibility(View.GONE);
                new GiftInputDialog(getContext()) {
                    @Override
                    protected void ok(int count) {
                        setAmount(count);
                    }
                }.show();
            }
        });

        rewardLl = findViewById(R.id.reward_ll);
        amountTv = findViewById(R.id.amount_tv);
        amountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (giftAdapter == null) {
                    return;
                }
                GiftBean giftBean = giftAdapter.getSelectBean();
                if (giftBean == null) {
                    ToastUtil.INSTANCE.showToast(activity, R.string.please_select_gift);
                    return;
                }
                adapter.setDatas(giftBean.twoGiftList);
                popView.setVisibility(View.VISIBLE);
            }
        });

        popView = findViewById(R.id.pop_view);
        popView.setVisibility(View.GONE);

        findViewById(R.id.pop_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popView.setVisibility(View.GONE);
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
            setGiftDialogView(rootView, GiftDialog.this);
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
                        setGiftDialogView(rootView, GiftDialog.this);
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
    private void setGiftDialogView(View view, final Dialog mDialog) {

        //-----------------初始化----------------
        final RecyclerView gift_rv = view.findViewById(R.id.gift_rv);

        final LinearLayout indicator_ll = view.findViewById(R.id.indicator_ll);
        final TextView gift_tv = view.findViewById(R.id.gift_tv);

        final TextView gold_tv = view.findViewById(R.id.gold_tv);
        TextView charge_tv = view.findViewById(R.id.charge_tv);
        reward_tv = view.findViewById(R.id.reward_tv);

        //初始化显示礼物
        gift_tv.setSelected(true);
        gift_rv.setVisibility(View.VISIBLE);
        indicator_ll.setVisibility(View.VISIBLE);

        //可用约豆
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
        giftAdapter = new GiftPagerAdapter(getContext()) {
            @Override
            protected void selectedGift() {
                setAmount(1);
            }
        };
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

        //充值
        charge_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ChargeActivity.class);
                activity.startActivity(intent);
                mDialog.dismiss();
            }
        });

        //dismiss的时候清空
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
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

                GiftBean giftBean = giftAdapter.getSelectBean();
                if (giftBean == null) {
                    ToastUtil.INSTANCE.showToast(activity, R.string.please_select_gift);
                    return;
                }
                //判断是否够
                if (giftBean.t_gift_gold > mMyGoldNumber) {
                    ToastUtil.INSTANCE.showToast(activity, R.string.gold_not_enough);
                    return;
                }
                if (giftBean.t_gift_gold * amount > 50000) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("友情提示")
                            .setMessage("你当前打赏的礼物超过500元，打赏后无法退回，请谨慎。")
                            .setNegativeButton(R.string.cancel, null)
                            .setPositiveButton("继续打赏", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    reWardGift(giftBean);
                                }
                            }).create().show();
                } else {
                    reWardGift(giftBean);
                }
            }
        });
    }

    private void initAmountRv() {
        RecyclerView amountRv = findViewById(R.id.amount_rv);
        adapter = new AbsRecycleAdapter(
                new AbsRecycleAdapter.Type(R.layout.item_gift_amount, GiftBean.GiftAmountBean.class)) {
            @Override
            public void convert(ViewHolder holder, Object t) {
                GiftBean.GiftAmountBean bean = (GiftBean.GiftAmountBean) t;
                holder.<TextView>getView(R.id.content_tv).setText(String.valueOf(bean.t_two_gift_number));
            }
        };
        amountRv.setAdapter(adapter);
        amountRv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object obj, int position) {
                GiftBean.GiftAmountBean bean = (GiftBean.GiftAmountBean) adapter.getData().get(position);
                setAmount(bean.t_two_gift_number);
                popView.setVisibility(View.GONE);
            }
        });
    }

    private void setAmount(int amount) {
        this.amount = amount;
        amountTv.setText(String.valueOf(amount));
    }

    /**
     * 打赏礼物
     */
    private void reWardGift(final GiftBean giftBean) {
        rewardLl.setVisibility(View.INVISIBLE);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverConsumeUserId", mActorId);
        paramMap.put("giftId", giftBean.t_gift_id);
        paramMap.put("giftNum", amount);
        OkHttpUtils.post().url(ChatApi.USER_GIVE_GIFT())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (!isShowing()) {
                    return;
                }
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        ToastUtil.INSTANCE.showToast(activity, R.string.reward_success);
                        dismiss();
                        sendOk(giftBean, 0);
                    } else {
                        ToastUtil.INSTANCE.showToast(activity, response.m_strMessage);
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(activity, R.string.pay_fail);
                }
            }

            @Override
            public void onAfter(int id) {
                if (!isShowing()) {
                    return;
                }
                rewardLl.setVisibility(View.VISIBLE);
            }

        });
    }

    public void sendOk(GiftBean giftBean, int gold) {

    }

    /**
     * 获取我的约豆余额
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