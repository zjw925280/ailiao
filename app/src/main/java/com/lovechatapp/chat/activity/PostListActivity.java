package com.lovechatapp.chat.activity;

import android.content.Intent;
import android.view.View;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;

import butterknife.OnClick;

/**
 * 相册/视频/动态选项列表
 */
public class PostListActivity extends BaseActivity {

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_post_list);
    }

    @Override
    protected void onContentAdded() {
        setTitle("发布");

        //主播显示动态按钮
        findViewById(R.id.active_tv).setVisibility(
                AppManager.getInstance().getUserInfo().isWomenActor() ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.active_tv,
            R.id.video_tv,
            R.id.album_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.active_tv: {
                startActivity(new Intent(mContext, UserSelfActiveActivity.class));
                break;
            }
            case R.id.video_tv: {
                UserAlbumListActivity.start(mContext, "视频", 1);
                break;
            }
            case R.id.album_tv: {
                UserAlbumListActivity.start(mContext, "相册", 0);
                break;
            }
        }
    }

}