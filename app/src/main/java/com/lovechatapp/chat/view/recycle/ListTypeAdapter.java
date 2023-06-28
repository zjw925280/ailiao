package com.lovechatapp.chat.view.recycle;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ListTypeAdapter extends RecyclerView.Adapter<ViewHolder> {

    protected BindViewHolder[] bindViewHolders;
    protected List<BindViewHolder> bindViewHolderList;

    public ListTypeAdapter(BindViewHolder... bindViewHolders) {

        this.bindViewHolders = bindViewHolders;
        bindViewHolderList = new ArrayList<>();

        for (BindViewHolder bindViewHolder : this.bindViewHolders) {
            if (bindViewHolder == null)
                continue;
            bindViewHolderList.add(bindViewHolder);
            bindViewHolder.setCommonAdapter(this);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        BindViewHolder typeView = bindViewHolderList.get(viewType);
        return typeView.createViewHolder(parent, typeView.layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        int itemCount = -1;
        int index = 0;

        for (BindViewHolder item : bindViewHolderList) {
            int itemSize = item.getSize();

            //item有数据才判定type
            if (itemSize > 0) {
                itemCount += itemSize;
                if (position <= itemCount) {
                    return index;
                }
            }
            index++;
        }
        return 0;
    }

    /**
     * 返回相应viewbinder数据源的下标
     */
    private int getRealPosition(int position) {
        int totalSize = 0;
        for (BindViewHolder item : bindViewHolderList) {
            int itemSize = item.getSize();
            if (itemSize > 0) {
                int include = totalSize + itemSize;
                if (position == include)
                    return 0;
                if (position < include) {
                    return position - totalSize;
                }
                totalSize += itemSize;
            }
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BindViewHolder bindViewHolder = getBindViewHolder(position);
        int realPosition = getRealPosition(position);
        holder.setRealPosition(realPosition);
        holder.bindData(bindViewHolder.asItem ? bindViewHolder.data.get(realPosition) : bindViewHolder.itemData);
        bindViewHolder.bindViewHolder(holder, bindViewHolder.asItem ? bindViewHolder.data.get(realPosition) : bindViewHolder.itemData);
    }

    private BindViewHolder getBindViewHolder(int position) {
        int index = getItemViewType(position);
        return bindViewHolderList.get(index);
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        for (BindViewHolder item : bindViewHolderList) {
            itemCount += item.getSize();
        }
        return itemCount;
    }

    public static class BindViewHolder {

        //item列表数据
        protected List data;

        Object itemData;

        //item布局Id
        int layoutId;

        //是否把item列表数据都交给recycle处理，否则ViewHolder自己处理
        private boolean asItem;

        private ListTypeAdapter commonAdapter;

        public BindViewHolder(List data, int layoutId, boolean asItem) {
            this.data = data;
            this.layoutId = layoutId;
            this.asItem = asItem;
        }

        public BindViewHolder(int layoutId) {
            this.layoutId = layoutId;
            this.data = new ArrayList();
        }

        public BindViewHolder(int layoutId, boolean asItem) {
            this.layoutId = layoutId;
            this.asItem = asItem;
        }

        public int getSize() {
            if (!asItem)
                return 1;
            if (data != null && data.size() > 0) {
                if (asItem) {
                    return data.size();
                }
            }
            return 0;
        }

        public void setAsItem(boolean asItem) {
            this.asItem = asItem;
        }

        public void setCommonAdapter(ListTypeAdapter commonAdapter) {
            this.commonAdapter = commonAdapter;
        }

        public ListTypeAdapter getCommonAdapter() {
            return commonAdapter;
        }

        public void setItemData(Object itemData) {
            this.itemData = itemData;
        }

        public void setData(List data) {
            this.data = data;
            if (commonAdapter != null)
                commonAdapter.notifyDataSetChanged();
        }

        public void setData(List data, boolean isRefresh) {
            if (this.data == null) {
                this.data = new ArrayList();
            }
            if (isRefresh)
                this.data.clear();
            if (data != null)
                this.data.addAll(data);
            if (commonAdapter != null)
                commonAdapter.notifyDataSetChanged();
        }

        public List getData() {
            return data;
        }

        public ViewHolder createViewHolder(ViewGroup parent, int layoutId) {
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            initViewHolder(viewHolder);
            return viewHolder;
        }

        protected void initViewHolder(ViewHolder viewHolder) {

        }

        protected void bindViewHolder(ViewHolder viewHolder, Object obj) {

        }

    }
}