package com.lovechatapp.chat.util;

import android.app.Dialog;
import android.content.Context;

import com.lovechatapp.chat.R;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：Dialog工具类
 * 作者：
 * 创建时间：2018/6/14
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class DialogUtil {

    /**
     * 进度条
     */
    public static Dialog showLoadingDialog(Context context) {
        Dialog dialog = new Dialog(context, R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_progress_loading);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

}
