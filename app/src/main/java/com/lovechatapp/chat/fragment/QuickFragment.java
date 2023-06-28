package com.lovechatapp.chat.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.UserRecyclerAdapter;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.layoutmanager.ViewPagerLayoutManager;
import com.lovechatapp.chat.listener.OnViewPagerListener;
import com.lovechatapp.chat.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：速配页面Fragment
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class QuickFragment extends BaseFragment {

    public QuickFragment() {

    }

    private RecyclerView mUserRv;//用户看到的视频
    private UserRecyclerAdapter mAdapter;
    private ViewPagerLayoutManager mLayoutManager;

    @Override
    protected int initLayout() {
        return R.layout.fragment_quick_layout;
    }

    @Override
    protected void initView(View view,Bundle savedInstanceState) {
        mUserRv = view.findViewById(R.id.user_rv);
    }

    @Override
    protected void onFirstVisible() {
        LogUtil.i("==--", "速配页面第一次可以见");
        //initUserRecycler();
    }

    @Override
    protected void onFragmentNotVisible() {
        LogUtil.i("==--", "速配页面不可见");
    }

    /**
     * 设置recyclerView
     */
    private void initUserRecycler() {
        mLayoutManager = new ViewPagerLayoutManager(getActivity(), OrientationHelper.HORIZONTAL);
        mAdapter = new UserRecyclerAdapter(getActivity());
        mUserRv.setLayoutManager(mLayoutManager);
        mUserRv.setAdapter(mAdapter);
        List<String> beans = new ArrayList<>();
        beans.add("111");
        beans.add("1444");
        beans.add("222");
        beans.add("333");
        beans.add("223");
        mAdapter.loadData(beans);
        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {

            @Override
            public void onInitComplete() {
                LogUtil.i("==--", "onInitComplete");
                playVideo(0);
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                LogUtil.i("==--", "释放位置:" + position + " 下一页:" + isNext);
                int index;
                if (isNext) {
                    index = 0;
                } else {
                    index = 1;
                }
                releaseVideo(index);
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                LogUtil.i("==--", "选中位置:" + position + "  是否是滑动到底部:" + isBottom);
                playVideo(0);
            }
        });
    }

    private void playVideo(int position) {
        View itemView = mUserRv.getChildAt(0);
        final VideoView videoView = itemView.findViewById(R.id.video_view);
        //final ImageView imgPlay = itemView.findViewById(R.id.img_play);
        //final RelativeLayout rootView = itemView.findViewById(R.id.root_view);
        //final MediaPlayer[] mediaPlayer = new MediaPlayer[1];
        final ImageView imgThumb = itemView.findViewById(R.id.thumb_iv);
        videoView.start();
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                //mediaPlayer[0] = mp;
                mp.setLooping(true);
                imgThumb.animate().alpha(0).setDuration(200).start();
                return false;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

            }
        });


        /*imgPlay.setOnClickListener(new View.OnClickListener() {
            boolean isPlaying = true;

            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    imgPlay.animate().alpha(1f).start();
                    videoView.pause();
                    isPlaying = false;
                } else {
                    imgPlay.animate().alpha(0f).start();
                    videoView.start();
                    isPlaying = true;
                }
            }
        });*/
    }

    private void releaseVideo(int index) {
        View itemView = mUserRv.getChildAt(index);
        final VideoView videoView = itemView.findViewById(R.id.video_view);
        final ImageView imgThumb = itemView.findViewById(R.id.thumb_iv);
        //final ImageView imgPlay = itemView.findViewById(R.id.img_play);
        videoView.stopPlayback();
        imgThumb.animate().alpha(1).start();
        //imgPlay.animate().alpha(0f).start();
    }

}
