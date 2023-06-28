package com.lovechatapp.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lovechatapp.chat.R;
import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.base.BaseResponse;
import com.lovechatapp.chat.bean.CoverUrlBean;
import com.lovechatapp.chat.constant.ChatApi;
import com.lovechatapp.chat.net.AjaxCallback;
import com.lovechatapp.chat.net.NetCode;
import com.lovechatapp.chat.util.DevicesUtil;
import com.lovechatapp.chat.util.ParamUtil;
import com.lovechatapp.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：编辑个人资料封面RecyclerView的Adapter
 * 作者：
 * 创建时间：2018/6/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CoverRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
    private List<CoverUrlBean> mBeans = new ArrayList<>();

    public CoverRecyclerAdapter(Activity context) {
        mContext = context;
    }

    public void loadData(List<CoverUrlBean> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_edit_info_cover_recycler_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyViewHolder mHolder = (MyViewHolder) holder;

        final CoverUrlBean coverUrlBean = mBeans.get(position);

        Glide.with(mContext).load(coverUrlBean.t_img_url).centerCrop().into(mHolder.mCoverIv);

        final int mPosition = position;

        //是否主封面: 0.是 1.否
        mHolder.mCoverTv.setVisibility(coverUrlBean.t_first == 0 ? View.VISIBLE : View.GONE);

        mHolder.setBtn.setVisibility(coverUrlBean.t_first == 0 ? View.GONE : View.VISIBLE);

        mHolder.deleteBtn.setVisibility(coverUrlBean.t_first == 0 ? View.GONE : View.VISIBLE);

        mHolder.setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMainCover(coverUrlBean);
            }
        });

        mHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCover(coverUrlBean);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    /**
     * 删除封面
     */
    private void deleteCover(final CoverUrlBean coverUrlBea) {
        if (coverUrlBea.t_first == 0) {
            ToastUtil.INSTANCE.showToast(R.string.can_not_delete_main);
            return;
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("coverImgId", String.valueOf(coverUrlBea.t_id));
        OkHttpUtils.post().url(ChatApi.DEL_COVER_IMG())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build()
                .execute(new AjaxCallback<BaseResponse>() {

                    @Override
                    public void onResponse(BaseResponse response, int id) {
                        if (response != null) {
                            if (response.m_istatus == NetCode.SUCCESS) {
                                mBeans.remove(coverUrlBea);
                                notifyDataSetChanged();
                                deleted();
                            } else {
                                ToastUtil.INSTANCE.showToast(response.m_strMessage);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        ToastUtil.INSTANCE.showToast(R.string.delete_fail);
                    }

                });
    }

    protected void deleted(){

    }

    /**
     * 设为主封面
     */
    private void setMainCover(final CoverUrlBean coverUrlBean) {
        if (coverUrlBean.t_first == 0) {
            ToastUtil.INSTANCE.showToast(R.string.already_main);
            return;
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("coverImgId", String.valueOf(coverUrlBean.t_id));
        OkHttpUtils.post().url(ChatApi.SET_MAIN_COVER_IMG())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        for (CoverUrlBean mBean : mBeans) {
                            mBean.t_first = 1;
                        }
                        coverUrlBean.t_first = 0;
                        notifyDataSetChanged();
                    } else {
                        ToastUtil.INSTANCE.showToast(response.m_strMessage);
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.INSTANCE.showToast(R.string.system_error);
            }
        });
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mCoverIv;
        TextView mCoverTv;
        View deleteBtn;
        View setBtn;

        MyViewHolder(View itemView) {
            super(itemView);

            Context mContext = itemView.getContext();
            int size = (DevicesUtil.getScreenW(mContext) - DevicesUtil.dp2px(mContext, 40)) / 4;

            setBtn = itemView.findViewById(R.id.set_btn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
            mCoverIv = itemView.findViewById(R.id.cover_iv);
            mCoverTv = itemView.findViewById(R.id.cover_tv);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            params.leftMargin = DevicesUtil.dp2px(mContext, 5);
            params.rightMargin = DevicesUtil.dp2px(mContext, 5);
            itemView.setLayoutParams(params);
        }
    }

}