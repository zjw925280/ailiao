package com.lovechatapp.chat.activity;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.ServeBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.glide.GlideCircleTransform;
import com.lovechatapp.chat.helper.IMHelper;
import com.lovechatapp.chat.net.PageRequester;
import com.lovechatapp.chat.net.RefreshHandler;
import com.lovechatapp.chat.net.RefreshPageListener;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.OnItemClickListener;
import com.lovechatapp.chat.view.recycle.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

import butterknife.BindView;

/**
 * 功能描述：客服列表
 */
public class ServeListActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.empty_tv)
    TextView mEmptyTv;

    private AbsRecycleAdapter adapter;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_serve_list);
    }

    @Override
    protected void onContentAdded() {
        setTitle("选择客服");

        mRefreshLayout.setEnableLoadMore(false);

        PageRequester<ServeBean> pageRequester = new PageRequester<ServeBean>() {
            @Override
            public void onSuccess(List<ServeBean> list, boolean isRefresh) {
                if (isFinishing()) {
                    return;
                }
                adapter.setData(list, isRefresh);
                mEmptyTv.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }
        };
        mRefreshLayout.setOnRefreshListener(new RefreshHandler(pageRequester));
        pageRequester.setApi(ChatApi.getServiceId());
        pageRequester.setOnPageDataListener(new RefreshPageListener(mRefreshLayout));

        adapter = new AbsRecycleAdapter(new AbsRecycleAdapter.Type(R.layout.item_serve, ServeBean.class)) {
            @Override
            public void convert(ViewHolder holder, Object t) {
                ServeBean bean = (ServeBean) t;
                Glide.with(mContext)
                        .load(bean.t_handImg)
                        .error(R.drawable.default_head)
                        .transform(new GlideCircleTransform(mContext))
                        .into(holder.<ImageView>getView(R.id.head_iv));
                holder.<TextView>getView(R.id.name_tv).setText(bean.t_nickName);
            }
        };

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object obj, int position) {
                ServeBean bean = (ServeBean) obj;
                IMHelper.toChatServe(mContext, bean.t_nickName, bean.t_id);
            }
        });

        mContentRv.setLayoutManager(new LinearLayoutManager(mContext));
        mContentRv.setAdapter(adapter);

        pageRequester.onRefresh();
    }

}