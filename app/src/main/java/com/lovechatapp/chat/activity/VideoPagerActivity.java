package com.lovechatapp.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lovechatapp.chat.dialog.LookResourceDialog;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.pili.pldroid.player.PLOnPreparedListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.VideoPagerAdapter;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.AlbumBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.PageRequester;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.view.recycle.OnViewPagerListener;
import com.lovechatapp.chat.view.recycle.ViewPagerLayoutManager;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class VideoPagerActivity extends BaseActivity {

    @BindView(R.id.rv)
    RecyclerView rv;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private VideoPagerAdapter videoPagerAdapter;
    private int defaultIndex, currentIndex;
    private VideoPagerAdapter.VideoPagerHolder videoPagerHolder;
    private PageRequester<AlbumBean> requester;
    private boolean noMoreData;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onPause() {
        super.onPause();
        pauseVideoView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoPagerHolder != null)
            videoPagerHolder.videoView.stopPlayback();
    }

    public static void start(Context context, List<AlbumBean> data, int index, int page, int queryType) {
        Intent starter = new Intent(context, VideoPagerActivity.class);
        starter.putExtra("data", JSON.toJSONString(data));
        starter.putExtra("index", index);
        starter.putExtra("queryType", queryType + "");
        starter.putExtra("page", page);
        context.startActivity(starter);
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_video_pager);
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置音量键控制媒体音量
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        defaultIndex = getIntent().getIntExtra("index", 0);
        currentIndex = defaultIndex;
        initRecycle();
    }

    /**
     * 释放videoView
     */
    private void pauseVideoView() {
        if (videoPagerHolder != null && !isFinishing()) {
            videoPagerHolder.videoView.pause();
            videoPagerHolder.videoView.setTag(true);
            videoPagerHolder.mPauseIv.setVisibility(View.VISIBLE);
        }
    }

    private void initRecycle() {
        ViewPagerLayoutManager mLayoutManager = new ViewPagerLayoutManager(this, OrientationHelper.VERTICAL);
        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {
                AlbumBean bean = videoPagerAdapter.getItem(defaultIndex);
                playVideo(bean);
            }

            @Override
            public void onPageRelease(View view) {
                VideoPagerAdapter.VideoPagerHolder videoPagerHolder = (VideoPagerAdapter.VideoPagerHolder) rv.getChildViewHolder(view);
                if (videoPagerHolder != null) {
                    videoPagerAdapter.resetViewHolder(videoPagerHolder);
                }
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                AlbumBean bean = videoPagerAdapter.getItem(position);
                currentIndex = position;
                playVideo(bean);
                isBottom = videoPagerAdapter.getItemCount() - 1 - position <= 5;
                if (isBottom && !noMoreData && !requester.isRequesting()) {
                    requester.onLoadMore();
                }
            }
        });
        videoPagerAdapter = new VideoPagerAdapter(this) {
            @Override
            public void play() {
                playVideo(videoPagerAdapter.getItem(currentIndex));
            }

            @Override
            public void showLoadingDialog() {
                VideoPagerActivity.this.showLoadingDialog();
            }

            @Override
            public void dismissLoadingDialog() {
                VideoPagerActivity.this.dismissLoadingDialog();
            }
        };
        List<AlbumBean> data = JSON.parseObject(getIntent().getStringExtra("data"), new TypeReference<List<AlbumBean>>() {});
        videoPagerAdapter.setBeans(data, false);
        rv.setLayoutManager(mLayoutManager);
        rv.setAdapter(videoPagerAdapter);
        rv.scrollToPosition(defaultIndex);
        mLayoutManager.setPosition(defaultIndex);

        refreshLayout.setEnableOverScrollDrag(false);
        refreshLayout.setEnableOverScrollBounce(false);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(0);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!requester.isRequesting()) {
                    requester.onLoadMore();
                }
            }
        });

        requester = new PageRequester<AlbumBean>() {
            @Override
            public void onSuccess(List<AlbumBean> list, boolean isRefresh) {
                if (isFinishing()) {
                    return;
                }
                if (list != null) {
                    videoPagerAdapter.setBeans(list, false);
                }
            }
        };

        requester.setOnPageDataListener(new PageRequester.SimplePageDataListener() {

            @Override
            public void finishLoadMore(boolean noMore) {
                if (isFinishing()) {
                    return;
                }
                refreshLayout.finishLoadMore(0, true, noMore);
                noMoreData = noMore;
            }

        });

        requester.setApi(ChatApi.GET_VIDEO_LIST());
        requester.setParam("queryType", getIntent().getStringExtra("queryType"));
        requester.setPage(getIntent().getIntExtra("page", 1));

        requester.onLoadMore();
    }

    /**
     * 播放视频
     */
    private synchronized void playVideo(final AlbumBean bean) {
        handler.removeCallbacksAndMessages(null);
        if (videoPagerHolder != null) {
            videoPagerHolder.clickView.setOnClickListener(null);
            videoPagerHolder.videoView.stopPlayback();
        }

        final String url = bean.t_addres_url;
        if (!bean.canSee()) {
            bean.t_file_type = 1;
            LookResourceDialog.showAlbum(this, bean, bean.t_user_id, new OnCommonListener<Boolean>() {
                @Override
                public void execute(Boolean aBoolean) {
                    Log.e("支付是否成功","成功回调="+aBoolean);
                    if (aBoolean) {
                        bean.is_see = 1;
                        videoPagerAdapter.notifyDataSetChanged();
                        playVideo(bean);
                    }
                }
            });
            return;
        }


        VideoPagerAdapter.VideoPagerHolder holder = null;
        for (int i = 0; i < rv.getChildCount(); i++) {
            VideoPagerAdapter.VideoPagerHolder viewHolder =
                    (VideoPagerAdapter.VideoPagerHolder) rv.getChildViewHolder(rv.getChildAt(i));
            if (viewHolder.position == currentIndex) {
                holder = viewHolder;
                break;
            }
        }
        if (holder == null) {
            holder = (VideoPagerAdapter.VideoPagerHolder) rv.getChildViewHolder(rv.getChildAt(0));
        }
        videoPagerHolder = holder;

        holder.videoView.setVideoPath(url);
        holder.videoView.setLooping(true);
        holder.videoView.start();
        holder.videoView.setTag(false);
        holder.videoView.setOnPreparedListener(new PLOnPreparedListener() {
            @Override
            public void onPrepared(int i) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
                alphaAnimation.setDuration(300);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        videoPagerHolder.coverImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                videoPagerHolder.coverImage.startAnimation(alphaAnimation);
            }
        });
        holder.clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoPagerHolder.videoView.getTag() == null) {
                    playVideo(bean);
                    return;
                }
                if (videoPagerHolder.videoView.isPlaying() && !isPause()) {
                    videoPagerHolder.videoView.pause();
                    videoPagerHolder.mPauseIv.setVisibility(View.VISIBLE);
                    videoPagerHolder.videoView.setTag(true);
                } else {
                    videoPagerHolder.videoView.start();
                    videoPagerHolder.mPauseIv.setVisibility(View.GONE);
                    videoPagerHolder.videoView.setTag(false);
                }
            }
        });
        addSeeTime(bean.t_id);
    }

    private boolean isPause() {
        return videoPagerHolder.videoView.getTag() != null && (Boolean) videoPagerHolder.videoView.getTag();
    }

    @OnClick({R.id.back_iv,
            R.id.complain_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.complain_iv:
                showComplainPopup(view);
                break;
        }
    }

    /**
     * 新增查看次数
     */
    private void addSeeTime(int fileId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("fileId", String.valueOf(fileId));
        OkHttpUtils.post().url(ChatApi.ADD_QUERY_DYNAMIC_COUNT())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {

            }
        });
    }

    /**
     * popup投诉举报
     */
    private void showComplainPopup(View view) {

        View contentView = LayoutInflater
                .from(this)
                .inflate(R.layout.popup_complain_layout, null, false);

        final PopupWindow window = new PopupWindow(contentView,
                DevicesUtil.dp2px(getApplicationContext(), 81),
                DevicesUtil.dp2px(getApplicationContext(), 81),
                true);

        TextView complainTv = contentView.findViewById(R.id.complain_tv);
        complainTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                intent.putExtra(Constant.ACTOR_ID, videoPagerAdapter.getItem(currentIndex).t_id);
                startActivity(intent);
                window.dismiss();
            }
        });
        TextView reportTv = contentView.findViewById(R.id.report_tv);
        reportTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                intent.putExtra(Constant.ACTOR_ID, videoPagerAdapter.getItem(currentIndex).t_id);
                startActivity(intent);
                window.dismiss();
            }
        });
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        window.showAsDropDown(view, -123, 3);
    }
}