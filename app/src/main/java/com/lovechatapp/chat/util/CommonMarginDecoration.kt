package com.lovechatapp.chat.util

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * @param verticalOffset 垂直间距（px）
 * @param horizontalOffset 水平间距（px）
 * @param horizontalCount 每行的item数量
 */
class CommonMarginDecoration(
    private val verticalOffset: Int,
    private val horizontalOffset: Int,
    private val horizontalCount: Int,
    private val isFirst: Boolean = false
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        //设置纵向间距
        val topOffset = if (parent.getChildAdapterPosition(view) < horizontalCount) {
            if (isFirst) verticalOffset else 0
        } else {
            //除第一行外的item，需要设置纵向间距
            verticalOffset
        }
        //设置水平间距
        val horOffset = if (horizontalCount > 1) {//每行item数量大于1，则需要设置水平间距
            //每行后面的item需要设置左间距
            horizontalOffset
        } else {//每行只有一个item，则无需设置水平间距
            0
        }

        outRect.set(
            0, topOffset, if (parent.getChildLayoutPosition(view) % horizontalCount == horizontalCount - 1) {
                0
            } else {
                horOffset
            }, 0
        )
    }
}