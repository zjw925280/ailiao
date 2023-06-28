package com.lovechatapp.chat.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import com.lovechatapp.chat.R
import com.lovechatapp.chat.databinding.DialogDateCodeInputBinding
import com.lovechatapp.chat.ext.addTextChangeListenerForViews
import com.lovechatapp.chat.ext.setClick

/**
 * 约会暗语输入弹框
 */
class DateCodeInputDialog(
    context: Context,
    private val onConfirmListener: ((code: String) -> Unit)?
) :
    Dialog(context) {
    /**视图绑定器*/
    private lateinit var mBinding: DialogDateCodeInputBinding

    /**暗语输入控件列表*/
    private val mCodeViewList = ArrayList<AppCompatEditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //初始化数据绑定器
        mBinding = DialogDateCodeInputBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        //初始化暗语输入控件列表
        mCodeViewList.add(mBinding.codeEdt1)
        mCodeViewList.add(mBinding.codeEdt2)
        mCodeViewList.add(mBinding.codeEdt3)
        mCodeViewList.add(mBinding.codeEdt4)
        window?.apply {
            //设置界面属性
            val lp = attributes
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.horizontalMargin = context.resources.getDimension(R.dimen.dp_12)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes = lp
            clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }
        autoChangeFocus()
        addTextChangeListenerForViews(mCodeViewList, object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                autoChangeFocus()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        mBinding.btnConfirm.setClick {
            val result = checkInput()
            if (result.isNotEmpty()) {
                //请求接口确认暗语是否正确
                onConfirmListener?.invoke(result)
                dismiss()
            }
        }
    }

    /**自动切换输入框的焦点*/
    private fun autoChangeFocus() {
        var isSetFocus = false
        mCodeViewList.forEach {
            if (it.text.toString().trim().isNotEmpty()) {
                it.clearFocus()
            } else {
                if (!isSetFocus) {
                    isSetFocus = true
                    it.requestFocus()
                }
            }
        }
    }

    /***/
    private fun checkInput(): String {
        var result = ""
        var isBlank = false
        mCodeViewList.forEach {
            if (it.text.toString().trim().isNotEmpty()) {
                result = result.plus(it.text.toString().trim())
            } else {
                isBlank = true
                return@forEach
            }
        }
        return if (isBlank) "" else result
    }
}