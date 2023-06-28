package com.lovechatapp.chat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.ActorVideoPlayActivity;
import com.lovechatapp.chat.activity.PhotoActivity;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.bean.AlbumBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.dialog.LookResourceDialog;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.PageRequester;
import com.lovechatapp.chat.net.RefreshHandler;
import com.lovechatapp.chat.net.RefreshPageListener;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.OnItemClickListener;
import com.lovechatapp.chat.view.recycle.ViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * 主播、用户的视频&图片
 */
public class PersonAlbumFragment extends BaseFragment {

    @BindView(R.id.content_rv)
    RecyclerView contentRv;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    int mActorId;

    int fileType;

    PageRequester<AlbumBean> requester;

    AbsRecycleAdapter adapter;
    Unbinder unbinder;

    @Override
    protected int initLayout() {
        return R.layout.fragment_actor_video_picture;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        fileType = getArguments().getInt("fileType");
        mActorId = getActivity().getIntent().getIntExtra(Constant.ACTOR_ID, mActorId);

        requester = new PageRequester<AlbumBean>() {
            @Override
            public void onSuccess(List<AlbumBean> list, boolean isRefresh) {
                adapter.setData(list, isRefresh);
            }
        };
        requester.setOnPageDataListener(new RefreshPageListener(refreshLayout));
        requester.setApi(ChatApi.GET_DYNAMIC_LIST());
        requester.setParam("fileType", fileType);
        requester.setParam("coverUserId", mActorId);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnRefreshListener(new RefreshHandler(requester));
        refreshLayout.setOnLoadMoreListener(new RefreshHandler(requester));

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        contentRv.setLayoutManager(layoutManager);

        adapter = new AbsRecycleAdapter(
                new AbsRecycleAdapter.Type(R.layout.item_actor_video_album, AlbumBean.class)) {
            @Override
            public void convert(ViewHolder holder, Object t) {
                AlbumBean bean = (AlbumBean) t;

                holder.getView(R.id.lock_iv).setVisibility(bean.isLock() ? View.VISIBLE : View.GONE);

                holder.getView(R.id.play_iv).setVisibility(bean.t_file_type == 0 ? View.GONE : View.VISIBLE);

                String imgUrl = bean.t_file_type == 0 ? bean.t_addres_url : bean.t_video_img;

                if (bean.isLock()) {
                    Glide.with(getActivity())
                            .load(imgUrl)
                            .transform(new CenterCrop(), new BlurTransformation(100, 2))
                            .into(holder.<ImageView>getView(R.id.content_iv));
                } else {
                    Glide.with(getActivity())
                            .load(imgUrl)
                            .transform(new CenterCrop())
                            .into(holder.<ImageView>getView(R.id.content_iv));
                }
            }
        };

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object object, int position) {
                final AlbumBean bean = (AlbumBean) adapter.getData().get(position);
                if (bean.canSee()) {
                    toIntent(bean);
                } else {
                    LookResourceDialog.showAlbum(getActivity(), bean, mActorId, new OnCommonListener<Boolean>() {
                        @Override
                        public void execute(Boolean aBoolean) {
                            if (aBoolean) {
                                toIntent(bean);
                            }
                        }
                    });
                }
            }
        });

        contentRv.setAdapter(adapter);
        requester.onRefresh();
    }

    private void toIntent(AlbumBean bean) {
        if (bean.t_file_type == 1) {
            ActorVideoPlayActivity.start(getActivity(), mActorId, bean.t_addres_url);
        } else {
            if (!TextUtils.isEmpty(bean.t_addres_url)) {
                Intent intent = new Intent(getActivity(), PhotoActivity.class);
                intent.putExtra(Constant.IMAGE_URL, bean.t_addres_url);
                getActivity().startActivity(intent);
            }
        }
    }

}