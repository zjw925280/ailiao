package com.lovechatapp.chat.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.InfoVideoRecyclerAdapter;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.base.LazyFragment;
import com.lovechatapp.chat.bean.PageBean;
import com.lovechatapp.chat.bean.AlbumBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播资料页下方主播视频Fragment
 * 作者：
 * 创建时间：2018/6/21
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActorVideoFragment extends LazyFragment {


    public ActorVideoFragment() {

    }

    private int mActorId;
    private TextView mNoVideoTv;
    private InfoVideoRecyclerAdapter mAdapter;
    private boolean mHaveFirstVisible;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actor_video_layout,
                container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mNoVideoTv = view.findViewById(R.id.no_video_tv);
        RecyclerView mVideoRv = view.findViewById(R.id.video_rv);
        mVideoRv.setNestedScrollingEnabled(false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mVideoRv.setLayoutManager(gridLayoutManager);
        mAdapter = new InfoVideoRecyclerAdapter(getActivity());
        mVideoRv.setAdapter(mAdapter);
        mIsViewPrepared = true;
    }

    @Override
    protected void onFirstVisibleToUser() {
        mHaveFirstVisible = true;
        Bundle bundle = getArguments();
        if (bundle != null) {
            mActorId = bundle.getInt(Constant.ACTOR_ID);
            getActorVideo();
        }
        mIsDataLoadCompleted = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHaveFirstVisible && mActorId > 0) {
            getActorVideo();
        }
    }

    /**
     * 获取主播视频照片
     */
    private void getActorVideo() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id + "");
        paramMap.put("coverUserId", String.valueOf(mActorId));
        paramMap.put("page", String.valueOf(1));
        OkHttpUtils.post().url(ChatApi.GET_DYNAMIC_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<AlbumBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<AlbumBean>> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<AlbumBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<AlbumBean> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            int size = focusBeans.size();
                            if (size > 0) {
                                mAdapter.loadData(focusBeans, mActorId);
                                mNoVideoTv.setVisibility(View.GONE);
                            } else {
                                mNoVideoTv.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(getContext(), R.string.system_error);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getContext(), R.string.system_error);
            }

        });
    }


}
