package com.lovechatapp.chat.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.PersonInfoActivity;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.NearBean;
import com.lovechatapp.chat.glide.GlideRoundTransform;
import com.lovechatapp.chat.net.FocusRequester;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：距离recycler的adapter
 * 作者：
 * 创建时间：2018/11/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class DistanceRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<NearBean> mBeans = new ArrayList<>();
    private String[] onlineText = {"在线", "忙碌", "离线"};

    public DistanceRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<NearBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_distance_recycler_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final NearBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {

            //头像
            Glide.with(mContext)
                    .load(bean.t_handImg)
                    .override(DevicesUtil.dp2px(mContext, 62))
                    .error(R.drawable.default_head)
                    .transform(new CenterCrop(), new GlideRoundTransform(5))
                    .into(mHolder.mHeadIv);

            //nick
            mHolder.mNickTv.setText(bean.t_nickName);

            //vip
            mHolder.mNickTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            if (bean.t_is_vip == 0) {
                mHolder.mNickTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.vip_icon, 0);
            }

            //城市
            mHolder.mDistanceTv.setText(bean.t_city);

            //在线状态  0.空闲 1.忙碌 2.离线
            mHolder.mOnlineTv.setText(onlineText[bean.t_onLine]);

            //性别 0.女 1.男
            int sexDrawId = bean.t_sex == 0 ? R.drawable.near_sex_women : R.drawable.near_sex_man;
            mHolder.mAgeTv.setCompoundDrawablesRelativeWithIntrinsicBounds(sexDrawId, 0, 0, 0);

            //年龄
            mHolder.mAgeTv.setText(String.format("%s岁", bean.t_age));

            if (bean.t_height > 0) {
                mHolder.mAgeTv.append(String.format("  |  %scm", bean.t_height));
            }

            //职业
            if (!TextUtils.isEmpty(bean.t_vocation)) {
                mHolder.mAgeTv.append(String.format("  |  %s", bean.t_vocation));
            }

            //签名
            if (!TextUtils.isEmpty(bean.t_autograph)) {
                mHolder.mSignTv.setText(bean.t_autograph);
            } else {
                mHolder.mSignTv.setText(R.string.lazy);
            }

            //关注
            mHolder.mFollowIv.setImageResource(bean.isFollow == 1 ? R.drawable.follow_selected : R.drawable.follow_unselected);
            mHolder.mFollowIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    boolean follow = bean.isFollow == 1;
                    new FocusRequester() {
                        @Override
                        public void onSuccess(BaseResponse response, boolean focus) {
                            if (mContext.isFinishing())
                                return;
                            super.onSuccess(response, focus);
                            bean.isFollow = focus ? 1 : 0;
                            ((ImageView) v).setImageResource(focus ? R.drawable.follow_selected : R.drawable.follow_unselected);
                        }
                    }.focus(bean.t_id, !follow);
                }
            });

            //点击事件
            mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PersonInfoActivity.start(mContext, bean.t_id);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView mHeadIv;
        private TextView mDistanceTv;
        private TextView mNickTv;
        private TextView mOnlineTv;
        private TextView mAgeTv;
        private TextView mSignTv;
        private ImageView mFollowIv;

        MyViewHolder(View itemView) {
            super(itemView);
            mFollowIv = itemView.findViewById(R.id.follow_iv);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mDistanceTv = itemView.findViewById(R.id.distance_tv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mOnlineTv = itemView.findViewById(R.id.online_tv);
            mAgeTv = itemView.findViewById(R.id.age_tv);
            mSignTv = itemView.findViewById(R.id.sign_tv);
        }
    }

}