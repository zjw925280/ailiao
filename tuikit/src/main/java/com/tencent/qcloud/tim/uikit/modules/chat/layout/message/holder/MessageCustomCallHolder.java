package com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder;

import android.view.View;
import android.widget.TextView;

import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.ImCustomMessage;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;

/**
 * 自定义通话item
 */
public class MessageCustomCallHolder extends MessageContentHolder {

    private TextView textView;

    public MessageCustomCallHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getVariableLayout() {
        return R.layout.item_custom_call_info;
    }

    @Override
    public void initVariableViews() {
        textView = itemView.findViewById(R.id.custom_tv);
    }

    @Override
    public void layoutVariableViews(MessageInfo msg, int position) {
        ImCustomMessage customMessageBean = (ImCustomMessage) msg.getExtra();
        int drawId = customMessageBean.isVideo() ? R.drawable.im_selector_call_video : R.drawable.im_selector_call_audio;
        if (msg.isSelf()) {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawId, 0);
            if (properties.getRightChatContentFontColor() != 0) {
                textView.setTextColor(properties.getRightChatContentFontColor());
            }
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(drawId, 0, 0, 0);
            if (properties.getLeftChatContentFontColor() != 0) {
                textView.setTextColor(properties.getLeftChatContentFontColor());
            }
        }
        textView.setSelected(msg.isSelf());
        if (ImCustomMessage.Type_video_connect.equals(customMessageBean.type)) {
            textView.setText(customMessageBean.getVideoTime());
        } else {
            textView.setText("未接听");
        }
    }
}