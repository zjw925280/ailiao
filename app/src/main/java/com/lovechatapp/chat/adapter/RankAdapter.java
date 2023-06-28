package com.lovechatapp.chat.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.RankBean;
import com.lovechatapp.chat.fragment.RankFragment;
import com.lovechatapp.chat.view.recycle.OnItemClickListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 榜单Adapter
 */
public class RankAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Activity mContext;

    private final DecimalFormat decimalFormat = new DecimalFormat("00");

    private final RankFragment.RankType rankType;

    private List<RankBean> mBeans = new ArrayList<>();

    private boolean rewardMode;

    private OnItemClickListener onItemClickListener;

    public RankAdapter(Activity context, RankFragment.RankType rankType) {
        mContext = context;
        this.rankType = rankType;
    }

    public final void loadData(List<RankBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    public final void setRewardMode(boolean rewardMode) {
        this.rewardMode = rewardMode;
    }

    public final void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_beauty_rank_recycler_content_layout, parent, false);
        ContentViewHolder contentViewHolder = new ContentViewHolder(itemView);
        boolean mFromCost = rankType == RankFragment.RankType.Consumption;
        contentViewHolder.mGoldTv.setCompoundDrawablesRelativeWithIntrinsicBounds(
                mFromCost ? R.drawable.rank_cost : R.drawable.rank_hot, 0, 0, 0);
        return contentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        final RankBean bean = mBeans.get(position);

        ContentViewHolder mHolder = (ContentViewHolder) holder;

        //排名
        mHolder.mNumberTv.setText(decimalFormat.format(position + 4));

        //头像
        Glide.with(mContext)
                .load(bean.t_handImg)
                .error(R.drawable.default_head_img)
                .transform(new CircleCrop())
                .into(mHolder.mHeadIv);

        //昵称
        mHolder.mNickTv.setText(bean.t_nickName);

        //ID
        mHolder.mIdTv.setText(String.format("ID: %s", bean.t_idcard));

        //神秘人隐藏ID
        mHolder.mIdTv.setVisibility(bean.t_rank_switch == 1 ? View.GONE : View.VISIBLE);

        //邀请榜用*号代替昵称
        if (rankType == RankFragment.RankType.Invitation) {
            if (bean.t_nickName != null && bean.t_nickName.length() > 0) {
                mHolder.mNickTv.setText(bean.t_nickName.substring(0, 1));
                mHolder.mNickTv.append("***");
            }
            mHolder.mIdTv.setVisibility(View.GONE);
        }

        //距离上一名花瓣数量
        String goldGapText = String.format(Locale.CHINA, mContext.getString(R.string.gold_gap), bean.off_gold + "");
        mHolder.mGoldTv.setText(goldGapText);

        //奖励
        mHolder.mRewardTv.setVisibility(rewardMode ? View.VISIBLE : View.GONE);
        mHolder.mRewardTv.setText(String.format("奖励: %s", bean.t_rank_gold));

        //奖励领取状态
        mHolder.mRewardStateTv.setVisibility(rewardMode ? View.VISIBLE : View.GONE);
        mHolder.mRewardStateTv.setText(bean.t_is_receive == 1 ? "已领取" : "未领取");
        mHolder.mRewardStateTv.setBackgroundResource(bean.t_is_receive == 1 ?
                R.drawable.corner_solid_graye7 : R.drawable.corner_purple);
        mHolder.mRewardStateTv.setTextColor(bean.t_is_receive == 1 ? 0xff999999 : 0xffffffff);

        //跳转到用户信息页面
        mHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, bean, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {

        TextView mNumberTv;
        ImageView mHeadIv;
        TextView mNickTv;
        TextView mGoldTv;
        TextView mRewardTv;
        TextView mRewardStateTv;
        TextView mIdTv;

        ContentViewHolder(View itemView) {
            super(itemView);
            mIdTv = itemView.findViewById(R.id.id_tv);
            mRewardTv = itemView.findViewById(R.id.reward_tv);
            mRewardStateTv = itemView.findViewById(R.id.reward_state_tv);
            mGoldTv = itemView.findViewById(R.id.gold_tv);
            mNumberTv = itemView.findViewById(R.id.number_tv);
            mHeadIv = itemView.findViewById(R.id.head_iv);
            mNickTv = itemView.findViewById(R.id.nick_tv);
        }
    }

}