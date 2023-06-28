package com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.ImCustomMessage;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;

/**
 * 自定义礼物item
 */
public class MessageCustomGiftHolder extends MessageContentHolder {

    private ImageView giftIv;
    private TextView giftNameTv;
    private TextView goldTv;
    private boolean isSelf;

    public MessageCustomGiftHolder(View itemView, boolean isSelf) {
        super(itemView);
        this.isSelf = isSelf;
    }

    @Override
    public int getVariableLayout() {
        return isSelf ? R.layout.item_custom_self_gift : R.layout.item_custom_other_gift;
    }

    @Override
    public void initVariableViews() {
        giftIv = itemView.findViewById(R.id.gift_iv);
        giftNameTv = itemView.findViewById(R.id.gift_name_tv);
        goldTv = itemView.findViewById(R.id.gold_tv);
    }

    @Override
    public void layoutVariableViews(MessageInfo msg, int position) {
        ImCustomMessage customMessageBean = (ImCustomMessage) msg.getExtra();
        //金币
        if (customMessageBean.type.equals("0")) {
            giftIv.setImageResource(R.drawable.im_ic_gold);
            String giftDes = "金币x1";
            giftNameTv.setText(giftDes);
        }
        //礼物
        else {
            if (customMessageBean.gift_number == 0)
                customMessageBean.gift_number = 1;
            GlideEngine.loadCornerImage(giftIv, customMessageBean.gift_still_url, null, 0);
            String giftDes = customMessageBean.gift_name + "x" + customMessageBean.gift_number;
            giftNameTv.setText(giftDes);
        }
        goldTv.setText(String.valueOf(customMessageBean.gold_number));
    }
}