package com.lovechatapp.chat.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import com.lovechatapp.chat.bean.DateGiftBean
import com.lovechatapp.chat.dialog.BottomDateGiftDialog.GiftSelectedListener
import android.os.Bundle
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.GridLayoutManager
import com.lovechatapp.chat.util.CommonMarginDecoration
import com.lovechatapp.chat.dialog.BottomDateGiftDialog.RvAdapter
import com.lovechatapp.chat.dialog.BottomDateGiftDialog.RvAdapter.GiftHolder
import com.lovechatapp.chat.helper.ImageLoadHelper
import android.support.v7.widget.AppCompatTextView
import android.view.*
import com.lovechatapp.chat.R
import java.util.ArrayList

class BottomDateGiftDialog(context: Context, dataList: ArrayList<DateGiftBean>) :
    Dialog(context, R.style.common_dialog) {
    private val mDataList = ArrayList<DateGiftBean>().apply {
        addAll(dataList)
    }
    private var listener: GiftSelectedListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val win = window
        win?.apply {
            decorView.setPadding(0, 0, 0, 0)
            val lp = attributes
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = context.resources.getDimensionPixelOffset(R.dimen.dp_374)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setWindowAnimations(R.style.listDialogWindowAnim)
            setGravity(Gravity.BOTTOM)
            attributes = lp
        }
        setContentView(R.layout.dialog_bottom_date_gift)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        initView()
    }

    private var giftIndexSelected = 0
    private fun initView() {
        val rv = findViewById<RecyclerView>(R.id.giftRv)
        rv.layoutManager = GridLayoutManager(context, 4)
        val offset = context.resources.getDimensionPixelOffset(R.dimen.dp_9)
        rv.addItemDecoration(CommonMarginDecoration(0, offset, 4, false))
        rv.adapter = RvAdapter()
    }

    fun setGiftSelectListener(listener: GiftSelectedListener?) {
        this.listener = listener
    }

    private inner class RvAdapter : RecyclerView.Adapter<GiftHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): GiftHolder {
            return GiftHolder(viewGroup)
        }

        override fun onBindViewHolder(giftHolder: GiftHolder, i: Int) {
            giftHolder.onBind(mDataList[i], i)
        }

        override fun getItemCount(): Int {
            return mDataList.size
        }

        private inner class GiftHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_item_date_gift, parent, false
            )
        ) {
            @SuppressLint("SetTextI18n")
            fun onBind(bean: DateGiftBean, position: Int) {
                ImageLoadHelper.glideShowImageWithUrl(
                    itemView.context, bean.t_gift_still_url, itemView.findViewById(
                        R.id.giftImg
                    )
                )
                val priceUnSelected = itemView.findViewById<AppCompatTextView>(R.id.giftPrice)
                val priceSelected = itemView.findViewById<AppCompatTextView>(R.id.giftPriceSend)
                val nameText = itemView.findViewById<AppCompatTextView>(R.id.giftName)
                val btnSend = itemView.findViewById<AppCompatTextView>(R.id.giftSend)
                priceUnSelected.text = "${bean.t_gift_gold}约豆"
                priceSelected.text = "${bean.t_gift_gold}约豆"
                nameText.text = bean.t_gift_name
                itemView.isSelected = bean.isSelected
                if (bean.isSelected) {
                    priceUnSelected.visibility = View.GONE
                    nameText.visibility = View.GONE
                    priceSelected.visibility = View.VISIBLE
                    btnSend.visibility = View.VISIBLE
                } else {
                    priceUnSelected.visibility = View.VISIBLE
                    nameText.visibility = View.VISIBLE
                    priceSelected.visibility = View.GONE
                    btnSend.visibility = View.GONE
                }
                itemView.setOnClickListener {
                    //选择礼物
                    if (position != giftIndexSelected) {
                        mDataList[giftIndexSelected].isSelected = false
                        notifyItemChanged(giftIndexSelected)
                        mDataList[position].isSelected = true
                        notifyItemChanged(position)
                        giftIndexSelected = position
                    }
                }
                btnSend.setOnClickListener {
                    listener?.apply {
                        listener!!.onSelected(bean)
                        dismiss()
                    }
                }
            }
        }
    }

    abstract class GiftSelectedListener {
        abstract fun onSelected(bean: DateGiftBean)
    }
}