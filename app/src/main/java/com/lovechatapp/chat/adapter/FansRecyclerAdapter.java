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
import com.lovechatapp.chat.activity.DateCreateActivity;
import com.lovechatapp.chat.activity.PersonInfoActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.FansBean;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.glide.GlideRoundTransform;
import com.lovechatapp.chat.helper.IMHelper;
import com.lovechatapp.chat.net.AudioVideoRequester;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：粉丝RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/8/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class FansRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<FansBean> mBeans = new ArrayList<>();
    private String[] onlineText = {"在线", "忙碌", "离线"};

    public FansRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<FansBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_fans_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final FansBean bean = mBeans.get(position);
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
//            if (!TextUtils.isEmpty(bean.t_autograph)) {
//                mHolder.mSignTv.setText(bean.t_autograph);
//            } else {
//                mHolder.mSignTv.setText(R.string.lazy);
//            }
            mHolder.mSignTv.setText(String.format("财富值: %s", bean.balance));

            //点击事件
            mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PersonInfoActivity.start(mContext, bean.t_id);
                }
            });

            //Im聊天
            mHolder.mChatTextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IMHelper.toChat(mContext, bean.t_nickName, bean.t_id, bean.t_sex);
                }
            });
            //约会
            mHolder.date_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int otherId = mContext.getIntent().getIntExtra(Constant.ACTOR_ID, 0);
                    DateCreateActivity.startActivity(mContext, String.valueOf(bean.t_id), String.valueOf( String.valueOf(AppManager.getInstance().getUserInfo().t_id+10000)), bean.t_nickName);

                }
            });

            //音视频聊天
            mHolder.mChatVideoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AudioVideoRequester(mContext,
                            false,
                            bean.t_id).executeVideo();
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
        private TextView mNickTv;
        private TextView mOnlineTv;
        private TextView mAgeTv;
        private TextView mSignTv;
        private View mChatTextBtn;
        private View mChatVideoBtn;
        private View date_btn;
        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mOnlineTv = itemView.findViewById(R.id.online_tv);
            mAgeTv = itemView.findViewById(R.id.age_tv);
            mSignTv = itemView.findViewById(R.id.sign_tv);
            mChatTextBtn = itemView.findViewById(R.id.chat_text_btn);
            mChatVideoBtn = itemView.findViewById(R.id.chat_video_btn);
            date_btn= itemView.findViewById(R.id.date_btn);

        }
    }
}