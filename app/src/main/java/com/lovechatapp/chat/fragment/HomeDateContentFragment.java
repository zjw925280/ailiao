package com.lovechatapp.chat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lovechatapp.chat.activity.DateCreateActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.helper.IMHelper;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.PersonInfoActivity;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.bean.GirlListBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.PageRequester;
import com.lovechatapp.chat.net.RefreshHandler;
import com.lovechatapp.chat.net.RefreshPageListener;
import com.lovechatapp.chat.view.recycle.ListTypeAdapter;
import com.lovechatapp.chat.view.recycle.ViewHolder;

import java.util.List;

/**
 * 主播列表页
 */
public class HomeDateContentFragment extends BaseFragment {

    protected PageRequester<GirlListBean> requester;

    protected SmartRefreshLayout mRefreshLayout;

    protected ListTypeAdapter adapter;

    protected ListTypeAdapter.BindViewHolder header;

    protected ListTypeAdapter.BindViewHolder content;

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
        mContentRv.setLayoutManager(new LinearLayoutManager(mContext));

        header = createHeader();
        content = new ListTypeAdapter.BindViewHolder(R.layout.layout_item_home_date, true) {

            @Override
            public ViewHolder createViewHolder(ViewGroup parent, int layoutId) {

                View v = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
                ViewHolder viewHolder =
                        new ViewHolder(v) {

                            @Override
                            public void convert(ViewHolder holder, Object t) {

                                if (getActivity() == null)
                                    return;

                                GirlListBean bean = (GirlListBean) t;
                                View itemBg = v.findViewById(R.id.itemBg);
                                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) itemBg.getLayoutParams();
                                if (getData().indexOf(bean) == 0) {
                                    //设置item距上距离
                                    params.topMargin = getResources().getDimensionPixelOffset(R.dimen.dp_16);
                                } else {
                                    params.topMargin = 0;
                                }
                                itemBg.setLayoutParams(params);
                                //绑定数据
                                int size = v.getContext().getResources().getDimensionPixelOffset(R.dimen.dp_60);
                                ImageLoadHelper.glideShowCornerImageWithUrl(v.getContext(), bean.t_cover_img, v.findViewById(R.id.ivAvatar),
                                        8, size, size);

                                ((ImageView) v.findViewById(R.id.ivStatus)).setImageLevel(bean.t_state);
//                                int visible;
//                                if (bean.getIsIdentified() == 0) {
//                                    visible = View.GONE;
//                                } else {
//                                    visible = View.VISIBLE;
//                                }
//                                itemView.findViewById(R.id.ivIdentity).setVisibility(visible);
                                ((AppCompatTextView) v.findViewById(R.id.textName)).setText(bean.t_nickName);
                                ((AppCompatTextView) v.findViewById(R.id.textInfo))
                                        .setText(
                                                v.getContext().getString(R.string.date_home_list_user_info,
                                                        bean.t_city,
                                                        bean.t_age + "",
                                                        bean.t_vocation)
                                        );
                                ((AppCompatTextView) v.findViewById(R.id.textProfile)).setText(bean.t_autograph);


                                v.findViewById(R.id.btnChat).setOnClickListener(view -> {
                                    IMHelper.toChat(getActivity(), bean.t_nickName, bean.t_id, bean.t_sex);
                                });
                                v.findViewById(R.id.btnDate).setOnClickListener(view -> {
                                    Log.e("啥玩意","是多少呢"+AppManager.getInstance().getUserInfo().t_id+" 是这个吧="+bean.t_id);
                                    DateCreateActivity.startActivity(mContext, String.valueOf(bean.t_id), String.valueOf(AppManager.getInstance().getUserInfo().t_id+10000), bean.t_nickName);
                                });
                            }
                        };

                viewHolder.setOnItemClickListener((view, object, position) -> {
                    GirlListBean bean = (GirlListBean) object;
                    PersonInfoActivity.start(mContext, bean.t_id);
                });

                return viewHolder;
            }
        };

        adapter = new ListTypeAdapter(header, content);
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