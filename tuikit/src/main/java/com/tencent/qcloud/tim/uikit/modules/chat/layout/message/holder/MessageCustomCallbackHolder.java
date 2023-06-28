package com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder;

import android.view.View;
import android.widget.TextView;

import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.ImCustomMessage;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;

/**
 * 自定义通话item
 */
public class MessageCustomCallbackHolder extends MessageContentHolder {

    private TextView typeTv;
    private ImCustomMessage customMessageBean;

    public MessageCustomCallbackHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getVariableLayout() {
        return R.layout.item_custom_call;
    }

    @Override
    public void initVariableViews() {

        typeTv = itemView.findViewById(R.id.call_type_tv);

        //回拨
        itemView.findViewById(R.id.call_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customMessageBean != null) {
                    View view = itemView.getRootView().findViewById(R.id.btn_video);
                    view.setTag(customMessageBean.isVideo() ? ImCustomMessage.Call_Type_Video : ImCustomMessage.Call_Type_Audio);
                    view.performClick();
                }
            }
        });
    }

    @Override
    public void layoutVariableViews(MessageInfo msg, int position) {
        customMessageBean = (ImCustomMessage) msg.getExtra();
        typeTv.setSelected(customMessageBean.isVideo());
        typeTv.setText(String.format("发来一个%s邀请", customMessageBean.getCallTypeText()));
    }
}