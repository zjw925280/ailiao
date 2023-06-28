package com.lovechatapp.chat.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.ActorVideoPlayActivity;
import com.lovechatapp.chat.activity.PhotoActivity;
import com.lovechatapp.chat.bean.AlbumBean;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.dialog.LookResourceDialog;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.listener.OnCommonListener;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：主播资料下方视频/图片RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class InfoVideoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private int mActorId;
    private List<AlbumBean> mBeans = new ArrayList<>();

    public InfoVideoRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<AlbumBean> beans, int actorId) {
        mBeans = beans;
        mActorId = actorId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_info_video_recycler_layout,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AlbumBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;
        if (bean != null) {
            //标题
            if (!TextUtils.isEmpty(bean.t_title)) {
                mHolder.mTitleTv.setText(bean.t_title);
                mHolder.mTitleTv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mTitleTv.setVisibility(View.GONE);
            }
            //昵称
            if (!TextUtils.isEmpty(bean.t_nickName)) {
                mHolder.mNickTv.setText(bean.t_nickName);
                mHolder.mNickTv.setVisibility(View.VISIBLE);
            } else {
                mHolder.mNickTv.setVisibility(View.GONE);
            }
            //加载封面
            final int fileType = bean.t_file_type;
            //是否私密
            final int mPrivate = bean.t_is_private;
            final int isSee = bean.is_see;//0.未查看1.已查看
            if (mPrivate == 1 && isSee == 0) {//是否私密：0.否1.是
                mHolder.mLockFl.setVisibility(View.VISIBLE);
                mHolder.mPlayIv.setVisibility(View.GONE);
                if (fileType == 0) {//0.图片
                    ImageLoadHelper.glideShowCornerImageWithFade(mContext, bean.t_addres_url, mHolder.mContentIv);
                } else {
                    ImageLoadHelper.glideShowCornerImageWithFade(mContext, bean.t_video_img, mHolder.mContentIv);
                }
                //钱
                int gold = bean.t_money;
                if (gold > 0) {
                    String content = String.valueOf(gold) + mContext.getResources().getString(R.string.gold_des_one);
                    mHolder.mGoldTv.setText(content);
                    mHolder.mGoldTv.setVisibility(View.VISIBLE);
                }
            } else {
                mHolder.mLockFl.setVisibility(View.GONE);
                mHolder.mGoldTv.setVisibility(View.GONE);
                String imageUrl;
                if (fileType == 0) {
                    imageUrl = bean.t_addres_url;
                    mHolder.mPlayIv.setVisibility(View.GONE);
                } else {
                    imageUrl = bean.t_video_img;
                    mHolder.mPlayIv.setVisibility(View.VISIBLE);
                }
                ImageLoadHelper.glideShowCornerImageWithUrl(mContext, imageUrl, mHolder.mContentIv);
            }
            //点击事件
            mHolder.mContentFl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LookResourceDialog.showAlbum(mContext, bean, mActorId, new OnCommonListener<Boolean>() {
                        @Override
                        public void execute(Boolean aBoolean) {
                            if (aBoolean) {

                                bean.is_see = 1;
                                notifyDataSetChanged();

                                if (fileType == 0) {//0.图片
                                    Intent intent = new Intent(mContext, PhotoActivity.class);
                                    intent.putExtra(Constant.IMAGE_URL, bean.t_addres_url);
                                    mContext.startActivity(intent);
                                } else if (fileType == 1) {//视频
                                    ActorVideoPlayActivity.start(mContext, mActorId, bean.t_addres_url);
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

   static class MyViewHolder extends RecyclerView.ViewHolder {

        View mContentFl;
        ImageView mContentIv;
        TextView mTitleTv;
        TextView mNickTv;
        TextView mGoldTv;
        ImageView mPlayIv;
        FrameLayout mLockFl;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentFl = itemView.findViewById(R.id.content_fl);
            mContentIv = itemView.findViewById(R.id.content_iv);
            mTitleTv = itemView.findViewById(R.id.title_tv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mGoldTv = itemView.findViewById(R.id.gold_tv);
            mPlayIv = itemView.findViewById(R.id.play_iv);
            mLockFl = itemView.findViewById(R.id.lock_fl);
        }
    }

}
