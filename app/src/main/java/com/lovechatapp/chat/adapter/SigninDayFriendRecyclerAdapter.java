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
import com.lovechatapp.chat.glide.GlideCircleTransform;

import java.util.ArrayList;
import java.util.List;

public class SigninDayFriendRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<String> mBeans = new ArrayList<>();

    public SigninDayFriendRecyclerAdapter(Context context) {
        mContext = context;
    }

    public void loadData(List<String> beans) {
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
        mHolder.tv_friend_head.setText(mBeans.get(position));
        Glide.with(mContext)
                .load(R.mipmap.bg_home_date)
                .error(R.drawable.default_head_img)
                .transform(new GlideCircleTransform(mContext))
                .into(mHolder.imge_head);

    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_friend_head;
        ImageView imge_head;
        MyViewHolder(View itemView) {
            super(itemView);
            tv_friend_head = itemView.findViewById(R.id.tv_friend_head);
            imge_head = itemView.findViewById(R.id.imge_head);

        }
    }

}