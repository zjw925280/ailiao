package com.lovechatapp.chat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.pili.pldroid.player.PLOnPreparedListener;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.VipCenterActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.GirlListBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.glide.GlideCircleTransform;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.AudioVideoRequester;
import com.lovechatapp.chat.net.PageRequester;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.tencent.qcloud.tim.uikit.utils.ThreadHelper;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * 选聊页面
 */
public class RandomChatFragment extends BaseFragment {

    @BindView(R.id.video_ll)
    LinearLayout videoLl;

    @BindView(R.id.head_iv)
    ImageView headIv;

    //SVIP才能使用
    @BindView(R.id.vip_ll)
    LinearLayout mVipLl;

    Unbinder unbinder;

    PageRequester<GirlListBean> requester;

    private List<ViewHolder> viewHolders = new ArrayList<>(3);

    @Override
    protected int initLayout() {
        return R.layout.fragment_random_chat;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        requester = new PageRequester<GirlListBean>() {
            @Override
            public void onSuccess(List<GirlListBean> list, boolean isRefresh) {
                refreshVideo(list);
            }
        };
        requester.setApi(ChatApi.GET_HOME_PAGE_LIST());
        // 类型 0.新人 1.推荐 2.活跃 3.女神 4.选聊
        requester.setParam("queryType", 4);
        requester.setSize(3);
        requester.onRefresh();

    }

    @Override
    public void onResume() {
        super.onResume();

        //头像
        Glide.with(mContext)
                .load(AppManager.getInstance().getUserInfo().headUrl)
                .error(R.drawable.default_head)
                .transform(new GlideCircleTransform(mContext))
                .into(headIv);
    }

    private void refreshVideo(List<GirlListBean> listBeans) {
        if (viewHolders.size() == 0) {
            for (int i = 0; i < 3; i++) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_random_chat, videoLl, false);
                ViewHolder viewHolder = new ViewHolder(view);
                viewHolders.add(viewHolder);
                videoLl.addView(view);
            }
        }
        for (int i = 0; i < viewHolders.size(); i++) {
            ViewHolder viewHolder = viewHolders.get(i);
            GirlListBean bean = null;
            if (listBeans != null && i < listBeans.size()) {
                bean = listBeans.get(i);
            }
            viewHolder.bindBean(bean);
        }
    }

    private boolean isGetting;

    private void changActor(final ViewHolder viewHolder) {

        if (viewHolder == null || viewHolder.girlListBean == null) {
            return;
        }

        if (isGetting || requester.isRequesting()) {
            ToastUtil.INSTANCE.showToast("您的手速太快啦");
            return;
        }

        isGetting = true;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("anotherId", viewHolder.girlListBean.t_id);
        OkHttpUtils.post().url(ChatApi.getSelectCharAnother())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<GirlListBean>>() {

            @Override
            public void onResponse(BaseResponse<GirlListBean> response, int id) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }
                isGetting = false;
                if (response.m_object == null) {
                    ToastUtil.INSTANCE.showToast("没有更多主播了");
                    return;
                }
                viewHolder.bindBean(response.m_object);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                isGetting = false;
            }
        });
    }

    protected void showChanged(boolean b) {
        if (b) {
            resumeAll();
        } else {
            pauseAll();
        }
        //是否VIP 0.是 1.否
        boolean show = AppManager.getInstance().getUserInfo().isVip();
        if (mVipLl != null) {
            mVipLl.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void pauseAll() {
        ThreadHelper.INST.execute(new Runnable() {
            @Override
            public void run() {
                for (RandomChatFragment.ViewHolder viewHolder : viewHolders) {
                    viewHolder.pause();
                }
            }
        });
    }

    private void resumeAll() {
        for (ViewHolder viewHolder : viewHolders) {
            viewHolder.resume();
        }
    }

    @Override
    protected void onFirstVisible() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
        for (ViewHolder viewHolder : viewHolders) {
            viewHolder.pause();
        }
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.update_tv, R.id.confirm_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_tv: {
                if (isGetting || requester.isRequesting()) {
                    ToastUtil.INSTANCE.showToast("您的手速太快啦");
                    return;
                }
                requester.onRefresh();
                break;
            }
            case R.id.confirm_tv: {
                if (getActivity() != null) {
                    getActivity().startActivity(new Intent(getActivity(), VipCenterActivity.class));
                }
                break;
            }
        }

    }

    private class ViewHolder {

        PLVideoTextureView plv;

        ImageView cover;

        View view;

        GirlListBean girlListBean;


        ViewHolder(View view) {

            this.plv = view.findViewById(R.id.video_view);
            this.cover = view.findViewById(R.id.img_iv);
            this.view = view;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (girlListBean == null) {
                        return;
                    }
                    /*if (AppManager.getInstance().getUserInfo().t_is_svip != 0) {
                        new VipDialog(getActivity(), "选聊功能只有SVIP用户才能使用").show();
                        return;
                    }*/
                    new AudioVideoRequester(getActivity(),
                            true,
                            girlListBean.t_id).executeVideo();
                }
            });

            view.findViewById(R.id.del_iv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changActor(RandomChatFragment.ViewHolder.this);
                }
            });
        }

        private void resume() {
            plv.start();
        }

        private void pause() {
            if (girlListBean != null)
                plv.pause();
        }

        private void bindBean(final GirlListBean girlListBean) {

            this.girlListBean = girlListBean;

            view.setVisibility(girlListBean == null ? View.INVISIBLE : View.VISIBLE);

            cover.setVisibility(View.VISIBLE);
            cover.setImageResource(0);

            plv.stopPlayback();
            plv.setTag(null);

            if (girlListBean != null && plv != null && !TextUtils.isEmpty(girlListBean.t_addres_url)) {

                ImageLoadHelper.glideShowCornerImageWithUrl(mContext, girlListBean.t_cover_img, cover, 0, 200, 350);

                final String url = girlListBean.t_addres_url;

                plv.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT);
                plv.setTag(url);
                plv.setLooping(true);
                plv.setVideoPath(url);
                plv.setVolume(0, 0);
                plv.setOnPreparedListener(new PLOnPreparedListener() {
                    @Override
                    public void onPrepared(int i) {
                        if (!url.equals(plv.getTag()))
                            return;
                        if (isShowing()) {
                            plv.start();
                        } else {
                            plv.pause();
                        }
                        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
                        alphaAnimation.setDuration(300);
                        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                cover.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        cover.startAnimation(alphaAnimation);
                    }
                });
            }
        }
    }
}