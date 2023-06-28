package com.lovechatapp.chat.net;

import com.scwang.smartrefresh.layout.api.RefreshLayout;

public class RefreshPageListener implements PageRequester.onPageDataListener {

    private RefreshLayout refreshLayout;

    public RefreshPageListener(RefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
    }

    @Override
    public void autoRefresh() {
        if (refreshLayout != null)
            refreshLayout.autoRefresh();
    }

    @Override
    public void onRefreshEnd() {
        if (refreshLayout != null)
            refreshLayout.finishRefresh();
    }

    @Override
    public void finishLoadMore(boolean noMore) {
        if (refreshLayout != null) {
            refreshLayout.finishLoadMore(310, true, noMore);
        }
    }

}