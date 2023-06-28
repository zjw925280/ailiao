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
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.GiftPackActivity;
import com.lovechatapp.chat.activity.InfoActiveActivity;
import com.lovechatapp.chat.activity.PersonInfoActivity;
import com.lovechatapp.chat.activity.RankProtectActivity;
import com.lovechatapp.chat.activity.UserCommentActivity;
import com.lovechatapp.chat.adapter.CloseGiftRecyclerAdapter;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.ActiveBean;
import com.lovechatapp.chat.bean.ActiveFileBean;
import com.lovechatapp.chat.bean.ActorInfoBean;
import com.lovechatapp.chat.bean.ChargeBean;
import com.lovechatapp.chat.bean.CommentBean;
import com.lovechatapp.chat.bean.CoverUrlBean;
import com.lovechatapp.chat.bean.DynamicPreBean;
import com.lovechatapp.chat.bean.InfoRoomBean;
import com.lovechatapp.chat.bean.IntimateBean;
import com.lovechatapp.chat.bean.IntimateDetailBean;
import com.lovechatapp.chat.bean.LabelBean;
import com.lovechatapp.chat.bean.ProtectRankBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.dialog.LookNumberDialog;
import com.lovechatapp.chat.glide.GlideRoundTransform;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.net.PageRequester;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.view.StarView;
import com.lovechatapp.chat.view.recycle.AbsRecycleAdapter;
import com.lovechatapp.chat.view.recycle.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * 个人资料页面
 */
public class PersonDataFragment extends BaseFragment {

    Unbinder unbinder;

    private int otherId;

    private ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean> bean;

    @Override
    protected int initLayout() {
        return R.layout.fragment_user_info;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        otherId = requireActivity().getIntent().getIntExtra(Constant.ACTOR_ID, 0);
        getData();
        protectRv();
        getIntimateGift();
        getDynamic();
        getComments();
    }

    private void loadData(ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean> bean) {
        if (bean != null) {

            this.bean = bean;

            //个人资料
            setMaterial(bean);

            //星星
            StarView starView = findViewById(R.id.star_view);
            starView.setSelected((int) bean.t_score);

            //评分
            TextView scoreTv = findViewById(R.id.score_tv);
            scoreTv.setText(String.valueOf(bean.t_score));
        }
    }

