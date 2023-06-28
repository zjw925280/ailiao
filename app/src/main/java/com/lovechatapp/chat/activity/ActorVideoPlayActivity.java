package com.lovechatapp.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.util.ToastUtil;

/**
 * 动态视频播放页  & 个人相册视频播放页
 */
public class ActorVideoPlayActivity extends BaseActivity {

    public static void start(Context context, int actorId, String url) {

        if (actorId == 0) {
            ToastUtil.INSTANCE.showToast("无效ID");
            return;
        }

        if (TextUtils.isEmpty(url)) {
            ToastUtil.INSTANCE.showToast("无效地址");
            return;
        }

        Intent intent = new Intent(context, ActorVideoPlayActivity.class);
        intent.putExtra(Constant.VIDEO_URL, url);
        intent.putExtra(Constant.ACTOR_ID, actorId);
        context.startActivity(intent);
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_actor_video_play_layout);
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
    }
}