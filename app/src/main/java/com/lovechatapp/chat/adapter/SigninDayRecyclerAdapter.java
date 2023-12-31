package com.lovechatapp.chat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovechatapp.chat.R;
import com.lovechatapp.chat.bean.SigninDayBean;

import java.util.ArrayList;
import java.util.List;

public class SigninDayRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<SigninDayBean.RowsBean> mBeans = new ArrayList<>();

    public SigninDayRecyclerAdapter(Context context,List<SigninDayBean.RowsBean> beans) {
        mContext = context;
        mBeans=beans;
    }

    public void loadData(List<SigninDayBean.RowsBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.sign_in_day_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder mHolder = (MyViewHolder) holder;
        SigninDayBean.RowsBean rowsBean = mBeans.get(position);
        mHolder.tv_day_num.setText(rowsBean.getDay()+"");
        mHolder.tv_gole.setText("+"+rowsBean.getGold()+"");

        if (rowsBean.isSignIn()){
            mHolder.imge_num_bg.setImageResource(R.drawable.sign_in_day_item_y_bg);
        }else {
            mHolder.imge_num_bg.setImageResource(R.drawable.sign_in_day_item_h_bg);
        }
    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_gole;
        TextView tv_day_num;
      ImageView imge_num_bg;
        MyViewHolder(View itemView) {
            super(itemView);
            tv_day_num = itemView.findViewById(R.id.tv_day_num);
            imge_num_bg = itemView.findViewById(R.id.imge_num_bg);
            tv_gole = itemView.findViewById(R.id.tv_gole);

        }
    }

}