    /**
     * 获取主播资料
     */
    private void getData() {

        if (getActivity() instanceof PersonInfoActivity) {
            PersonInfoActivity actorInfoActivity = (PersonInfoActivity) getActivity();
            bean = actorInfoActivity.getBean();
        }

        if (bean == null) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
            paramMap.put("coverUserId", otherId);
            OkHttpUtils.post().url(ChatApi.GET_ACTOR_INFO())
                    .addParams("param", ParamUtil.getParam(paramMap))
                    .build().execute(new AjaxCallback<BaseResponse<ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean>>>() {
                @Override
                public void onResponse(BaseResponse<ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean>> response, int id) {
                    if (getActivity() == null || getActivity().isFinishing()) {
                        return;
                    }
                    if (response != null && response.m_istatus == NetCode.SUCCESS) {
                        loadData(response.m_object);
                    }
                }
            });
        } else {
            loadData(bean);
        }
    }

    /**
     * 守护榜
     */
    public final void protectRv() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("coverUserId", otherId);
        OkHttpUtils
                .post()
                .url(ChatApi.getUserGuardGiftList())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build()
                .execute(new AjaxCallback<BaseResponse<List<ProtectRankBean>>>() {
                    @Override
                    public void onResponse(BaseResponse<List<ProtectRankBean>> response, int id) {

                        if (getActivity() == null
                                || getActivity().isFinishing()
                                || response == null
                                || response.m_object == null
                                || response.m_object.size() == 0) {
                            return;
                        }

                        findViewById(R.id.protect_ll).setVisibility(View.VISIBLE);

                        List<ProtectRankBean> list = response.m_object;
                        if (list.size() > 6) {
                            list = list.subList(0, 6);
                        }

                        AbsRecycleAdapter adapter = new AbsRecycleAdapter() {

                            final int[] bgDrawIds = {
                                    R.drawable.protect_circle_rank1,
                                    R.drawable.protect_circle_rank2,
                                    R.drawable.protect_circle_rank3};

                            @Override
                            public void convert(ViewHolder holder, Object t) {
                                ProtectRankBean bean = (ProtectRankBean) t;

                                Glide.with(mContext)
                                        .load(bean.t_handImg)
                                        .error(R.drawable.default_head)
                                        .transform(new CircleCrop())
                                        .into(holder.<ImageView>getView(R.id.head_iv));
                                if (holder.getRealPosition() < bgDrawIds.length) {
                                    holder.<ImageView>getView(R.id.rank_iv).setVisibility(View.VISIBLE);
                                    holder.<ImageView>getView(R.id.rank_iv).setImageResource(bgDrawIds[holder.getRealPosition()]);
                                } else {
                                    holder.<ImageView>getView(R.id.rank_iv).setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public int getItemViewType(int position) {
                                return R.layout.item_protect_rank;
                            }

                        };
                        adapter.setDatas(list);
                        RecyclerView recyclerView = findViewById(R.id.protect_rv);
                        recyclerView.setNestedScrollingEnabled(false);
                        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 6));
                        recyclerView.setAdapter(adapter);

                        findViewById(R.id.protect_ll).setOnClickListener(v -> RankProtectActivity.start(mContext, otherId));
                    }
                });
    }

    /**
     * 用户印象
     */
    private void getComments() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("coverUserId", otherId);
        OkHttpUtils
                .post()
                .url(ChatApi.getNewEvaluationList())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build()
                .execute(new AjaxCallback<BaseResponse<List<CommentBean>>>() {
                    @Override
                    public void onResponse(BaseResponse<List<CommentBean>> response, int id) {

                        if (getActivity() == null || getActivity().isFinishing() || response.m_object == null) {
                            return;
                        }

                        if (response.m_object.size() == 0) {
                            return;
                        }

                        findViewById(R.id.comment_ll).setOnClickListener(v -> UserCommentActivity.start(mContext, JSON.toJSONString(response.m_object)));

                        View commentIv = findViewById(R.id.comment_iv);
                        commentIv.setVisibility(View.VISIBLE);
                        int[] tvIds = {R.id.comment1_tv, R.id.comment2_tv, R.id.comment3_tv};
                        for (int i = 0; i < tvIds.length; i++) {
                            TextView textView = findViewById(tvIds[i]);
                            if (i < response.m_object.size()) {
                                textView.setText(response.m_object.get(i).t_label_name);
                                textView.append(String.format("(%s)", response.m_object.get(i).evaluationCount));
                                textView.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                });
    }

    /**
     * 预览动态
     */
    private void getDynamic() {

        PageRequester<ActiveBean<ActiveFileBean>> pageRequester = new PageRequester<ActiveBean<ActiveFileBean>>() {

            @Override
            public void onSuccess(List<ActiveBean<ActiveFileBean>> list, boolean isRefresh) {

                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

                if (list.size() == 0) {
                    return;
                }
                View dynamicBtn = findViewById(R.id.dynamic_ll);
                dynamicBtn.setVisibility(View.VISIBLE);
                dynamicBtn.setOnClickListener(v -> InfoActiveActivity.start(mContext, otherId));

                List<DynamicPreBean> beans = new ArrayList<>();
                int index = 0;
                for (ActiveBean<ActiveFileBean> bean : list) {
                    List<ActiveFileBean> fileBeans = bean.dynamicFiles;
                    if (fileBeans != null && fileBeans.size() > 0) {
                        ActiveFileBean fileBean = fileBeans.get(0);
                        String imgUrl = fileBean.t_file_type == 1 ? fileBean.t_cover_img_url : fileBean.t_file_url;
                        DynamicPreBean dynamicPreBean = new DynamicPreBean(bean, index, imgUrl, fileBean.t_file_type == 1);
                        dynamicPreBean.isLock = fileBean.judgePrivate(otherId);
                        beans.add(dynamicPreBean);
                        if (beans.size() == 4) {
                            break;
                        }
                    }
                    index++;
                }

                AbsRecycleAdapter adapter = new AbsRecycleAdapter() {

                    @Override
                    public void convert(ViewHolder holder, Object t) {
                        DynamicPreBean bean = (DynamicPreBean) t;
                        if (bean.isLock) {
                            Glide.with(mContext)
                                    .load(bean.imgUrl)
                                    .transform(
                                            new CenterCrop(),
                                            new GlideRoundTransform(6),
                                            new BlurTransformation(100, 2))
                                    .into(holder.<ImageView>getView(R.id.content_iv));
                        } else {
                            Glide.with(mContext)
                                    .load(bean.imgUrl)
                                    .transform(
                                            new CenterCrop(),
                                            new GlideRoundTransform(6))
                                    .into(holder.<ImageView>getView(R.id.content_iv));
                        }
                        holder.getView(R.id.lock_iv).setVisibility(bean.isLock ? View.VISIBLE : View.GONE);
                        holder.getView(R.id.video_play_iv).setVisibility(bean.isVideo ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public int getItemViewType(int position) {
                        return R.layout.item_pre_dynamic;
                    }

                };
                adapter.setDatas(beans);
                RecyclerView recyclerView = findViewById(R.id.dynamic_rv);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
                recyclerView.setAdapter(adapter);
            }
        };
        pageRequester.setParam("coverUserId", otherId);
        pageRequester.setParam("reqType", 0);
        pageRequester.setApi(ChatApi.GET_PRIVATE_DYNAMIC_LIST());
        pageRequester.onRefresh();
    }

    /**
     * 查看联系方式
     */
    private void setContact(final ActorInfoBean<CoverUrlBean, LabelBean, ChargeBean, InfoRoomBean> bean) {

        View mWeixinTv = findViewById(R.id.contact_wx);
        View mQqTv = findViewById(R.id.contact_qq);
        View mPhoneTv = findViewById(R.id.contact_phone);

        mWeixinTv.setTag(0);
        mPhoneTv.setTag(1);
        mQqTv.setTag(2);

        View.OnClickListener lookClickListener = v -> new LookNumberDialog(mContext, bean, (int) v.getTag(), otherId).show();

        if (bean.anchorSetup != null && bean.anchorSetup.size() > 0) {
            ChargeBean chargeBean = bean.anchorSetup.get(0);
            if (chargeBean.t_weixin_gold != 0 && !TextUtils.isEmpty(bean.t_weixin)) {
                mWeixinTv.setAlpha(1f);
                mWeixinTv.setOnClickListener(lookClickListener);
            }
            if (chargeBean.t_qq_gold != 0 && !TextUtils.isEmpty(bean.t_qq)) {
                mQqTv.setAlpha(1f);
                mQqTv.setOnClickListener(lookClickListener);
            }
            if (chargeBean.t_phone_gold != 0 && !TextUtils.isEmpty(bean.t_phone)) {
                mPhoneTv.setAlpha(1f);
                mPhoneTv.setOnClickListener(lookClickListener);
            }
        }

        mWeixinTv.setOnClickListener(lookClickListener);
        mPhoneTv.setOnClickListener(lookClickListener);
        mQqTv.setOnClickListener(lookClickListener);
    }

    /**
     * 资料
     */
    private void setMaterial(ActorInfoBean actorInfoBean) {
        List<String> list = new ArrayList<>();
        if (actorInfoBean.t_height != 0) {
            list.add("身高: " + actorInfoBean.t_height + "cm");
        }
        if (actorInfoBean.t_weight != 0) {
            list.add("体重: " + actorInfoBean.t_weight + "kg");
        }
        if (!TextUtils.isEmpty(actorInfoBean.t_vocation)) {
            list.add("职业: " + actorInfoBean.t_vocation);
        }
        if (!TextUtils.isEmpty(actorInfoBean.t_marriage)) {
            list.add("婚姻: " + actorInfoBean.t_marriage);
        }
        if (!TextUtils.isEmpty(actorInfoBean.t_login_time)) {
            list.add(actorInfoBean.t_login_time);
        }
        if (list.size() > 0) {
            TagFlowLayout flowLayout = findViewById(R.id.flow_view);
            flowLayout.setVisibility(View.VISIBLE);
            flowLayout.setAdapter(new TagAdapter<String>(list) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_actor_flow_label, parent, false);
                    tv.setText(s);
                    return tv;
                }
            });
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    /**
     * 获取亲密榜礼物柜
     */
    private void getIntimateGift() {

//        final View.OnClickListener closeClick = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, CloseRankActivity.class);
//                intent.putExtra(Constant.ACTOR_ID, otherId);
//                startActivity(intent);
//            }
//        };
//        findViewById(R.id.close_tv).setOnClickListener(closeClick);

        final View.OnClickListener giftClick = v -> {
            Intent intent = new Intent(mContext, GiftPackActivity.class);
            intent.putExtra(Constant.ACTOR_ID, otherId);
            startActivity(intent);
        };
        findViewById(R.id.gift_tv).setOnClickListener(giftClick);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", otherId);
        OkHttpUtils.post().url(ChatApi.GET_INITMATE_AND_GIFT())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<IntimateBean<IntimateDetailBean>>>() {
            @Override
            public void onResponse(BaseResponse<IntimateBean<IntimateDetailBean>> response, int id) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    IntimateBean<IntimateDetailBean> bean = response.m_object;
                    if (bean != null) {
//                        List<IntimateDetailBean> intimates = bean.intimates;
                        List<IntimateDetailBean> gifts = bean.gifts;

                        //亲密榜
//                        RecyclerView closeRv = findViewById(R.id.close_rv);
//                        closeRv.setNestedScrollingEnabled(false);
//                        if (intimates != null && intimates.size() > 0) {
//                            ((View) closeRv.getParent()).setVisibility(View.VISIBLE);
//                            ((View) closeRv.getParent()).setOnClickListener(closeClick);
//                            setRecyclerView(closeRv, intimates, 0);
//                        }

                        //礼物柜
                        RecyclerView giftRv = findViewById(R.id.gift_rv);
                        giftRv.setNestedScrollingEnabled(false);
                        if (gifts != null && gifts.size() > 0) {
                            ((View) giftRv.getParent()).setVisibility(View.VISIBLE);
                            ((View) giftRv.getParent()).setOnClickListener(giftClick);
                            setRecyclerView(giftRv, gifts, 1);
                        }
                    }
                }
            }
        });
    }

    /**
     * 设置礼物柜&亲密
     */
    private void setRecyclerView(RecyclerView recyclerView, List<IntimateDetailBean> beans, int type) {
        GridLayoutManager manager = new GridLayoutManager(mContext, 6);
        recyclerView.setLayoutManager(manager);
        CloseGiftRecyclerAdapter adapter = new CloseGiftRecyclerAdapter(mContext, type);
        recyclerView.setAdapter(adapter);
        adapter.loadData(beans);
    }

}