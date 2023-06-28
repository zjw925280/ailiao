package com.lovechatapp.chat.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.lovechatapp.chat.base.AppManager
import java.lang.Exception
import java.lang.reflect.Field

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：Toast工具类
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
@SuppressLint("DiscouragedPrivateApi")
object ToastUtil {

    fun showToast(text: String?) {
        doToast(context = AppManager.getInstance(), message = text)
    }

    fun showToast(resourceId: Int) {
        doToast(context = AppManager.getInstance(), resId = resourceId)
    }

    fun showToast(context: Context? = AppManager.getInstance().baseContext, text: String?) {
        doToast(context = context, message = text)
    }

    fun showToast(context: Context? = AppManager.getInstance().baseContext, resourceId: Int) {
        doToast(context = context, resId = resourceId)
    }

    fun showToastLong(context: Context?, resourceId: Int) {
        doToast(context = context, resId = resourceId, duration = Toast.LENGTH_LONG)
    }

    fun showToastLong(context: Context?, text: String?) {
        doToast(context = context, message = text, duration = Toast.LENGTH_LONG)
    }


    private var toast: Toast? = null

    fun doToast(
        context: Context? = AppManager.getInstance(),
        resId: Int = 0,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        val message = if (resId > 0) {
            AppManager.getInstance().resources.getString(resId)
        } else {
            "未知消息"
        }
        doToast(context, message, duration)
    }

    fun doToast(
        context: Context? = AppManager.getInstance(),
        message: String? = "",
        duration: Int = Toast.LENGTH_SHORT
    ) {
        toast?.apply {
            val curToast = getTnView(this)
            curToast?.apply {
                toast!!.setText(message)
            } ?: apply {
                toast!!.setText(message)
                toast!!.duration = duration
                show()
            }
        } ?: apply {
            init()
            context?.let {
                toast = Toast.makeText(context, "", duration).apply {
                    hook(this)
                    setText(message)
                    setGravity(Gravity.BOTTOM, 0, 0)
                    show()

                }

            }
        }
    }

    private var sField_TN: Field? = null
    private var sField_TN_Handler: Field? = null

    private fun init() {
        try {
            sField_TN = Toast::class.java.getDeclaredField("mTN")
            sField_TN?.isAccessible = true
            sField_TN_Handler = sField_TN?.type?.getDeclaredField("mHandler")
            sField_TN_Handler?.isAccessible = true
        } catch (e: Exception) {
        }
    }

    private fun getTnView(toast: Toast): View? {
        val viewField =
            sField_TN?.type?.getDeclaredField("mView").also { it?.isAccessible = true }
        val tn = sField_TN?.get(toast)
        return viewField?.get(tn) as? View
    }

    private fun hook(toast: Toast) {
        try {
            val tn = sField_TN?.get(toast)
            val preHandler = sField_TN_Handler?.get(tn) as Handler
            sField_TN_Handler?.set(tn, SafelyHandlerWrapper(preHandler))
        } catch (e: Exception) {
        }

    }

    private class SafelyHandlerWrapper(private val impl: Handler) : Handler() {

        override fun dispatchMessage(msg: Message) {
            try {
                super.dispatchMessage(msg)
            } catch (e: Exception) {
            }
        }

        override fun handleMessage(msg: Message) {
            impl.handleMessage(msg)//需要委托给原Handler执行
        }
    }
}