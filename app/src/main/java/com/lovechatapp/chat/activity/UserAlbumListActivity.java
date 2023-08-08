package com.lovechatapp.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.UserAlbumListRecyclerAdapter;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.AlbumResponseBean;
import com.lovechatapp.chat.bean.AlbumBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Request;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：我的相册
 * 作者：
 * 创建时间：2018/10/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UserAlbumListActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private int mCurrentPage = 1;
    private List<AlbumBean> mFocusBeans = new ArrayList<>();
    private UserAlbumListRecyclerAdapter mAdapter;

    /**
     * 查询相册类型 -1=全部 0=图片 1=视频
     */
    private int type = -1;

    public static void start(Context context, String title, int type) {
        Intent starter = new Intent(context, UserAlbumListActivity.class);
        starter.putExtra("title", title);
        starter.putExtra("type", type);
        context.startActivity(starter);
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_user_album_list_layout);
    }

    @Override
    protected void onContentAdded() {
        initStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAlbumList(mRefreshLayout, true, 1);
    }

    /**
     * 初始化
     */
    private void initStart() {

        type = getIntent().getIntExtra("type", type);

        if (!TextUtils.isEmpty(getIntent().getStringExtra("title"))) {
            setTitle(getIntent().getStringExtra("title"));
        } else {
            setTitle(getString(R.string.album));
        }

        mRightTv.setBackgroundResource(R.drawable.active_camera);
        mRightTv.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRightTv.getLayoutParams();
        params.rightMargin = DevicesUtil.dp2px(getApplicationContext(), 15);
        mRightTv.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
            intent.putExtra("type", type);
            startActivity(intent);
        });

        initRecycler();
    }

    /**
     * 获取个人浏览记录
     */
    private void getAlbumList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("page", page);
        if (type >= 0) {
            paramMap.put("type", type);
        }
        OkHttpUtils.post().url(ChatApi.GET_MY_ANNUAL_ALBUM())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<AlbumResponseBean<AlbumBean>>>() {
            @Override
            public void onResponse(BaseResponse<AlbumResponseBean<AlbumBean>> response, int id) {
                if (isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    AlbumResponseBean<AlbumBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        //列表
                        List<AlbumBean> focusBeans = pageBean.data;
                        if (focusBeans != null) {
                            int size = focusBeans.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mFocusBeans.clear();
                                mFocusBeans.addAll(focusBeans);
                                mAdapter.loadData(mFocusBeans);
                                if (mFocusBeans.size() > 0) {
                                    mRefreshLayout.setEnableRefresh(true);
                                }
                                refreshlayout.finishRefresh();
                                if (size >= 10) {//如果是刷新,且返回的数据大于等于10条,就可以load more
                                    refreshlayout.setEnableLoadMore(true);
                                }
                            } else {
                                mCurrentPage++;
                                mFocusBeans.addAll(focusBeans);
                                mAdapter.loadData(mFocusBeans);
                                if (size >= 10) {
                                    refreshlayout.finishLoadMore();
                                }
                            }
                            if (size < 10) {//如果数据返回少于10了,那么说明就没数据了
                                refreshlayout.finishLoadMoreWithNoMoreData();
                            }
                        }
                    } else {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
                        if (isRefresh) {
                            refreshlayout.finishRefresh();
                        } else {
                            refreshlayout.finishLoadMore();
                        }
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
                    if (isRefresh) {
                        refreshlayout.finishRefresh();
                    } else {
                        refreshlayout.finishLoadMore();
                    }
                }
            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecycler() {
        mRefreshLayout.setOnRefreshListener(refreshLayout -> getAlbumList(refreshLayout, true, 1));
        mRefreshLayout.setOnLoadMoreListener(refreshLayout -> getAlbumList(refreshLayout, false, mCurrentPage + 1));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new UserAlbumListRecyclerAdapter(this){

            @Override
            protected void delete(AlbumBean bean) {
                deletePhoto(bean);
            }

            @Override
            protected void setCoverVideo(AlbumBean bean) {
                setVideoCover(bean);
            }
        };
        mContentRv.setAdapter(mAdapter);
    }

    /**
     * 删除照片/视频
     */
    private void deletePhoto(AlbumBean bean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("photoId", String.valueOf(bean.t_id));
        OkHttpUtils.post().url(ChatApi.DEL_MY_PHOTO())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.success_str))) {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), message);
                        getAlbumList(mRefreshLayout, true, 1);
                    }
                } else {
                    ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.delete_fail);
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                dismissLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }

    /**
     * 设置封面视频
     */
    private void setVideoCover(AlbumBean bean) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("albumId", bean.t_id);
        OkHttpUtils.post().url(ChatApi.setFirstAlbum())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    String message = response.m_strMessage;
                    if (!TextUtils.isEmpty(message) && message.contains(getResources().getString(R.string.success_str))) {
                        ToastUtil.INSTANCE.showToast(getApplicationContext(), message);
                        getAlbumList(mRefreshLayout, true, 1);
                    }
                } else {
                    ToastUtil.INSTANCE.showToast("设置失败");
                }
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                showLoadingDialog();
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                dismissLoadingDialog();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
            }
        });
    }
}