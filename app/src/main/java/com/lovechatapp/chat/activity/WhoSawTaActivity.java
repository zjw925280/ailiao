package com.lovechatapp.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.BrowedBean;
import com.lovechatapp.chat.bean.PageBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.FocusRequester;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.BeanParamUtil;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.TimeUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.OnItemClickListener;
import com.lovechatapp.chat.view.recycle.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 谁看过 TA/我
 */
public class WhoSawTaActivity extends BaseActivity {

    @BindView(R.id.content_rv)
    RecyclerView mContentRv;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.empty_tv)
    TextView emptyTv;

    private AbsRecycleAdapter mAdapter;

    private List<BrowedBean> mBrowedBeans = new ArrayList<>();

    private int mCurrentPage = 1;

    private int actorId;

    public static void start(Context context) {
        Intent starter = new Intent(context, WhoSawTaActivity.class);
        starter.putExtra("actorId", AppManager.getInstance().getUserInfo().t_id);
        context.startActivity(starter);
    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_who_saw_me);
    }

    @Override
    protected void onContentAdded() {

        actorId = getIntent().getIntExtra("actorId", 0);

        //谁看过我
        if (actorId == 0 || getUserId().equals(actorId + "")) {
            setTitle(R.string.who_saw_me);
        }
        //谁看过TA
        else {
            setTitle(R.string.who_saw_ta);
        }
        initRecycler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFocusList(mRefreshLayout, true, 1);
    }

    public String getActorId() {
        return actorId == 0 ? getUserId() : actorId + "";
    }

    /**
     * 获取谁看过我
     */
    private void getFocusList(final RefreshLayout refreshlayout, final boolean isRefresh, int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", getActorId());
        paramMap.put("page", String.valueOf(page));
        paramMap.put("size", "10");
        OkHttpUtils.post().url(ChatApi.getCoverBrowseList())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<BrowedBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<BrowedBean>> response, int id) {
                if (isFinishing()){
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    PageBean<BrowedBean> pageBean = response.m_object;
                    if (pageBean != null) {
                        List<BrowedBean> BrowedBeans = pageBean.data;
                        if (BrowedBeans != null) {
                            int size = BrowedBeans.size();
                            if (isRefresh) {
                                mCurrentPage = 1;
                                mBrowedBeans.clear();
                                mBrowedBeans.addAll(BrowedBeans);
                                mAdapter.setDatas(mBrowedBeans);
                                if (mBrowedBeans.size() > 0) {
                                    emptyTv.setVisibility(View.GONE);
                                } else {
                                    emptyTv.setVisibility(View.VISIBLE);
                                }
                                refreshlayout.finishRefresh();
                                if (size >= 10) {//如果是刷新,且返回的数据大于等于10条,就可以load more
                                    refreshlayout.setEnableLoadMore(true);
                                }
                            } else {
                                mCurrentPage++;
                                mBrowedBeans.addAll(BrowedBeans);
                                mAdapter.setDatas(mBrowedBeans);
                                if (size >= 10) {
                                    refreshlayout.finishLoadMore();
                                }
                            }
                            if (size < 10) {//如果数据返回少于10了,那么说明就没数据了
                                refreshlayout.finishLoadMoreWithNoMoreData();
                            }
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

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(getApplicationContext(), R.string.system_error);
                if (isRefresh) {
                    refreshlayout.finishRefresh();
                } else {
                    refreshlayout.finishLoadMore();
                }
            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecycler() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                getFocusList(refreshlayout, true, 1);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshlayout) {
                getFocusList(refreshlayout, false, mCurrentPage + 1);
            }
        });

        final int smallOverWidth = DevicesUtil.dp2px(mContext, 50);

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        mContentRv.getItemAnimator().setChangeDuration(0);
        mContentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new AbsRecycleAdapter(mBrowedBeans, new AbsRecycleAdapter.Type(R.layout.item_who_saw_me, BrowedBean.class)) {
            @Override
            public void convert(ViewHolder holder, Object t) {

                BrowedBean bean = (BrowedBean) t;

                //昵称
                holder.<TextView>getView(R.id.nick_tv).setText(bean.t_nickName);

                //头像
                ImageView imageView = holder.getView(R.id.head_iv);
                if (!TextUtils.isEmpty(bean.t_handImg)) {
                    ImageLoadHelper.glideShowCircleImageWithUrl(
                            mContext,
                            bean.t_handImg,
                            imageView,
                            smallOverWidth,
                            smallOverWidth);
                } else {
                    imageView.setImageResource(R.drawable.default_head_img);
                }

                //关注时间
                holder.<TextView>getView(R.id.time_tv).setText(TimeUtil.getTimeStr(bean.t_create_time));

                TextView tvAge = holder.getView(R.id.age_tv);

                //年龄
                tvAge.setText(BeanParamUtil.getAge(bean.t_age));

                //性别
                tvAge.setSelected(bean.t_sex == 0);

                TextView textView = holder.getView(R.id.attention_tv);

                //是否关注
                textView.setSelected(bean.isFollow == 1);
                textView.setText(BeanParamUtil.getFocus(bean.isFollow, bean.isCoverFollow));
            }

            @Override
            public void setViewHolder(final ViewHolder viewHolder) {

                //关注点击事件
                viewHolder.getView(R.id.attention_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final BrowedBean bean = (BrowedBean) getData().get(viewHolder.getRealPosition());
                        boolean focus = bean.isFollow != 0;
                        new FocusRequester() {
                            @Override
                            public void onSuccess(BaseResponse response, boolean focus) {
                                ToastUtil.INSTANCE.showToast(getApplicationContext(), response.m_strMessage);
                                bean.isFollow = focus ? 1 : 0;
                                notifyItemChanged(viewHolder.getRealPosition());
                            }
                        }.focus(bean.t_id, !focus);
                    }
                });
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object object, int position) {
                BrowedBean bean = (BrowedBean) mAdapter.getData().get(position);
                PersonInfoActivity.start(mContext, bean.t_id);
            }
        });
        mContentRv.setAdapter(mAdapter);
    }
}