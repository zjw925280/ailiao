package com.lovechatapp.chat.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.VideoRecyclerAdapter;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.AlbumBean;
import com.lovechatapp.chat.bean.PageBeanOne;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.util.ParamUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.mm.opensdk.utils.Log;
import com.zhy.http.okhttp.OkHttpUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;

public class VideoActivity extends BaseActivity {
    @BindView(R.id.rv_video)
    RecyclerView rv_video;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    private List<AlbumBean> dayNumList=new ArrayList<>();
    private VideoRecyclerAdapter videoRecyclerAdapter;
    private int otherId;
    private int page=1;
    @NotNull
    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_video);
    }

    @Override
    protected void onContentAdded() {
        setTitle("视频");
         otherId = getIntent().getIntExtra("userid",-1);
        initRecyclerView(this);
        getVideoList(page);
    }

    private void initRecyclerView(Context context) {



        //设置签到RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,3, LinearLayoutManager.VERTICAL,false);
        rv_video.setLayoutManager(gridLayoutManager);
        videoRecyclerAdapter = new VideoRecyclerAdapter(context);
        rv_video.setAdapter(videoRecyclerAdapter);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page=1;
                getVideoList(page);

            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page++;
                getVideoList(page);
            }
        });
    }

    /**
     * 获取主播视频照片
     */
    private void getVideoList(int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", String.valueOf(otherId));
        paramMap.put("page", String.valueOf(page));
        paramMap.put("queryType", String.valueOf(-1));
        OkHttpUtils.post().url(ChatApi.PESON_VIODE())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBeanOne<AlbumBean>>>() {
                    @Override
                    public void onResponse(BaseResponse<PageBeanOne<AlbumBean>> response, int id) {
                        Log.e("啥","啥玩意="+new Gson().toJson(response));
                        if (page==1){
                            dayNumList.clear();
                            dayNumList.addAll(response.m_object.data);
                            videoRecyclerAdapter.loadData(dayNumList);
                            refreshLayout.finishRefresh();
                        }else {
                            dayNumList.addAll(response.m_object.data);
                            videoRecyclerAdapter.loadData(dayNumList);
                            refreshLayout.finishLoadMore();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }
                });
    }
}
