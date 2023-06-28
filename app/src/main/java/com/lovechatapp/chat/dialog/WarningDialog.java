package com.lovechatapp.chat.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;

/**
 * 提醒dialog
 */
public class WarningDialog extends Dialog implements View.OnClickListener {

    public WarningDialog(@NonNull Activity context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_warning);

        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setGravity(Gravity.CENTER);
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        win.setAttributes(lp);

        setCanceledOnTouchOutside(true);
        setCancelable(true);

        TextView titleTv = findViewById(R.id.title_tv);
        titleTv.setText(String.format("%s官方", getContext().getString(R.string.app_name)));

        TextView contentTv = findViewById(R.id.info_tv);
        String message = "女神您好,欢迎加入爱聊交友!" +
                "请不要使用过外挂脚本对男用户实行轰炸式拨打视频和发消息," +
                "这样会让男用户对您造成反感," +
                "而不但不接听您的视频还会把您加入黑名单!" +
                "请规范的手动发一些正规的招呼和情感话题以此来吸引男用户," +
                "效果会更好哦!" +
                "请不要频繁的发送露骨的招呼,爱聊平台祝您月入万元以上!";
        if (AppManager.getInstance().getUserInfo().isSexMan()) {
            message = "男神你好，本平台是绿色交友平台，倡导文明健康的聊天内容，" +
                    "如你在使用过程中发现有人利用本平台对你发送违规信息，请向客服投诉。" +
                    "特别提醒：任何以【可线下约会见面】为由要求打赏礼物或者添加微信、QQ等第三方工具发红包的均是骗子。" +
                    "严禁未成年人使用本平台，如果你还未满十八周岁，请主动卸载本软件。";
            String warning = "特别提醒：任何以【可线下约会见面】为由要求打赏礼物或者添加微信、QQ等第三方工具发红包的均是骗子。";
            int start = message.indexOf(warning);
            SpannableString spannableString = new SpannableString(message);
            spannableString.setSpan(
                    new ForegroundColorSpan(Color.RED),
                    start, start + warning.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            contentTv.setText(spannableString);
        } else {
            contentTv.setText(message);
        }
        findViewById(R.id.confirm_tv).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}