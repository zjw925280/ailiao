package com.lovechatapp.chat.fragment.info;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.adapter.InfoCommentRecyclerAdapter;
import com.lovechatapp.chat.base.BaseFragment;
import com.lovechatapp.chat.base.BaseListResponse;
import com.lovechatapp.chat.bean.CommentBean;
import com.lovechatapp.chat.bean.TagBean;
import com.lovechatapp.chat.bean.UserInfoBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：资料Fragment
 * 作者：
 * 创建时间：2018/10/23
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class InfoFragment extends BaseFragment {

    private InfoCommentRecyclerAdapter mAdapter;
    private View mPhoneRl;
    private TextView mPhoneTv;
    private View mWeChatRl;
    private TextView mWeChatTv;
    private TextView mLastTimeTv;
    private View mListenRl;
    private TextView mAcceptRateTv;
    private TextView mHighTv;
    private TextView mWeightTv;
    private TextView mStarTv;
    private TextView mCityTv;
    private TextView mSignTv;
    private LinearLayout mTagsLl;
    private RecyclerView mCommentRv;
    private TextView mNoCommentTv;
    private View mCommentV;
    private View mCommentLl;
    private View mCommentFl;

    @Override
    protected int initLayout() {
        return R.layout.fragment_info_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mPhoneRl = view.findViewById(R.id.phone_rl);
        mPhoneTv = view.findViewById(R.id.phone_tv);
        mWeChatRl = view.findViewById(R.id.we_chat_rl);
        mWeChatTv = view.findViewById(R.id.we_chat_tv);
        mLastTimeTv = view.findViewById(R.id.last_time_tv);
        mListenRl = view.findViewById(R.id.listen_rl);
        mAcceptRateTv = view.findViewById(R.id.accept_rate_tv);
        mHighTv = view.findViewById(R.id.high_tv);
        mWeightTv = view.findViewById(R.id.weight_tv);
        mStarTv = view.findViewById(R.id.star_tv);
        mCityTv = view.findViewById(R.id.city_tv);
        mSignTv = view.findViewById(R.id.sign_tv);
        mTagsLl = view.findViewById(R.id.tags_ll);
        mCommentRv = view.findViewById(R.id.comment_rv);
        mNoCommentTv = view.findViewById(R.id.no_comment_tv);
        mCommentV = view.findViewById(R.id.comment_v);
        mCommentLl = view.findViewById(R.id.comment_ll);
        mCommentFl = view.findViewById(R.id.comment_fl);

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
        mCommentRv.setLayoutManager(gridLayoutManager);
        mAdapter = new InfoCommentRecyclerAdapter(getActivity());
        mCommentRv.setAdapter(mAdapter);
    }

    @Override
    protected void onFirstVisible() {

    }

    /**
     * 加载数据
     */
    public void loadData(UserInfoBean<TagBean> bean) {
        if (bean != null) {
            //手机号
            if (!TextUtils.isEmpty(bean.t_phone)) {
                mPhoneRl.setVisibility(View.VISIBLE);
                mPhoneTv.setText(bean.t_phone);
            }
            //微信号
            if (!TextUtils.isEmpty(bean.t_weixin)) {
                mWeChatRl.setVisibility(View.VISIBLE);
                mWeChatTv.setText(bean.t_weixin);
            }
            //最后登录
            if (!TextUtils.isEmpty(bean.t_login_time)) {
                mLastTimeTv.setText(bean.t_login_time);
            }
            //接听率
            if (!TextUtils.isEmpty(bean.t_reception) && bean.t_role == 1) {
                mListenRl.setVisibility(View.VISIBLE);
                mAcceptRateTv.setText(bean.t_reception);
            }
            //身高
            if (bean.t_height > 0) {
                String content = String.valueOf(bean.t_height) + getResources().getString(R.string.high_des);
                mHighTv.setText(content);
            }
            //体重
            if (bean.t_weight > 0) {
                String content = String.valueOf(bean.t_weight) + getResources().getString(R.string.body_des);
                mWeightTv.setText(content);
            }
            //星座
            if (!TextUtils.isEmpty(bean.t_constellation)) {
                mStarTv.setText(bean.t_constellation);
            }
            //城市
            if (!TextUtils.isEmpty(bean.t_city)) {
                mCityTv.setText(bean.t_city);
            }
            //个人签名
            if (!TextUtils.isEmpty(bean.t_autograph)) {
                mSignTv.setText(bean.t_autograph);
            } else {
                mSignTv.setText(mContext.getResources().getString(R.string.lazy_one));
            }
            //标签
            List<TagBean> tagBeans = bean.lable;
            if (tagBeans != null && tagBeans.size() > 0) {
                setLabelView(tagBeans);
            }
            //获取评价,如果是主播的话
            if (bean.t_role == 1) {
                mCommentV.setVisibility(View.VISIBLE);
                mCommentLl.setVisibility(View.VISIBLE);
                mCommentFl.setVisibility(View.VISIBLE);
                getUserComment();
            }
        }
    }

    /**
     * 获取用户评价
     */
    private void getUserComment() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        OkHttpUtils.post().url(ChatApi.GET_EVALUATION_LIST())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseListResponse<CommentBean>>() {
            @Override
            public void onResponse(BaseListResponse<CommentBean> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    List<CommentBean> commentBeans = response.m_object;
                    if (commentBeans != null && commentBeans.size() > 0) {
                        mAdapter.loadData(commentBeans);
                        mNoCommentTv.setVisibility(View.GONE);
                        mCommentRv.setVisibility(View.VISIBLE);
                    } else {
                        mNoCommentTv.setVisibility(View.VISIBLE);
                        mCommentRv.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                mNoCommentTv.setVisibility(View.VISIBLE);
                mCommentRv.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * 设置标签View
     */
    private void setLabelView(List<TagBean> labelBeans) {
        //形象标签
        mTagsLl.removeAllViews();
        int[] backs = {R.drawable.shape_tag_one, R.drawable.shape_tag_two, R.drawable.shape_tag_three};
        if (labelBeans != null && labelBeans.size() > 0) {
            for (int i = 0; i < labelBeans.size(); i++) {
                @SuppressLint("InflateParams")
                View view = LayoutInflater.from(getContext()).inflate(R.layout.item_tag_user_info_layout, null);
                TextView textView = view.findViewById(R.id.content_tv);
                textView.setText(labelBeans.get(i).t_label_name);
                Random random = new Random();
                int index = random.nextInt(backs.length);
                textView.setBackgroundResource(backs[index]);
                if (i != 0) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = 20;
                    textView.setLayoutParams(params);
                }
                mTagsLl.addView(textView);
            }
            if (mTagsLl.getChildCount() > 0) {
                mTagsLl.setVisibility(View.VISIBLE);
            }
        }
    }


}
