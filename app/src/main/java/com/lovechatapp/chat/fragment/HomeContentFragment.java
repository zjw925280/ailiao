package com.lovechatapp.chat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.lovechatapp.chat.util.LogUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.PersonInfoActivity;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.bean.GirlListBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.glide.GlideRoundTransform;
import com.lovechatapp.chat.net.PageRequester;
import com.lovechatapp.chat.net.RefreshHandler;
import com.lovechatapp.chat.net.RefreshPageListener;
import com.lovechatapp.chat.view.recycle.ListTypeAdapter;
import com.lovechatapp.chat.view.recycle.RecycleGridDivider;
import com.lovechatapp.chat.view.recycle.ViewHolder;

import java.util.List;

/**
 * 主播列表页
 */
public class HomeContentFragment extends BaseFragment {

    protected PageRequester<GirlListBean> requester;

    protected SmartRefreshLayout mRefreshLayout;

    protected ListTypeAdapter adapter;

    protected ListTypeAdapter.BindViewHolder header;

    protected ListTypeAdapter.BindViewHolder content;

    private int[] drawIds = {
            R.drawable.shape_free_indicator,
            R.drawable.shape_busy_indicator,
            R.drawable.shape_offline_indicator
    };

    @Override
    protected int initLayout() {
        return R.layout.fragment_home_content;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRefreshLayout = findViewById(R.id.refreshLayout);

        final RecyclerView mContentRv = findViewById(R.id.content_rv);

        mContentRv.setNestedScrollingEnabled(false);
        mContentRv.addItemDecoration(new RecycleGridDivider((int) getActivity().getResources().getDimension(R.dimen.item_space)));

        header = createHeader();
        content = new ListTypeAdapter.BindViewHolder(R.layout.item_girl_recycler_grid_layout, true) {

            @Override
            public ViewHolder createViewHolder(ViewGroup parent, int layoutId) {

                View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
                ViewHolder viewHolder =
                        new ViewHolder(view) {

                            @Override
                            public void convert(ViewHolder holder, Object t) {

                                if (getActivity() == null)
                                    return;

                                GirlListBean bean = (GirlListBean) t;

                                //昵称
                                holder.<TextView>getView(R.id.name_tv).setText(bean.t_nickName);

//                                //在线状态(0.空闲1.忙碌2.离线)
//                                int state = bean.t_state;
//                                if (state == 0) {//在线
//                                    holder.<TextView>getView(R.id.status_tv).setText(R.string.free);
//                                    holder.<TextView>getView(R.id.status_tv).setTextColor(mContext.getResources().getColor(R.color.white));
//                                    holder.<TextView>getView(R.id.status_tv).setCompoundDrawablesWithIntrinsicBounds(R.drawable.shape_free_indicator, 0, 0, 0);
//                                } else if (state == 1) {//忙碌
//                                    holder.<TextView>getView(R.id.status_tv).setText(R.string.busy);
//                                    holder.<TextView>getView(R.id.status_tv).setTextColor(mContext.getResources().getColor(R.color.white));
//                                    holder.<TextView>getView(R.id.status_tv).setCompoundDrawablesWithIntrinsicBounds(R.drawable.shape_busy_indicator, 0, 0, 0);
//                                } else if (state == 2) {
//                                    holder.<TextView>getView(R.id.status_tv).setText(R.string.offline);
//                                    holder.<TextView>getView(R.id.status_tv).setTextColor(mContext.getResources().getColor(R.color.gray_bcbcbc));
//                                    holder.<TextView>getView(R.id.status_tv).setCompoundDrawablesWithIntrinsicBounds(R.drawable.shape_offline_indicator, 0, 0, 0);
//                                }

                                //在线状态(0.空闲1.忙碌2.离线)
                                holder.<ImageView>getView(R.id.status_iv).setImageResource(drawIds[bean.t_state]);

                                //城市
                                holder.<TextView>getView(R.id.city_tv).setText(bean.t_city);

                                //显示封面图
                                Glide.with(getActivity())
                                        .load(bean.t_cover_img)
                                        .error(R.drawable.default_head_img)
                                        .transform(new CenterCrop(), new GlideRoundTransform(6))
                                        .into(holder.<ImageView>getView(R.id.head_iv));

                                //视频聊天价格（约豆）
                                holder.<TextView>getView(R.id.price_tv).setText(String.format("%s岁", bean.t_age));
                            }
                        };

                viewHolder.setOnItemClickListener((view1, object, position) -> {
                    GirlListBean bean = (GirlListBean) object;
                    PersonInfoActivity.start(mContext, bean.t_id);
                });

                return viewHolder;
            }
        };

        adapter = new ListTypeAdapter(header, content);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                return i == 0 && header != null ? gridLayoutManager.getSpanCount() : 1;
            }
        });

        mContentRv.setLayoutManager(gridLayoutManager);
        mContentRv.setAdapter(adapter);

        requester = new PageRequester<GirlListBean>() {
            @Override
            public void onSuccess(List<GirlListBean> list, boolean isRefresh) {
                content.setData(list, isRefresh);
            }
        };
        requester.setApi(ChatApi.GET_HOME_PAGE_LIST());
        if (getArguments() != null) {
            requester.setParam("queryType", getArguments().getString("queryType"));
        }
        requester.setOnPageDataListener(new RefreshPageListener(mRefreshLayout));

        mRefreshLayout.setOnRefreshListener(new RefreshHandler(requester));
        mRefreshLayout.setOnLoadMoreListener(new RefreshHandler(requester));

        beforeGetData();
        LogUtil.e("Ralph", "params ======== " + requester.getParamMap());
        getData();
    }

    protected ListTypeAdapter.BindViewHolder createHeader() {
        return null;
    }

    protected void beforeGetData() {

    }

    protected void getData() {
        requester.onRefresh();
    }
}