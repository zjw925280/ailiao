package com.lovechatapp.chat.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.ActorVideoPlayActivity;
import com.lovechatapp.chat.activity.PhotoActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.bean.AlbumBean;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.dialog.LookResourceDialog;
import com.lovechatapp.chat.glide.GlideRoundTransform;
import com.lovechatapp.chat.helper.ImageLoadHelper;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class VideoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<AlbumBean> mBeans=new ArrayList<>();

    public VideoRecyclerAdapter(Context context) {
        mContext = context;
    }

    public void loadData(List<AlbumBean> mBeans) {
        this.mBeans=mBeans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_video_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,int position) {
        MyViewHolder mHolder = (MyViewHolder) holder;
//        //加载封面
//        Glide.with(mContext)
//                .load(mBeans.get(position).t_video_img)
//                .error(R.drawable.default_back)
//                .transform(new CenterCrop(),
//                        new GlideRoundTransform(2)
//                )
//                .into(mHolder.imge_imag);
        mHolder.mTitleTv.setText(TimeUtil.getFormatYM(mBeans.get(position).t_create_time));
//        mHolder.imge_imag.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActorVideoPlayActivity.start(mContext, AppManager.getInstance().getUserInfo().t_id, mBeans.get(position).t_addres_url);
//
//            }
//        });
       AlbumBean bean= mBeans.get(position);
        mHolder.mLockFl.setVisibility(View.GONE);
        mHolder.mGoldTv.setVisibility(View.GONE);

        //t_is_private 是否私密：0.否1.是
        //is_see 0.未查看1.已查看
        if (!bean.canSee()) {

            mHolder.mLockFl.setVisibility(View.VISIBLE);
            if (bean.t_money > 0) {
                String content = bean.t_money + mContext.getResources().getString(R.string.gold);
                mHolder.mGoldTv.setText(content);
                mHolder.mGoldTv.setVisibility(View.VISIBLE);
            }
            //加载封面
            Glide.with(mContext)
                    .load(bean.t_video_img)
                    .error(R.drawable.default_back)
                    .transform(new CenterCrop(),
                            new BlurTransformation(100, 2),
                            new GlideRoundTransform(2))
                    .into(mHolder.mContentIv);

            mHolder.mPlayIv.setVisibility(View.GONE);
        } else {
            //加载封面
            Glide.with(mContext)
                    .load(bean.t_video_img)
                    .error(R.drawable.default_back)
                    .transform(new CenterCrop(), new GlideRoundTransform(6))
                    .into(mHolder.mContentIv);
            mHolder.mPlayIv.setVisibility(View.VISIBLE);
        }
        //点击事件
        mHolder.mContentFl.setOnClickListener(v -> itemClick(position));
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView mContentIv;
        private TextView mTitleTv;
        private FrameLayout mContentFl;
        private TextView mNickTv;
        private TextView mGoldTv;
        private ImageView mPlayIv;
        private FrameLayout mLockFl;
        MyViewHolder(View itemView) {
            super(itemView);
            mContentIv = itemView.findViewById(R.id.content_iv);
            mTitleTv = itemView.findViewById(R.id.title_tv);
            mContentFl = itemView.findViewById(R.id.content_fl);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mGoldTv = itemView.findViewById(R.id.gold_tv);
            mPlayIv = itemView.findViewById(R.id.play_iv);
            mLockFl = itemView.findViewById(R.id.lock_fl);
        }
    }
    public void itemClick(int position) {

    }
}

