package com.lovechatapp.chat.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.PersonInfoActivity;
import com.lovechatapp.chat.activity.ReportActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.ActorInfoBean;
import com.lovechatapp.chat.bean.ChargeBean;
import com.lovechatapp.chat.bean.CoverUrlBean;
import com.lovechatapp.chat.bean.InfoRoomBean;
import com.lovechatapp.chat.bean.LabelBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.dialog.GiftDialog;
import com.lovechatapp.chat.glide.GlideCircleTransform;
import com.lovechatapp.chat.helper.IMHelper;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.AudioVideoRequester;
import com.lovechatapp.chat.net.FocusRequester;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.DensityUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.view.viewpager.YViewPager;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 动态视频播放页 & 主播视频封面播放页（上下滑动布局） & 个人相册视频播放页
 */
public class VideoPlayFragment extends BaseFragment implements PLOnErrorListener {

    @BindView(R.id.plv)
    PLVideoTextureView plv;

    @BindView(R.id.nick_tv)
    TextView nickTv;

    @BindView(R.id.age_tv)
    TextView ageTv;

    @BindView(R.id.online_tv)
    TextView onlineTv;

    @BindView(R.id.follow_tv)
    TextView followTv;

    @BindView(R.id.head_iv)
    ImageView headImg;

    @BindView(R.id.up_iv)
    View upView;

    @BindView(R.id.bottom_ll)
    View infoView;

    @BindView(R.id.complain_iv)
    View complainIv;

    Unbinder unbinder;

    private int mActorId;

    private ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean> mActorInfoBean;

    private String videoUrl;

    private boolean canPlay = true;

    @Override
    protected int initLayout() {
        return R.layout.fragment_actor_pager_video;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        infoView.setVisibility(View.GONE);

        mActorId = requireActivity().getIntent().getIntExtra(Constant.ACTOR_ID, 0);
        videoUrl = requireActivity().getIntent().getStringExtra(Constant.VIDEO_URL);

        if (mActorId == AppManager.getInstance().getUserInfo().t_id) {
            complainIv.setVisibility(View.GONE);
        } else {
            complainIv.setVisibility(View.VISIBLE);
        }
        getActorInfo();

        playVideoWithUrl(videoUrl);

        final YViewPager pagerVv = requireActivity().findViewById(R.id.pager_vv);
        if (pagerVv != null) {
            /*
             * 上下滑动视频播放页，需要监听page选中
             */
            pagerVv.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    if (plv != null) {
                        if (position == 0) {
                            plv.start();
                            canPlay = true;
                        } else {
                            plv.pause();
                            canPlay = false;
                        }
                    }
                }
            });
        } else {
            /*
             * 单独视频播放页
             */
            upView.setVisibility(View.GONE);
            headImg.setOnClickListener(v -> PersonInfoActivity.start(requireActivity(), mActorId));
        }

