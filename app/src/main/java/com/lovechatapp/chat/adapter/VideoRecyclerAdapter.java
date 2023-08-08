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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.AlbumBean;
import com.lovechatapp.chat.glide.GlideRoundTransform;
import com.lovechatapp.chat.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder mHolder = (MyViewHolder) holder;
        //加载封面
        Glide.with(mContext)
                .load(mBeans.get(position).t_video_img)
                .error(R.drawable.default_back)
                .transform(new CenterCrop(),
                        new GlideRoundTransform(2)
                )
                .into(mHolder.imge_imag);
        mHolder.tv_date.setText(TimeUtil.getFormatYM(mBeans.get(position).t_create_time));
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imge_imag;
        private TextView tv_date;

        MyViewHolder(View itemView) {
            super(itemView);
            imge_imag = itemView.findViewById(R.id.imge_imag);
            tv_date = itemView.findViewById(R.id.tv_date);

        }
    }

}

