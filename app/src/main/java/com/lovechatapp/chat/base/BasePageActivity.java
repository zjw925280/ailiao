package com.lovechatapp.chat.base;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.net.PageRequester;
import com.lovechatapp.chat.net.RefreshHandler;
import com.lovechatapp.chat.net.RefreshPageListener;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;

import java.util.List;

import butterknife.BindView;

public abstract class BasePageActivity<T> extends BaseActivity {

    protected PageRequester requester;

    @BindView(R.id.content_rv)
    protected RecyclerView mContentRv;

    @BindView(R.id.refreshLayout)
    protected SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.empty_tv)
    protected TextView emptyTv;

    protected RecyclerView.Adapter adapter;

    public abstract PageRequester createRequester();

    public abstract RecyclerView.Adapter createAdapter();

    public abstract String getApi();

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_base_page);
    }

    @Override
    protected void onContentAdded() {
        initRequester();
        initRecycler();
        if (emptyTv != null) {
            emptyTv.setText(getEmptyText());
            emptyTv.setVisibility(View.GONE);
        }
    }

    protected void initRequester() {
        requester = createRequester();
        requester.setApi(getApi());
        requester.setOnPageDataListener(new RefreshPageListener(mRefreshLayout));
    }

    protected void initRecycler() {
        RefreshHandler refreshHandler = new RefreshHandler(requester);
        mRefreshLayout.setOnRefreshListener(refreshHandler);
        mRefreshLayout.setOnLoadMoreListener(refreshHandler);
        mContentRv.getItemAnimator().setChangeDuration(0);
        mContentRv.setLayoutManager(getLayoutManager());
        adapter = createAdapter();
        mContentRv.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (emptyTv != null)
                    emptyTv.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }
        });
    }

    protected void handleList(List<T> list, boolean isRefresh) {
        if (adapter instanceof AbsRecycleAdapter) {
            ((AbsRecycleAdapter) adapter).setData(list, isRefresh);
        }
    }

    public AbsRecycleAdapter getAbsAdapter() {
        return (AbsRecycleAdapter) adapter;
    }

    protected String getEmptyText() {
        return "";
    }

    protected void refresh() {
        requester.onRefresh();
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this);
    }
}