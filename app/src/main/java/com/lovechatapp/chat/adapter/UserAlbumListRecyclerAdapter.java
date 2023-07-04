package com.lovechatapp.chat.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.ActorVideoPlayActivity;
import com.lovechatapp.chat.activity.PhotoActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.AlbumBean;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.util.DevicesUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：用户资料相册列表页面adapter
 * 作者：
 * 创建时间：2018/10/27
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class UserAlbumListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;
    private List<AlbumBean> mBeans = new ArrayList<>();

    public UserAlbumListRecyclerAdapter(BaseActivity context) {
        mContext = context;
    }

    public void loadData(List<AlbumBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_user_album_list_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AlbumBean bean = mBeans.get(position);
        MyViewHolder mHolder = (MyViewHolder) holder;

        if (bean != null) {

            //审核状态
            mHolder.mStatusTv.setVisibility(View.GONE);
            if (bean.t_auditing_type == 0) {//审核中
                mHolder.mStatusTv.setVisibility(View.VISIBLE);
                mHolder.mStatusTv.setText("审核中");
            }

            //约豆判断是否收费相册，免费视频可设为视频封面
            mHolder.moreBtn.setVisibility(View.GONE);
            if (bean.t_money > 0) {
                mHolder.moreBtn.setVisibility(View.VISIBLE);
                mHolder.moreBtn.setOnClickListener(null);
                mHolder.moreBtn.setText(String.format("%s 约豆", bean.t_money));
            } else if (bean.t_is_first == 1 && bean.t_file_type == 1) {
                mHolder.moreBtn.setVisibility(View.VISIBLE);
                mHolder.moreBtn.setText("视频封面");
            } else if (AppManager.getInstance().getUserInfo().isWomenActor()
                    && bean.t_file_type == 1
                    && bean.t_auditing_type == 1) {
                mHolder.moreBtn.setVisibility(View.VISIBLE);
                mHolder.moreBtn.setText("设为视频封面");
                mHolder.moreBtn.setOnClickListener(v -> new AlertDialog.Builder(mContext)
                        .setMessage("确认设为视频封面吗？")
                        .setPositiveButton(R.string.confirm, (dialog, which) -> setCoverVideo(bean))
                        .setNegativeButton(R.string.cancel, null)
                        .create().show());
            }

            //文件类型0.图片 1.视频
            final int fileType = bean.t_file_type;
            final String addressUrl = bean.t_addres_url;
            final String t_video_img = bean.t_video_img;
            if (fileType == 0) {
                mHolder.mPlayIv.setVisibility(View.GONE);
                int width = (DevicesUtil.getScreenW(mContext) - DevicesUtil.dp2px(mContext, 4)) / 3;
                int height = DevicesUtil.dp2px(mContext, 165);
                if (!TextUtils.isEmpty(addressUrl)) {
                    ImageLoadHelper.glideShowImageWithUrl(mContext, addressUrl, mHolder.mImageIv, width, height);
                }
            } else {
                mHolder.mPlayIv.setVisibility(View.VISIBLE);
                int width = (DevicesUtil.getScreenW(mContext) - DevicesUtil.dp2px(mContext, 4)) / 3;
                int height = DevicesUtil.dp2px(mContext, 165);
                if (!TextUtils.isEmpty(t_video_img)) {
                    ImageLoadHelper.glideShowImageWithUrl(mContext, t_video_img, mHolder.mImageIv, width, height);
                }
            }

            //点击事件
            mHolder.mContentFl.setOnClickListener(v -> {
                if (fileType == 1) {
                    ActorVideoPlayActivity.start(mContext, AppManager.getInstance().getUserInfo().t_id, addressUrl);
                } else {//图片
                    if (!TextUtils.isEmpty(addressUrl)) {
                        Intent intent = new Intent(mContext, PhotoActivity.class);
                        intent.putExtra(Constant.IMAGE_URL, addressUrl);
                        mContext.startActivity(intent);
                    }
                }
            });

            //删除
            mHolder.deleteBtn.setOnClickListener(v -> new AlertDialog.Builder(mContext)
                    .setMessage("确认删除吗？")
                    .setPositiveButton(R.string.confirm, (dialog, which) -> delete(bean))
                    .setNegativeButton(R.string.cancel, null)
                    .create().show());

        }
    }

    protected void delete(AlbumBean bean) {

    }

    protected void setCoverVideo(AlbumBean bean) {

    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        View mContentFl;
        ImageView mImageIv;
        ImageView mLockIv;
        ImageView mPlayIv;
        TextView mStatusTv;
        TextView moreBtn;
        View deleteBtn;

        MyViewHolder(View itemView) {
            super(itemView);
            mContentFl = itemView.findViewById(R.id.content_fl);
            mImageIv = itemView.findViewById(R.id.image_iv);
            mLockIv = itemView.findViewById(R.id.lock_iv);
            mPlayIv = itemView.findViewById(R.id.play_iv);
            mStatusTv = itemView.findViewById(R.id.status_tv);
            moreBtn = itemView.findViewById(R.id.more_btn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
        }
    }
}