//        plv.setOnErrorListener(i -> {
//            ToastUtil.INSTANCE.showToast("Sorry,播放失败" + i);
//            return false;
//        });

        plv.setOnErrorListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        if (plv != null)
            plv.stopPlayback();
        super.onDestroyView();
        unbinder.unbind();
    }

    int[] stateIcons = {
            R.drawable.shape_free_indicator,
            R.drawable.shape_busy_indicator,
            R.drawable.shape_offline_indicator};

    int[] stateTexts = {
            R.string.free,
            R.string.busy,
            R.string.offline};

    private void loadData(ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean> mActorInfoBean) {
        this.mActorInfoBean = mActorInfoBean;
        ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean> bean = mActorInfoBean;
        if (bean != null) {

            //显示非个人信息
            if (mActorId != AppManager.getInstance().getUserInfo().t_id) {
                infoView.setVisibility(View.VISIBLE);
            }

            //头像
            Glide.with(requireActivity())
                    .load(bean.t_handImg)
                    .error(R.drawable.default_head_img)
                    .override(DensityUtil.dip2px(requireActivity(), 50))
                    .transform(new GlideCircleTransform(requireActivity()))
                    .into(headImg);

            //昵称
            nickTv.setText(bean.t_nickName);

            //岁数
            ageTv.setText(String.format("%s岁", bean.t_age));

            //关注  是否关注 0.未关注 1.已关注
            refreshFollow(bean.isFollow == 1);

            //在线状态(0.在线1.忙碌2.离线)
            onlineTv.setCompoundDrawablesRelativeWithIntrinsicBounds(stateIcons[bean.t_onLine], 0, 0, 0);
            onlineTv.setText(stateTexts[bean.t_onLine]);

            if (bean.t_is_not_disturb == 0) {
                onlineTv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.shape_busy_indicator, 0, 0, 0);
                onlineTv.setText("勿扰");
                return;
            }

            if (TextUtils.isEmpty(videoUrl) && !TextUtils.isEmpty(bean.t_addres_url)) {
                videoUrl = bean.t_addres_url;
                playVideoWithUrl(bean.t_addres_url);
            }
        }
    }

    private void playVideoWithUrl(String url) {
        if (plv != null && !TextUtils.isEmpty(url)) {
            videoUrl = url;
            if (plv.isPlaying())
                return;
            plv.setVideoPath(url);
            plv.setLooping(true);
            plv.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (plv != null) {
            plv.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (plv != null && canPlay) {
            plv.start();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({
            R.id.text_chat_btn,
            R.id.video_chat_btn,
            R.id.audio_chat_btn,
            R.id.send_gift_btn,
            R.id.follow_tv,
            R.id.back_iv,
            R.id.complain_iv
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_chat_btn:
                if (mActorInfoBean == null) {
                    getActorInfo();
                    return;
                }
                IMHelper.toChat(requireActivity(), mActorInfoBean.t_nickName, mActorId, mActorInfoBean.t_sex);
                break;
            case R.id.send_gift_btn:
                new GiftDialog(mContext, mActorId).show();
                break;
            case R.id.back_iv:
                requireActivity().finish();
                break;
            case R.id.follow_tv:
                follow(view.isSelected());
                break;
            case R.id.complain_iv:
                Intent intent = new Intent(requireActivity(), ReportActivity.class);
                intent.putExtra(Constant.ACTOR_ID, mActorId);
                startActivity(intent);
                break;
            case R.id.video_chat_btn:
            case R.id.audio_chat_btn:
                if (mActorInfoBean == null) {
                    getActorInfo();
                    return;
                }
                if (AppManager.getInstance().getUserInfo().t_sex == mActorInfoBean.t_sex) {
                    ToastUtil.INSTANCE.showToast(requireActivity(), R.string.sex_can_not_communicate);
                    return;
                }
                AudioVideoRequester audioVideoRequester = new AudioVideoRequester(
                        requireActivity(),
                        true,
                        mActorId);
//                if (view.getId() == R.id.video_chat_btn) {
                audioVideoRequester.executeVideo();
//                } else {
//                    audioVideoRequester.executeAudio();
//                }
                break;
        }
    }

    /**
     * 设置关注
     */
    private void follow(boolean isFollow) {
        final boolean setFollow = !isFollow;
        new FocusRequester() {
            @Override
            public void onSuccess(BaseResponse response, boolean focus) {
                if (requireActivity() == null || requireActivity().isFinishing())
                    return;
                refreshFollow(setFollow);
            }
        }.focus(mActorId, setFollow);
    }

    /**
     * 关注
     */
    private void refreshFollow(boolean isFollow) {
        followTv.setSelected(isFollow);
        followTv.setText(isFollow ? "已关注" : "关注");
        int drawId = isFollow ? R.drawable.video_follow_actor_selected : R.drawable.video_follow_actor_unselected;
        followTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, drawId, 0, 0);
    }

    /**
     * 获取主播资料
     */
    private void getActorInfo() {

        //FragmentActivity属于ActorUserInfoActivity则无需请求数据
        if (requireActivity() instanceof PersonInfoActivity) {
            PersonInfoActivity actorInfoActivity = (PersonInfoActivity) requireActivity();
            mActorInfoBean = actorInfoActivity.getBean();
        }
        if (mActorInfoBean == null) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
            paramMap.put("coverUserId", mActorId);
            OkHttpUtils.post().url(ChatApi.GET_ACTOR_INFO())
                    .addParams("param", ParamUtil.getParam(paramMap))
                    .build().execute(new AjaxCallback<BaseResponse<ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean>>>() {
                        @Override
                        public void onResponse(BaseResponse<ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean>> response, int id) {
                            if (requireActivity() == null || requireActivity().isFinishing()) {
                                return;
                            }
                            if (response != null && response.m_istatus == NetCode.SUCCESS) {
                                loadData(response.m_object);
                            }
                        }
                    });
        } else {
            loadData(mActorInfoBean);
        }
    }

    @Override
    public boolean onError(int i) {
        ToastUtil.INSTANCE.showToast("Sorry,播放失败" + i);
        return false;
    }

//    @Override
//    public boolean onError(int i, Object o) {
//        ToastUtil.INSTANCE.showToast("Sorry,播放失败" + i);
//        return false;
//    }
}