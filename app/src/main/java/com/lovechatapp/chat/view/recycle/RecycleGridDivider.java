package com.lovechatapp.chat.view.recycle;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * recycleview 分割线
 */
public class RecycleGridDivider extends RecyclerView.ItemDecoration {

    private int space;

    public RecycleGridDivider(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager manager = (GridLayoutManager) parent.getLayoutManager();

        //为了Item大小均匀，将设定分割线平均分给左右两边Item各一半
        int offset = space / 2;

        //根据view获得position
        int position = parent.getChildAdapterPosition(view);

        //一个item占用的spanCount大小
        int itemSpanCount = manager.getSpanSizeLookup().getSpanSize(position);
        int spanCount = manager.getSpanCount();

        //一个item占用一行
        if (itemSpanCount == spanCount) {
            if (manager.getItemViewType(view) != 0) {
                outRect.set(space, space, space, manager.getItemCount() == position ? space : 0);
            }
        } else {

            //每行中的第几个item
            int indexOfSpan = manager.getSpanSizeLookup().getSpanIndex(position, spanCount);

            //左边
            if (indexOfSpan % spanCount == 0) {
                outRect.set(space, space, offset, 0);
            }
            //右边
            else if (indexOfSpan % spanCount == spanCount - 1) {
                outRect.set(offset, space, space, 0);
            }
            //中间
            else {
                outRect.set(offset, space, offset, 0);
            }
        }
    }
}