package com.lovechatapp.chat.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.bean.ActiveFileBean;
import com.lovechatapp.chat.dialog.LookResourceDialog;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.view.LoadingView;

import java.util.Arrays;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：查看动态图片Fragment页面
 * 作者：
 * 创建时间：2018/1/5
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PhotoFragment extends Fragment {

    private PhotoView mContentPv;
    private FrameLayout mLockFl;
    private ImageView mCoverIv;
    private View mCoverV;
    private LoadingView mLoadingLv;
    private BaseActivity mContext;
    private ActiveFileBean fileBean;
    private int actorId;

    public final void setFileBean(ActiveFileBean fileBean, int actorId) {
        this.fileBean = fileBean;
        this.actorId = actorId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mContext = (BaseActivity) getActivity();
        return inflater.inflate(R.layout.fragment_photo_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContentPv = view.findViewById(R.id.content_pv);
        mLockFl = view.findViewById(R.id.lock_fl);
        mCoverIv = view.findViewById(R.id.cover_iv);
        mCoverV = view.findViewById(R.id.cover_v);
        mLoadingLv = view.findViewById(R.id.loading_lv);

        loadPicture(fileBean, actorId);
    }

    protected void loadPicture(final ActiveFileBean fileBean, final int actorId) {
        if (fileBean != null) {
            //锁
            if (fileBean.judgePrivate(actorId)) {
                mLockFl.setVisibility(View.VISIBLE);
                mCoverIv.setVisibility(View.VISIBLE);
                mCoverV.setVisibility(View.VISIBLE);
                ImageLoadHelper.glideShowImageWithFade(mContext, fileBean.t_file_url, mCoverIv);
                mLockFl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LookResourceDialog.showActive(mContext, Arrays.asList(fileBean), actorId, 0,
                                new OnCommonListener<ActiveFileBean>() {
                                    @Override
                                    public void execute(ActiveFileBean fileBean) {
                                        if (getActivity() != null && !getActivity().isFinishing()) {
                                            loadPicture(fileBean, actorId);
                                        }
                                    }
                                });
                    }
                });
            } else {
                mCoverV.setVisibility(View.GONE);
                mCoverIv.setVisibility(View.GONE);
                mLockFl.setVisibility(View.GONE);
            }

            final PhotoViewAttacher attacher = new PhotoViewAttacher(mContentPv);
            Glide.with(this)
                    .load(fileBean.t_file_url)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            attacher.update();
                            mLoadingLv.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(mContentPv);
            attacher.setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(ImageView view, float x, float y) {
                    if (getActivity() != null) {
                        getActivity().finish();
                    } else {
                        if (mContext != null) {
                            mContext.finish();
                        }
                    }
                }
            });
        }
    }
}