package com.lovechatapp.chat.util.share;

import android.app.Activity;
import android.content.Intent;

import com.lovechatapp.chat.activity.PromotionPosterActivity;

/**
 * 分享海报
 */
public class SharePoster implements IShare {

    @Override
    public void share(Activity activity) {
        Intent intent = new Intent(activity, PromotionPosterActivity.class);
        activity.startActivity(intent);
    }
}