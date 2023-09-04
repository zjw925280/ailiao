package com.lovechatapp.chat.view.recycle;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsRecycleAdapter extends RecyclerView.Adapter<ViewHolder> {

    protected Type[] mTypes;

    protected List mDatas;

    private OnItemClickListener onItemClickListener;

    public abstract void convert(ViewHolder holder, Object t);

    public AbsRecycleAdapter(Type... mTypes) {
        this.mTypes = mTypes;
    }

    public AbsRecycleAdapter(List data, Type... types) {
        mTypes = types;
        mDatas = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        ViewHolder viewHolder = ViewHolder.get(parent, viewType);
        viewHolder.setOnItemClickListener((view, object, position) -> {
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(view, object, position);
        });
        setViewHolder(viewHolder);
        return viewHolder;
    }

    public void setViewHolder(ViewHolder viewHolder) {

    }

    @Override
    public int getItemViewType(int position) {
        if (mTypes != null)
            for (Type type : mTypes) {
                Object o = mDatas.get(position);
                if (type != null && o != null)
                    if (type.clazz == mDatas.get(position).getClass()) {
                        return type.layoutId;
                    }
            }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setRealPosition(position);
        Object obj = mDatas == null ? null : mDatas.get(position);
        holder.bindData(obj);
        convert(holder, obj);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public List getData() {
        return mDatas;
    }

    public final boolean hasData() {
        return mDatas != null && mDatas.size() > 0;
    }

    public final void addData(List list) {
        if (this.mDatas == null)
            this.mDatas = new ArrayList();
        mDatas.addAll(list);
        notifyDataSetChanged();
    }

    public final void addData(Object object, boolean notify) {
        if (this.mDatas == null)
            this.mDatas = new ArrayList();
        mDatas.add(object);
        if (notify) {
            notifyDataSetChanged();
        }
    }

    public final void updateData(List list) {
        if (this.mDatas == null)
            this.mDatas = new ArrayList();
        mDatas.clear();
        if (list != null) {
            mDatas.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void setData(List data, boolean isRefresh) {
        if (isRefresh) {
            updateData(data);
        } else {
            addData(data);
        }
    }

    public void setDatas(List data) {
        this.mDatas = data;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class Type {
        int layoutId;
        Class clazz;

        public Type(int layoutId, Class clazz) {
            this.layoutId = layoutId;
            this.clazz = clazz;
        }
    }

}