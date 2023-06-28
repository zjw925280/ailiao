package com.lovechatapp.chat.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjt2325.cameralibrary.util.ScreenUtils;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.AudioChatActivity;
import com.lovechatapp.chat.activity.PersonInfoActivity;
import com.lovechatapp.chat.activity.PhoneVerifyActivity;
import com.lovechatapp.chat.activity.VideoChatActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.socket.domain.ReceiveFloatingBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 悬浮礼物动画管理器
 */
public class FloatingManagerOne {

    static final List<String> filterClass = Collections.singletonList(
            PhoneVerifyActivity.class.getName()
    );

    /**
     * 飘屏队列
     */
    private static final List<ReceiveFloatingBean> receiveGiftBeans = new ArrayList<>();

    /**
     * 是否动画进行中
     */
    private static boolean isAnimating = false;

    public static void clear() {
        receiveGiftBeans.clear();
    }

    public static void receiveGift(Activity showingActivity, ReceiveFloatingBean receiveGiftBean) {
        if (showingActivity == null) {
            return;
        }
        if (filterClass.contains(showingActivity.getClass().getName())) {
            return;
        }
        receiveGiftBeans.add(receiveGiftBean);
        if (!isAnimating) {
            startAnim(showingActivity, receiveGiftBean);
        }
    }

    private static void startAnim(final Activity showingActivity, final ReceiveFloatingBean receiveGiftBean) {
        if (showingActivity == null) {
            return;
        }

        final ViewGroup view = showingActivity.findViewById(R.id.float_gift_fl);
        if (view == null) {
            return;
        }
        Log.d("FloatingView", "startAnim: ");
        view.setVisibility(View.VISIBLE);
        isAnimating = true;
        //绑定数据
        View animView = view.getChildAt(0);
        View bgView = view.findViewById(R.id.gift_bg_view);
        TextView senderTv = view.findViewById(R.id.user_tv);
        TextView receiverTv = view.findViewById(R.id.receiver_tv);
        TextView centerTv = view.findViewById(R.id.center_tv);
        ImageView typeIv = view.findViewById(R.id.right_iv);
        typeIv.setVisibility(View.INVISIBLE);
        ImageView headIv = view.findViewById(R.id.gift_head_iv);
        ImageView giftIv = view.findViewById(R.id.gift_iv);
        ImageLoadHelper.glideShowCircleImageWithUrl(headIv.getContext(), receiveGiftBean.t_handImg, headIv, 60, 60);
        senderTv.setText(receiveGiftBean.t_nickName);

        //点击事件
        animView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VideoChatActivity.isChatting || AudioChatActivity.isChatting)
                    return;
                int t_role = 0;
                int id = 0;
                if (receiveGiftBean.t_left_id != 0
                        && receiveGiftBean.t_left_sex != AppManager.getInstance().getUserInfo().t_sex) {
                    t_role = receiveGiftBean.t_left_role;
                    id = receiveGiftBean.t_left_id;
                } else if (receiveGiftBean.t_right_id != 0
                        && receiveGiftBean.t_right_sex != AppManager.getInstance().getUserInfo().t_sex) {
                    t_role = receiveGiftBean.t_right_role;
                    id = receiveGiftBean.t_right_id;
                }
                if (id != 0) {
                    PersonInfoActivity.start(showingActivity, id);
                }
            }
        });

        // 开通会员时 1
        // 当用户充值 2
        // 送礼物 3
        switch (receiveGiftBean.sendType) {
            case 1:
                bgView.setBackgroundResource(R.drawable.floating_vip_bg);
                typeIv.setImageResource(R.drawable.floating_vip);
                typeIv.setVisibility(View.VISIBLE);
                receiverTv.setText(R.string.floating_vip);
                centerTv.setText("|");
                centerTv.setTextColor(Color.WHITE);
                giftIv.setVisibility(View.GONE);
                break;
            case 2:
                bgView.setBackgroundResource(R.drawable.floating_charge_bg);
                typeIv.setImageResource(R.drawable.floating_gold);
                typeIv.setVisibility(View.VISIBLE);
                String content = receiverTv.getContext().getString(R.string.floating_charge);
                receiverTv.setText(String.format(content, receiveGiftBean.recharge + ""));
                centerTv.setText("|");
                centerTv.setTextColor(Color.WHITE);
                giftIv.setVisibility(View.GONE);
                break;
            case 3:
                bgView.setBackgroundResource(R.drawable.floating_gift_bg);
                centerTv.setText(R.string.floating_send);
                centerTv.setTextColor(centerTv.getResources().getColor(R.color.yellow_f9fb44));
                receiverTv.setText(receiveGiftBean.t_cover_nickName);
                giftIv.setVisibility(View.VISIBLE);
                ImageLoadHelper.glideShowImageWithUrl(giftIv.getContext(), receiveGiftBean.t_gift_still_url, giftIv, 100, 100);
                break;
        }

        //启动动画
        int screenWidth = ScreenUtils.getScreenWidth(AppManager.getInstance());

        ObjectAnimator enter = ObjectAnimator.ofFloat(animView, "translationX", screenWidth, 0);
        enter.setDuration(500);

        ObjectAnimator stay = ObjectAnimator.ofFloat(animView, "translationX", 0, 0);
        stay.setDuration(10000);

        ObjectAnimator exit = ObjectAnimator.ofFloat(animView, "translationX", 0, -screenWidth);
        exit.setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(enter, stay, exit);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                receiveGiftBeans.remove(receiveGiftBean);
                isAnimating = false;
                if (receiveGiftBeans.size() > 0) {
                    startAnim(showingActivity, receiveGiftBeans.get(0));
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }
}