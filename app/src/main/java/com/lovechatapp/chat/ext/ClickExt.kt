package com.lovechatapp.chat.ext

import android.support.v7.widget.AppCompatEditText
import android.text.TextWatcher
import android.view.View
import com.lovechatapp.chat.util.ClickControlUtil

fun View.setClick(onClick: (view: View) -> Unit) {
    this.setOnClickListener {
        if (ClickControlUtil.getInstance().isClickable()) {
            onClick.invoke(it)
        }
    }
}

fun setAllClick(vararg views: View, onClick: (view: View) -> Unit) {
    views.forEach {
        it.setOnClickListener { view ->
            if (ClickControlUtil.getInstance().isClickable()) {
                onClick.invoke(view)
            }
        }
    }
}

fun addTextChangeListenerForViews(
    editTexts: ArrayList<AppCompatEditText>, listener: TextWatcher) {
    editTexts.forEach {
        it.addTextChangedListener(listener)
    }
}