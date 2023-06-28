package com.lovechatapp.chat.im;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.tencent.qcloud.tim.uikit.modules.chat.GroupChatManagerKit;

/**
 * Im聊天室
 */
public class ChatGroupActivity extends BaseActivity {

    private Fragment mChatFragment;

    private boolean isOnStart;

    @Override
    protected void onNewIntent(Intent intent) {
        if (!isOnStart)
            return;
        super.onNewIntent(intent);
        setIntent(intent);
        toChat();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isOnStart = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GroupChatManagerKit.getInstance().setChatting(false);
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.chat_activity);
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        toChat();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (mChatFragment != null) {
            mChatFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void toChat() {
        GroupChatManagerKit.getInstance().setChatting(true);
        mChatFragment = new ChatGroupFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.empty_view, mChatFragment).commitAllowingStateLoss();
    }

}