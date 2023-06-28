package com.lovechatapp.chat.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import com.lovechatapp.chat.activity.ServeListActivity;
import com.lovechatapp.chat.helper.SharedPreferenceHelper;

import java.util.List;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：转码工具
 * 作者：
 * 创建时间：2018/7/12
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class CodeUtil {

    /**
     * 跳转到QQ
     */
    public static void jumpToQQ(Activity mContext) {
        Intent intent = new Intent(mContext, ServeListActivity.class);
        mContext.startActivity(intent);
//        String data = SharedPreferenceHelper.getQQ(mContext.getApplicationContext());
//        if (TextUtils.isEmpty(data)) {
//            return;
//        }
//
//        if (data.startsWith("http")) {
//            CommonWebViewActivity.start(mContext, "客服", data);
//        } else {
//            // 跳转之前，可以先判断手机是否安装QQ
//            if (isQQClientAvailable(mContext)) {
//                // 跳转到客服的QQ
//                String mQQNumber = SharedPreferenceHelper.getQQ(mContext.getApplicationContext());
//                if (TextUtils.isEmpty(mQQNumber)) {
//                    ToastUtil.INSTANCE.showToast(mContext.getApplicationContext(), R.string.qq_not_exist);
//                    return;
//                }
//                String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + mQQNumber;
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                // 跳转前先判断Uri是否存在，如果打开一个不存在的Uri，App可能会崩溃
//                if (isValidIntent(mContext, intent)) {
//                    mContext.startActivity(intent);
//                } else {
//                    ToastUtil.INSTANCE.showToast(mContext, R.string.system_error);
//                }
//            } else {
//                ToastUtil.INSTANCE.showToast(mContext, R.string.not_install_qq);
//            }
//        }
    }


    /**
     * 判断 用户是否安装QQ客户端
     */
    private static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase("com.tencent.qqlite")
                        || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断 Uri是否有效
     */
    private static boolean isValidIntent(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        return !activities.isEmpty();
    }

    public static String getClipBoardContent(Context context) {
        try {
            //如果本地有
            String saveUserId = SharedPreferenceHelper.getShareId(context);
            if (!TextUtils.isEmpty(saveUserId) && !saveUserId.equals("0")) {
                return saveUserId;
            }
            // 获取系统剪贴板
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 获取剪贴板的剪贴数据集
            if (clipboard != null) {
                ClipData clipData = clipboard.getPrimaryClip();
                if (clipData != null && clipData.getItemCount() > 0) {
                    // 从数据集中获取（粘贴）第一条文本数据
                    ClipData.Item item = clipData.getItemAt(0);
                    if (item != null) {
                        CharSequence charSequence = item.getText();
                        if (!TextUtils.isEmpty(charSequence)) {
                            String str = charSequence.toString().trim();
                            if (str.contains("&")) {
                                str = str.substring(0, str.indexOf("&"));
                            }
                            System.out.println("text: " + str);
                            //kuailiao:userId=12654545
                            if (str.startsWith("chat:userId=")) {
                                String[] content = str.split("=");
                                if (content.length > 0) {
                                    String id = content[1];
                                    if (!TextUtils.isEmpty(id)) {
                                        return id;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    public static String getClipBoardContentOne(Context context) {
        try {
            // 获取系统剪贴板
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 获取剪贴板的剪贴数据集
            if (clipboard != null) {
                ClipData clipData = clipboard.getPrimaryClip();
                if (clipData != null && clipData.getItemCount() > 0) {
                    // 从数据集中获取（粘贴）第一条文本数据
                    ClipData.Item item = clipData.getItemAt(0);
                    if (item != null) {
                        CharSequence charSequence = item.getText();
                        if (!TextUtils.isEmpty(charSequence)) {
                            String str = charSequence.toString().trim();
                            if (str.contains("&")) {
                                str = str.substring(0, str.indexOf("&"));
                            }
                            System.out.println("text: " + str);
                            //kuailiao:userId=12654545
                            if (str.startsWith("chat:userId=")) {
                                String[] content = str.split("=");
                                if (content.length > 0) {
                                    String id = content[1];
                                    if (!TextUtils.isEmpty(id)) {
                                        return id;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    public static void clearClipBoard(Context context) {
        SharedPreferenceHelper.saveShareId(context, "");
        // 获取系统剪贴板
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
        ClipData clipData = ClipData.newPlainText(null, "");
        // 把数据集设置（复制）到剪贴板
        if (clipboard != null && clipData != null) {
            clipboard.setPrimaryClip(clipData);
        }
    }

}
