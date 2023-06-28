package com.lovechatapp.chat.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View
import android.view.WindowManager
import com.lovechatapp.chat.R
import com.lovechatapp.chat.databinding.DialogDateTimePickerBinding
import java.util.Calendar

class DateTimePickerDialog(context: Context, private val onConfirm: (result: String) -> Unit) :
    Dialog(context) {
    private lateinit var mBinding: DialogDateTimePickerBinding
    private var mDateStr = ""
    private var mTimeStr = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DialogDateTimePickerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        window?.apply {
            val lp = attributes
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes = lp
        }
        mBinding.datePicker.minDate = Calendar.getInstance().timeInMillis
        findViewById<View>(R.id.btnCancel).setOnClickListener {
            dismiss()
        }
        findViewById<View>(R.id.btnConfirm).setOnClickListener {
            onConfirm.invoke(mDateStr.plus("  ").plus(mTimeStr))
            dismiss()
        }
        mDateStr =
            "${mBinding.datePicker.year}-${mBinding.datePicker.month + 1}-${mBinding.datePicker.dayOfMonth}"
        mTimeStr = "${mBinding.timePicker.hour}:${mBinding.timePicker.minute}"

        mBinding.datePicker.setOnDateChangedListener { _, i, i2, i3 ->
            mDateStr = "$i-${getFormatNum(i2 + 1)}-${getFormatNum(i3)}"
        }
        mBinding.timePicker.setOnTimeChangedListener { _, i, i2 ->
            mTimeStr = "${getFormatNum(i)}:${getFormatNum(i2)}"
        }
    }

    private fun getFormatNum(num: Int): String {
        return if (num < 10) {
            "0$num"
        } else {
            "$num"
        }
    }
}