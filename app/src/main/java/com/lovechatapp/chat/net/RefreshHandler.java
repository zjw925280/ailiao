package com.lovechatapp.chat.net;


import android.support.annotation.NonNull;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

public class RefreshHandler implements OnLoadMoreListener, OnRefreshListener {

    private PageRequester requester;

    public RefreshHandler(PageRequester requester) {
        this.requester = requester;
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        requester.onLoadMore();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requester.onRefresh();
    }
}