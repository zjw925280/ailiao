package com.lovechatapp.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.SiginBean;
import com.lovechatapp.chat.glide.GlideCircleTransform;
import com.lovechatapp.chat.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class SigninDayFriendRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<SiginBean.SiginInRecordBean> mBeans = new ArrayList<>();

    public SigninDayFriendRecyclerAdapter(Context context,List<SiginBean.SiginInRecordBean> beans) {
        mContext = context;
        mBeans = beans;

    }

    public void loadData(List<SiginBean.SiginInRecordBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.sign_in_day_friend_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder mHolder = (MyViewHolder) holder;
        SiginBean.SiginInRecordBean siginInRecordBean = mBeans.get(position);
        mHolder.tv_friend_head.setText(siginInRecordBean.getNickName());
        Glide.with(mContext)
                .load(siginInRecordBean.getHandImg())
                .error(R.drawable.default_head_img)
                .transform(new GlideCircleTransform(mContext))
                .into(mHolder.imge_head);
        mHolder.tv_time.setText(TimeUtil.getFormatYMD(siginInRecordBean.getSignInTime())+"已签到");

    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_friend_head;
        ImageView imge_head;
        TextView    tv_time;
        MyViewHolder(View itemView) {
            super(itemView);
            tv_friend_head = itemView.findViewById(R.id.tv_friend_head);
            imge_head = itemView.findViewById(R.id.imge_head);
            tv_time = itemView.findViewById(R.id.tv_time);

        }
    }

}