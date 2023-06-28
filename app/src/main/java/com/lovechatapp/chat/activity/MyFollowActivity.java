package com.lovechatapp.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;

/**
 * 谁喜欢我/我喜欢谁/相互喜欢
 */
public class MyFollowActivity extends BaseActivity {

    /**
     * type 0谁喜欢我 1我喜欢谁 2互相喜欢
     */
    public static void start(Context context, String title, int type) {
        Intent starter = new Intent(context, MyFollowActivity.class);
        starter.putExtra("title", title);
        starter.putExtra("type", type);
        context.startActivity(starter);
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_follow);
    }

    @Override
    protected void onContentAdded() {
        setTitle(getIntent().getStringExtra("title"));
    }

}