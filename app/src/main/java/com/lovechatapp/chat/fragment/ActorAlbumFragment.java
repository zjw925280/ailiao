package com.lovechatapp.chat.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.ActorVideoAlbumActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.PageBean;
import com.lovechatapp.chat.bean.AlbumBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.glide.GlideRoundTransform;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.DensityUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * 主播资料页相册&视频
 */
public class ActorAlbumFragment extends Fragment {

    private int mActorId;
    private boolean isVideo;
    private int fileType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actor_album, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null && getView() != null) {
            RecyclerView recyclerView = getView().findViewById(R.id.recycle_view);
            recyclerView.setNestedScrollingEnabled(false);
            getView().setVisibility(View.GONE);
            mActorId = getActivity().getIntent().getIntExtra(Constant.ACTOR_ID, 0);
            if (getTag() != null) {
                try {
                    fileType = Integer.parseInt(getTag());
                    isVideo = fileType == 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            getActorVideo();
        }
    }

    /**
     * 获取主播视频照片
     * fileType 0.图片 1.视频
     */
    private void getActorVideo() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("coverUserId", mActorId);
        paramMap.put("page", 1);
        paramMap.put("fileType", fileType);
        OkHttpUtils.post().url(ChatApi.GET_DYNAMIC_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<PageBean<AlbumBean>>>() {
            @Override
            public void onResponse(BaseResponse<PageBean<AlbumBean>> response, int id) {
                if (getActivity() == null || getActivity().isFinishing() || getView() == null)
                    return;
                if (response != null
                        && response.m_istatus == NetCode.SUCCESS
                        && response.m_object != null
                        && response.m_object.data != null
                        && response.m_object.data.size() > 0) {

                    List<AlbumBean> list = new ArrayList<>();
                    if (isVideo && response.m_object.data.size() > 2) {
                        list.addAll(response.m_object.data.subList(0, 2));
                    } else if (!isVideo && response.m_object.data.size() > 3) {
                        list.addAll(response.m_object.data.subList(0, 3));
                    } else {
                        list.addAll(response.m_object.data);
                    }

                    getView().setVisibility(View.VISIBLE);

                    getView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActorVideoAlbumActivity.start(getActivity(), mActorId, fileType);
                        }
                    });

                    RecyclerView recyclerView = getView().findViewById(R.id.recycle_view);
                    recyclerView.setNestedScrollingEnabled(false);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(layoutManager);

                    DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.HORIZONTAL);
                    decoration.setDrawable(getActivity().getResources().getDrawable(R.drawable.divider_vertical_trans20));
                    recyclerView.addItemDecoration(decoration);

                    final int imgSize = DensityUtil.dip2px(getActivity(), isVideo ? 130 : 80);

                    final AbsRecycleAdapter adapter = new AbsRecycleAdapter(
                            new AbsRecycleAdapter.Type(isVideo ? R.layout.item_actor_video : R.layout.item_actor_album, AlbumBean.class)) {
                        @Override
                        public void convert(ViewHolder holder, Object t) {
                            AlbumBean bean = (AlbumBean) t;

                            holder.getView(R.id.lock_iv).setVisibility(bean.isLock() ? View.VISIBLE : View.GONE);

                            //图片
                            if (bean.t_file_type == 0) {
                                if (bean.isLock()) {
                                    Glide.with(getActivity())
                                            .load(bean.t_addres_url)
                                            .override(imgSize, imgSize)
                                            .transform(new CenterCrop(),
                                                    new BlurTransformation(25, 2),
                                                    new GlideRoundTransform(8))
                                            .into(holder.<ImageView>getView(R.id.content_iv));
                                } else {
                                    Glide.with(getActivity())
                                            .load(bean.t_addres_url)
                                            .override(imgSize, imgSize)
                                            .transform(new CenterCrop(), new GlideRoundTransform(8))
                                            .into(holder.<ImageView>getView(R.id.content_iv));
                                }
                            }

                            //视频
                            else {
                                if (bean.isLock()) {
                                    Glide.with(getActivity())
                                            .load(bean.t_video_img)
                                            .override(imgSize, imgSize)
                                            .transform(new CenterCrop(),
                                                    new BlurTransformation(25, 2),
                                                    new GlideRoundTransform(8))
                                            .into(holder.<ImageView>getView(R.id.content_iv));
                                } else {
                                    Glide.with(getActivity())
                                            .load(bean.t_video_img)
                                            .override(imgSize, imgSize)
                                            .transform(new CenterCrop(), new GlideRoundTransform(8))
                                            .into(holder.<ImageView>getView(R.id.content_iv));
                                }
                            }
                        }
                    };

                    recyclerView.setAdapter(adapter);
                    adapter.setData(list, true);
                }
            }
        });
    }
}