package com.lovechatapp.chat.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.activity.PersonInfoActivity;
import com.lovechatapp.chat.activity.ActiveCommentActivity;
import com.lovechatapp.chat.activity.ReportActivity;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseActivity;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.ActiveBean;
import com.lovechatapp.chat.bean.ActiveFileBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.constant.Constant;
import com.lovechatapp.chat.dialog.LookResourceDialog;
import com.lovechatapp.chat.glide.GlideCircleTransform;
import com.lovechatapp.chat.glide.GlideRoundTransform;
import com.lovechatapp.chat.helper.IMHelper;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.TimeUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.view.ExpandTextView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述  动态RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/8/29
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ActiveRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseActivity mContext;

    private List<ActiveBean<ActiveFileBean>> mBeans = new ArrayList<>();

    private int headSize;

    private OnCommonListener<Boolean> updateListener;

    public ActiveRecyclerAdapter(BaseActivity context) {
        mContext = context;
        headSize = DevicesUtil.dp2px(mContext, 40);
        updateListener = new OnCommonListener<Boolean>() {
            @Override
            public void execute(Boolean aBoolean) {
                if (aBoolean) {
                    notifyDataSetChanged();
                }
            }
        };
    }

    public void loadData(List<ActiveBean<ActiveFileBean>> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_active_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final ActiveBean<ActiveFileBean> bean = mBeans.get(position);

        final MyViewHolder mHolder = (MyViewHolder) holder;

        if (bean != null) {

            //头像
            Glide.with(mContext)
                    .load(bean.t_handImg)
                    .error(R.drawable.default_head)
                    .override(headSize)
                    .transform(new GlideCircleTransform(mContext))
                    .into(mHolder.mHeadIv);

            //昵称
            mHolder.mNickTv.setText(bean.t_nickName);

            //年龄
            mHolder.mAgeTv.setText(String.valueOf(bean.t_age));

            //性别
            mHolder.mAgeTv.setSelected(bean.t_sex == 1);

            //时间
            mHolder.mTimeTv.setVisibility(View.GONE);
            if (bean.t_create_time > 0) {
                mHolder.mTimeTv.setText(TimeUtil.getTimeStr(bean.t_create_time));
                mHolder.mTimeTv.setVisibility(View.VISIBLE);
            }

            //位置
            mHolder.mPositionTv.setText(bean.t_address);
            mHolder.mPositionTv.setVisibility(!TextUtils.isEmpty(bean.t_address) ? View.VISIBLE : View.GONE);

            //点赞
            mHolder.mHeartTv.setText(String.valueOf(bean.praiseCount));

            //是否点赞 0 未点赞 1 已点赞
            mHolder.mHeartIv.setSelected(bean.isPraise == 1);

            //评论
            mHolder.mCommentTv.setText(String.valueOf(bean.commentCount));

            //是否关注 0.未关注 1.已关注
            mHolder.mFocusTv.setText(bean.isFollow == 1 ? R.string.have_focus : R.string.focus);
            mHolder.mFocusTv.setSelected(bean.isFollow == 1);

            //-------处理图片、文字内容------------
            dealContent(mHolder, bean);

            //------------------点击事件---------------
            //评论
            mHolder.mCommentLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.dynamicId > 0) {
                        Intent intent = new Intent(mContext, ActiveCommentActivity.class);
                        intent.putExtra(Constant.ACTIVE_ID, bean.dynamicId);
                        intent.putExtra(Constant.ACTOR_ID, bean.t_id);
                        intent.putExtra(Constant.COMMENT_NUMBER, bean.commentCount);
                        mContext.startActivity(intent);
                    }
                }
            });

            //点赞
            mHolder.mHeartLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.dynamicId > 0 && !mHolder.mHeartIv.isSelected()) {
                        addHeart(mHolder.mHeartTv, mHolder.mHeartIv, bean.dynamicId);
                    }
                }
            });

            //撩她文字聊天
            mHolder.mChatHerTv.setVisibility(View.VISIBLE);
            if (bean.t_id == AppManager.getInstance().getUserInfo().t_id
                    || AppManager.getInstance().getUserInfo().isSameSex(bean.t_sex)) {
                mHolder.mChatHerTv.setVisibility(View.INVISIBLE);
            }
            mHolder.mChatHerTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int userId = bean.t_id;
                    if (userId > 0) {
                        IMHelper.toChat(mContext, bean.t_nickName, userId, bean.t_sex);
                    }
                }
            });

            //关注
            mHolder.mFocusTv.setVisibility(bean.t_id != AppManager.getInstance().getUserInfo().t_id ? View.VISIBLE : View.INVISIBLE);
            mHolder.mFocusTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.t_id > 0) {
                        String text = mHolder.mFocusTv.getText().toString().trim();
                        if (text.equals(mContext.getResources().getString(R.string.focus))) {//未关注
                            saveFollow(bean.t_id, mHolder.mFocusTv);
                        } else {
                            cancelFollow(bean.t_id, mHolder.mFocusTv);
                        }
                    }
                }
            });

            //跳转到信息
            mHolder.mHeadIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PersonInfoActivity.start(mContext, bean.t_id);
                }
            });

            //举报
            mHolder.mMoreIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean.t_id > 0) {
                        showReportDialog(bean.t_id);
                    }
                }
            });
        }
    }

    /**
     * 添加关注
     */
    private void saveFollow(int actorId, final TextView mFocusTv) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());//关注人
        paramMap.put("coverFollowUserId", String.valueOf(actorId));//	被关注人
        OkHttpUtils.post().url(ChatApi.SAVE_FOLLOW())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.INSTANCE.showToast(mContext, R.string.focus_success);
                    mFocusTv.setText(R.string.have_focus);
                    mFocusTv.setSelected(true);
                }
            }
        });
    }

    /**
     * 取消关注
     */
    private void cancelFollow(int actorId, final TextView mFocusTv) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());//关注人
        paramMap.put("coverFollow", String.valueOf(actorId));//	被关注人
        OkHttpUtils.post().url(ChatApi.DEL_FOLLOW())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    ToastUtil.INSTANCE.showToast(mContext, R.string.cancel_focus_success);
                    mFocusTv.setText(R.string.focus);
                    mFocusTv.setSelected(false);
                }
            }
        });
    }

    /**
     * 添加点赞
     */
    private void addHeart(final TextView heart_tv, final ImageView heart_iv, int dynamicId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", mContext.getUserId());
        paramMap.put("dynamicId", String.valueOf(dynamicId));
        OkHttpUtils.post().url(ChatApi.GIVE_THE_THUMB_UP())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    heart_iv.setSelected(true);
                    String number = heart_tv.getText().toString().trim();
                    String last = String.valueOf(Integer.parseInt(number) + 1);
                    heart_tv.setText(last);
                    ToastUtil.INSTANCE.showToast(mContext, R.string.heart_success);
                }
            }
        });
    }

    /**
     * 处理图片
     */
    private void dealContent(final MyViewHolder mHolder, final ActiveBean<ActiveFileBean> bean) {
        //---------------文字---------------------------
        String contentText = bean.t_content;
        if (!TextUtils.isEmpty(contentText)) {
            mHolder.mTextContentLl.setVisibility(View.VISIBLE);
            mHolder.mExpandTv.setText(contentText, false, new ExpandTextView.Callback() {
                @Override
                public void onExpand() {
                    // 展开状态，比如：显示“收起”按钮
                    mHolder.mSeeMoreTv.setVisibility(View.VISIBLE);
                    mHolder.mSeeMoreTv.setText(mContext.getResources().getString(R.string.collapse));
                }

                @Override
                public void onCollapse() {
                    // 收缩状态，比如：显示“查看”按钮
                    mHolder.mSeeMoreTv.setVisibility(View.VISIBLE);
                    mHolder.mSeeMoreTv.setText(mContext.getResources().getString(R.string.see_all));
                }

                @Override
                public void onLoss() {
                    // 不满足展开的条件，比如：隐藏“全文”按钮
                    mHolder.mSeeMoreTv.setVisibility(View.GONE);
                }
            });
            //点击收起
            mHolder.mSeeMoreTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = mHolder.mSeeMoreTv.getText().toString().trim();
                    //查看全部
                    if (text.equals(mContext.getResources().getString(R.string.see_all))) {
                        //切换状态
                        mHolder.mExpandTv.setChanged(true);
                        mHolder.mSeeMoreTv.setText(mContext.getResources().getString(R.string.collapse));
                    } else {
                        mHolder.mExpandTv.setChanged(false);
                        mHolder.mSeeMoreTv.setText(mContext.getResources().getString(R.string.see_all));
                    }
                }
            });
        } else {
            mHolder.mTextContentLl.setVisibility(View.GONE);
        }

        //--------------图片视频-----------------------
        final List<ActiveFileBean> fileBeans = bean.dynamicFiles;

        if (fileBeans != null && fileBeans.size() > 0) {

            mHolder.mImageFl.setVisibility(View.VISIBLE);

            //-------------------如果只有一个文件-------------------
            if (fileBeans.size() == 1) {

                final ActiveFileBean fileBean = fileBeans.get(0);

                mHolder.mTwoImageLl.setVisibility(View.GONE);
                mHolder.mThreeRv.setVisibility(View.GONE);
                mHolder.mOneImageFl.setVisibility(View.VISIBLE);

                //显示图片
                String imgUrl = fileBean.t_file_type == 1 ? fileBean.t_cover_img_url : fileBean.t_file_url;

                if (!TextUtils.isEmpty(imgUrl)) {
                    //私密且未消费
                    boolean needPay = fileBean.judgePrivate(bean.t_id);
                    mHolder.mOneLockFl.setVisibility(needPay ? View.VISIBLE : View.GONE);
                    loadImg(needPay,
                            imgUrl,
                            DevicesUtil.dp2px(mContext, 180),
                            DevicesUtil.dp2px(mContext, 240),
                            mHolder.mOneImageIv);
                }

                //视频时长
                mHolder.mVideoTimeTv.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(fileBean.t_video_time) && fileBean.t_file_type == 1) {
                    mHolder.mVideoTimeTv.setVisibility(View.VISIBLE);
                    mHolder.mVideoTimeTv.setText(fileBean.t_video_time);
                }

                //点击事件
                mHolder.mOneImageFl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LookResourceDialog.showActive(mContext, fileBeans, bean.t_id, 0);
                    }
                });

            } else if (fileBeans.size() == 2) {//-------------------2个文件(图片)------------------

                mHolder.mOneImageFl.setVisibility(View.GONE);
                mHolder.mThreeRv.setVisibility(View.GONE);
                mHolder.mTwoImageLl.setVisibility(View.VISIBLE);

                //第一张
                final ActiveFileBean fileBean = fileBeans.get(0);
                boolean needPay = fileBean.judgePrivate(bean.t_id);
                mHolder.mTwoLockOneIv.setVisibility(needPay ? View.VISIBLE : View.GONE);
                loadImg(needPay,
                        fileBean.t_file_url,
                        DevicesUtil.dp2px(mContext, 126),
                        DevicesUtil.dp2px(mContext, 135),
                        mHolder.mTwoImageOneIv);
                mHolder.mTwoImageOneFl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LookResourceDialog.showActive(mContext, fileBeans, bean.t_id, 0);
                    }
                });

                //第二张
                final ActiveFileBean fileBeanTwo = fileBeans.get(1);
                boolean needPay2 = fileBeanTwo.judgePrivate(bean.t_id);
                mHolder.mTwoLockTwoIv.setVisibility(needPay2 ? View.VISIBLE : View.GONE);
                loadImg(needPay2,
                        fileBeanTwo.t_file_url,
                        DevicesUtil.dp2px(mContext, 126),
                        DevicesUtil.dp2px(mContext, 135),
                        mHolder.mTwoImageTwoIv);
                mHolder.mTwoImageTwoFl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LookResourceDialog.showActive(mContext, fileBeans, bean.t_id, 1);
                    }
                });

            } else {//-----------------大于等于3个文件(图片)-----------------

                mHolder.mOneImageFl.setVisibility(View.GONE);
                mHolder.mTwoImageLl.setVisibility(View.GONE);
                mHolder.mThreeRv.setVisibility(View.VISIBLE);

                GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
                ActiveImagesRecyclerAdapter adapter = new ActiveImagesRecyclerAdapter(mContext);
                mHolder.mThreeRv.setLayoutManager(layoutManager);
                mHolder.mThreeRv.setAdapter(adapter);
                adapter.setOnImageItemClickListener(new ActiveImagesRecyclerAdapter.OnImageItemClickListener() {
                    @Override
                    public void onImageItemClick(int position, ActiveFileBean fileBean) {
                        LookResourceDialog.showActive(mContext, fileBeans, bean.t_id, position);
                    }
                });
                adapter.loadData(fileBeans, bean.t_id);
            }
        } else {
            mHolder.mImageFl.setVisibility(View.GONE);
        }
    }

    private void loadImg(boolean fade, String url, int overWidth, int overHeight, ImageView imageView) {
        if (fade) {
            Glide.with(mContext)
                    .load(url)
                    .error(R.drawable.default_back)
                    .override(overWidth, overHeight)
                    .transform(
                            new GlideRoundTransform(6),
                            new CenterCrop(),
                            new BlurTransformation(100, 2))
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(url)
                    .error(R.drawable.default_back)
                    .override(overWidth, overHeight)
                    .centerCrop()
                    .transform(
                            new GlideRoundTransform(6),
                            new CenterCrop())
                    .into(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mHeadIv;
        TextView mNickTv;
        TextView mAgeTv;
        TextView mTimeTv;
        ImageView mChatHerTv;
        //内容相关
        TextView mContentTv;
        FrameLayout mImageFl;
        //一张图
        FrameLayout mOneLockFl;
        FrameLayout mOneImageFl;
        ImageView mOneImageIv;
        //2张图
        LinearLayout mTwoImageLl;
        FrameLayout mTwoImageOneFl;
        ImageView mTwoImageOneIv;
        ImageView mTwoLockOneIv;
        FrameLayout mTwoImageTwoFl;
        ImageView mTwoImageTwoIv;
        ImageView mTwoLockTwoIv;
        //大于等于三张
        RecyclerView mThreeRv;
        //点赞
        View mHeartLl;
        ImageView mHeartIv;
        TextView mHeartTv;
        //评论
        View mCommentLl;
        ImageView mCommentIv;
        TextView mCommentTv;
        ImageView mMoreIv;
        //关注
        TextView mFocusTv;
        //位置
        TextView mPositionTv;
        //查看更多
        LinearLayout mTextContentLl;
        TextView mSeeMoreTv;
        ExpandTextView mExpandTv;
        //视频时长
        TextView mVideoTimeTv;

        MyViewHolder(View itemView) {
            super(itemView);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
            mAgeTv = itemView.findViewById(R.id.age_tv);
            mTimeTv = itemView.findViewById(R.id.time_tv);
            mChatHerTv = itemView.findViewById(R.id.chat_her_tv);
            mContentTv = itemView.findViewById(R.id.content_tv);
            mImageFl = itemView.findViewById(R.id.image_fl);
            mOneLockFl = itemView.findViewById(R.id.one_lock_fl);
            mOneImageFl = itemView.findViewById(R.id.one_image_fl);
            mOneImageIv = itemView.findViewById(R.id.one_image_iv);
            mTwoImageLl = itemView.findViewById(R.id.two_image_ll);
            mTwoImageOneIv = itemView.findViewById(R.id.two_image_one_iv);
            mTwoLockOneIv = itemView.findViewById(R.id.two_lock_one_iv);
            mTwoImageTwoIv = itemView.findViewById(R.id.two_image_two_iv);
            mTwoLockTwoIv = itemView.findViewById(R.id.two_lock_two_iv);
            mThreeRv = itemView.findViewById(R.id.three_rv);
            mHeartLl = itemView.findViewById(R.id.heart_ll);
            mHeartIv = itemView.findViewById(R.id.heart_iv);
            mHeartTv = itemView.findViewById(R.id.heart_tv);
            mCommentLl = itemView.findViewById(R.id.comment_ll);
            mCommentIv = itemView.findViewById(R.id.comment_iv);
            mCommentTv = itemView.findViewById(R.id.comment_tv);
            mMoreIv = itemView.findViewById(R.id.more_iv);
            mFocusTv = itemView.findViewById(R.id.focus_tv);
            mPositionTv = itemView.findViewById(R.id.position_tv);
            mTextContentLl = itemView.findViewById(R.id.text_content_ll);
            mSeeMoreTv = itemView.findViewById(R.id.see_more_tv);
            mExpandTv = itemView.findViewById(R.id.expand_tv);
            mVideoTimeTv = itemView.findViewById(R.id.video_time_tv);
            mTwoImageOneFl = itemView.findViewById(R.id.two_image_one_fl);
            mTwoImageTwoFl = itemView.findViewById(R.id.two_image_two_fl);
        }
    }

    /**
     * 显示头像选择dialog
     */
    private void showReportDialog(int mActorId) {
        final Dialog mDialog = new Dialog(mContext, R.style.DialogStyle_Dark_Background);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_active_more_layout, null);
        setDialogView(view, mDialog, mActorId);
        mDialog.setContentView(view);
        Point outSize = new Point();
        mContext.getWindowManager().getDefaultDisplay().getSize(outSize);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = outSize.x;
            window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.BottomPopupAnimation); // 添加动画
        }
        mDialog.setCanceledOnTouchOutside(true);
        if (!mContext.isFinishing()) {
            mDialog.show();
        }
    }

    /**
     * 设置头像选择dialog的view
     */
    private void setDialogView(View view, final Dialog mDialog, final int mActorId) {
        TextView cancel_tv = view.findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        //举报
        TextView report_tv = view.findViewById(R.id.report_tv);
        report_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ReportActivity.class);
                intent.putExtra(Constant.ACTOR_ID, mActorId);
                mContext.startActivity(intent);
                mDialog.dismiss();
            }
        });

    }

}
