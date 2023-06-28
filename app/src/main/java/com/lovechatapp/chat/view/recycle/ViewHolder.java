package com.lovechatapp.chat.view.recycle;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;
    private Object object;

    public ViewHolder(View itemView) {
        super(itemView);
        mConvertView = itemView;
        mViews = new SparseArray<>();
        mConvertView.setTag(this);
    }

    public Object getObject() {
        return object;
    }

    public static ViewHolder get(ViewGroup parent, int layoutId) {
        return new ViewHolder(inflate(parent, layoutId));
    }

    public static View inflate(ViewGroup parent, int layoutId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }

    public <V extends View> V getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (V) view;
    }

    public View getConvertView() {
        return mConvertView;
    }


    public int getRealPosition() {
        return mPosition;
    }


    public void setRealPosition(int position) {
        mPosition = position;
    }

    public final void bindData(Object o) {
        this.object = o;
        convert(this, o);
    }

    public void convert(ViewHolder holder, Object t) {

    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        mConvertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, object, getRealPosition());
            }
        });
    }
}