package com.lovechatapp.chat.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.ImCustomMessage;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.ViewHolder;

/**
 * 多人聊天消息adapter
 */
public class MultipleMessageRecyclerAdapter extends AbsRecycleAdapter {

    private BaseActivity mContext;

    /**
     * 绿色字体
     */
    private final int nickNameColor = 0xff31DF9B;

    /**
     * 红色字体
     */
    private final int giftNickNameColor = 0xffFE70B6;

    /**
     * 黄色字体
     */
    private final int operateColor = 0xffEAFF00;

    /**
     * 白色字体
     */
    private final int whiteColor = 0xffffffff;

    public MultipleMessageRecyclerAdapter(BaseActivity context) {
        super(new Type(R.layout.item_multiple_message, MessageInfo.class));
        mContext = context;
    }

    public final void add(MessageInfo message, RecyclerView recyclerView) {
        addData(message, false);
        notifyItemInserted(getLastPosition());
        recyclerView.scrollToPosition(getLastPosition());
    }

    private int getLastPosition() {
        return getItemCount() - 1;
    }

    @Override
    public void convert(ViewHolder holder, Object t) {

        final MessageInfo bean = (MessageInfo) t;

        TextView textView = holder.getView(R.id.content_tv);
        textView.setTextColor(whiteColor);
        textView.setText(null);

        holder.<ImageView>getView(R.id.content_iv).setImageResource(0);

        int senderUserId = -1;
        try {
            senderUserId = Integer.parseInt(bean.getFromUser()) - 10000;
        } catch (Exception e) {
            e.printStackTrace();
        }

        String nickName = getNameText(senderUserId, bean.getTIMMessage() != null ?
                bean.getTIMMessage().getSenderNickname() : bean.getFromUser());

        if (bean.getExtra() != null && bean.getExtra() instanceof ImCustomMessage) {

            ImCustomMessage customMessage = (ImCustomMessage) bean.getExtra();

            String otherName = getNameText(customMessage.otherId, customMessage.otherName);

            //礼物消息
            if (ImCustomMessage.Type_gift.equals(customMessage.type)) {
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(nickName, new ForegroundColorSpan(giftNickNameColor), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append("送给", new ForegroundColorSpan(whiteColor), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append(otherName, new ForegroundColorSpan(giftNickNameColor), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append(String.format("一个%s", customMessage.gift_name), new ForegroundColorSpan(whiteColor), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(builder);
                Glide.with(mContext).load(customMessage.gift_still_url).into(holder.<ImageView>getView(R.id.content_iv));
            }

            //移出房间消息
            else if (ImCustomMessage.Type_kickUser.equals(customMessage.type)) {
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(nickName, new ForegroundColorSpan(operateColor), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append("已将", new ForegroundColorSpan(operateColor), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append(otherName, new ForegroundColorSpan(nickNameColor), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append("移出房间", new ForegroundColorSpan(operateColor), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(builder);
            }

            //进入房间消息
            else if (ImCustomMessage.Type_joined.equals(customMessage.type)) {
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(nickName, new ForegroundColorSpan(nickNameColor), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append("进入房间", new ForegroundColorSpan(whiteColor), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(builder);
            }

            //离开房间消息
            else if (ImCustomMessage.Type_leaved.equals(customMessage.type)) {
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(nickName, new ForegroundColorSpan(nickNameColor), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append("离开了房间", new ForegroundColorSpan(whiteColor), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(builder);
            }
        }

        //文本消息
        else {
            if (bean.getExtra() != null) {
                nickName += ": ";
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(nickName, new ForegroundColorSpan(nickNameColor), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append(bean.getExtra().toString(), new ForegroundColorSpan(whiteColor), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(builder);
            }
        }
    }

    private String getNameText(int t_id, String nickName) {
        if (nickName == null) {
            nickName = "";
        }
        return t_id == AppManager.getInstance().getUserInfo().t_id ? "我" : nickName;
    }

}